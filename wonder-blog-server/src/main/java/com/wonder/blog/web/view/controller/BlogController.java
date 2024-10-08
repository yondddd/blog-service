package com.wonder.blog.web.view.controller;

import com.wonder.blog.entity.BlogDO;
import com.wonder.blog.entity.CategoryDO;
import com.wonder.blog.entity.TagDO;
import com.wonder.blog.service.BlogService;
import com.wonder.blog.service.BlogTagService;
import com.wonder.blog.service.CategoryService;
import com.wonder.blog.util.jwt.JwtUtil;
import com.wonder.blog.util.jwt.PayloadHelper;
import com.wonder.blog.web.view.async.BlogViewFlush;
import com.wonder.blog.web.view.convert.BlogViewConverter;
import com.wonder.blog.web.view.req.BlogCheckReq;
import com.wonder.blog.web.view.req.BlogDetailReq;
import com.wonder.blog.web.view.req.BlogPageReq;
import com.wonder.blog.web.view.req.BlogSearchReq;
import com.wonder.blog.web.view.vo.BlogDetailVO;
import com.wonder.blog.web.view.vo.BlogVO;
import com.wonder.blog.web.view.vo.SearchBlogVO;
import com.wonder.common.annotation.VisitLogger;
import com.wonder.common.constant.JwtConstant;
import com.wonder.common.enums.VisitBehavior;
import com.wonder.common.resp.PageResponse;
import com.wonder.common.resp.Response;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 博客相关
 * @Author: Yond
 */
@RestController
@RequestMapping("/view/blog")
public class BlogController {

    @Resource
    private BlogService blogService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private BlogTagService blogTagService;
    @Resource
    private BlogViewFlush blogViewFlush;

    @VisitLogger(VisitBehavior.INDEX)
    @PostMapping("/page")
    public PageResponse<List<BlogVO>> page(@RequestBody BlogPageReq req) {

        Pair<Integer, List<BlogDO>> pair = blogService.viewPageBy(req.getCategoryId(), req.getTagId(), req.getPageNo(), req.getPageSize());
        List<Long> blogIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (BlogDO blogDO : pair.getRight()) {
            blogIds.add(blogDO.getId());
            categoryIds.add(blogDO.getCategoryId());
        }
        Map<Long, CategoryDO> categoryMap = categoryService.listByIds(categoryIds).stream().collect(Collectors.toMap(CategoryDO::getId, Function.identity()));
        Map<Long, List<TagDO>> blogTagMap = blogTagService.listTagsByBlogIds(blogIds);
        List<BlogVO> data = BlogViewConverter.do2vo(pair.getRight(), categoryMap, blogTagMap);
        return PageResponse.<List<BlogVO>>custom().setData(data).setPageNo(req.getPageNo()).setPageSize(req.getPageSize()).setTotal(pair.getLeft()).setSuccess();
    }

    @VisitLogger(VisitBehavior.BLOG)
    @PostMapping("/detail")
    public Response<BlogDetailVO> getBlog(@RequestBody BlogDetailReq req,
                                          @RequestHeader(value = JwtConstant.TOKEN_HEADER, defaultValue = "") String jwt) {

        BlogDO blog = blogService.getBlogById(req.getId());
        Assert.notNull(blog, "博客不存在" + req.getId());
        Assert.isTrue(blog.getPublished(), "博客不公开" + req.getId());
        if (StringUtils.isNotBlank(blog.getPassword())) {
            String err = this.checkBlogView(jwt, req.getId());
            if (StringUtils.isNotBlank(err)) {
                return Response.custom(403, err);
            }
        }
        CategoryDO category = categoryService.getById(blog.getId());
        List<TagDO> tags = blogTagService.listTagsByBlogId(req.getId());
        blogViewFlush.blogViewIncr(req.getId());
        return Response.success(BlogViewConverter.do2detail(blog, category, tags));
    }


    @VisitLogger(VisitBehavior.CHECK_PASSWORD)
    @PostMapping("/checkPassword")
    public Response<String> checkBlogPassword(@RequestBody BlogCheckReq req) {
        String password = blogService.getBlogById(req.getBlogId()).getPassword();
        if (!password.equals(req.getPassword())) {
            return Response.custom(403, "密码错误");
        }
        // 生成有效时间一个月的Token
        PayloadHelper payloadHelper = new PayloadHelper()
                .setIssuer(JwtConstant.DEFAULT_CLIENT)
                .setIssuedAt(new Date())
                .setSubject(req.getBlogId().toString())
                .setSecret(JwtConstant.DEFAULT_SECRET)
                .setAdditionalInfo(new HashMap<>());
        String token = JwtUtil.creatToken(payloadHelper, JwtConstant.ONE_MONTH_TIME);

        return Response.success(token);
    }

    @VisitLogger(VisitBehavior.SEARCH)
    @PostMapping("/search")
    public Response<List<SearchBlogVO>> searchBlog(@RequestBody BlogSearchReq req) {
        String keyword = req.getKeyword();
        if (StringUtils.isBlank(keyword) || hasSpecialChar(keyword) || keyword.trim().length() > 20) {
            return Response.fail("参数错误");
        }
        List<SearchBlogVO> searchBlogVOS = blogService.searchPublic(keyword.trim());
        return Response.success(searchBlogVOS);
    }

    public boolean hasSpecialChar(String... str) {
        for (String s : str) {
            if (s.contains("%") || s.contains("_") || s.contains("[") || s.contains("#") || s.contains("*")) {
                return true;
            }
        }
        return false;
    }

    private String checkBlogView(String jwt, Long blogId) {
        if (!JwtUtil.judgeTokenIsExist(jwt)) {
            return "此文章受密码保护，请验证密码！";
        }
        try {
            Claims claims = JwtUtil.validateJwt(jwt, JwtConstant.DEFAULT_SECRET);
            String subject = claims.getSubject();
            if (subject.startsWith(JwtConstant.ADMIN_PREFIX)) {
                // 博主身份Token,所有都可以看
                if (claims.getExpiration().before(new Date())) {
                    return "博主身份Token已失效，请重新登录！";
                }
            } else {
                Long tokenBlogId = Long.parseLong(subject);
                if (!tokenBlogId.equals(blogId)) {
                    return "Token不匹配，请重新验证密码！";
                }
            }
        } catch (Exception e) {
            return "Token已失效，请重新验证密码！";
        }
        return null;
    }

}
