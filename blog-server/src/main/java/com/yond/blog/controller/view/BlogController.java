package com.yond.blog.controller.view;

import com.yond.blog.entity.User;
import com.yond.blog.model.dto.BlogPassword;
import com.yond.blog.model.vo.BlogDetail;
import com.yond.blog.model.vo.BlogInfo;
import com.yond.blog.model.vo.PageResult;
import com.yond.blog.model.vo.SearchBlog;
import com.yond.blog.service.BlogService;
import com.yond.blog.service.impl.UserServiceImpl;
import com.yond.blog.util.JwtUtils;
import com.yond.blog.util.MyStringUtils;
import com.yond.common.annotation.VisitLogger;
import com.yond.common.constant.JwtConstants;
import com.yond.common.enums.VisitBehavior;
import com.yond.common.resp.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 博客相关
 * @Author: Naccl
 * @Date: 2020-08-12
 */
@RestController
public class BlogController {

    private final BlogService blogService;
    private final UserServiceImpl userService;

    public BlogController(BlogService blogService, UserServiceImpl userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    /**
     * 按置顶、创建时间排序 分页查询博客简要信息列表
     *
     * @param pageNum 页码
     */
    @VisitLogger(VisitBehavior.INDEX)
    @GetMapping("/view/blogs")
    public Result<PageResult<BlogInfo>> blogs(@RequestParam(defaultValue = "1") Integer pageNum) {
        PageResult<BlogInfo> pageResult = blogService.getBlogInfoListByIsPublished(pageNum);
        return Result.success(pageResult);
    }

    /**
     * 按id获取公开博客详情
     *
     * @param id  博客id
     * @param jwt 密码保护文章的访问Token
     * @return
     */
    @VisitLogger(VisitBehavior.BLOG)
    @GetMapping("/view/blog")
    public Result getBlog(@RequestParam Long id,
                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
        BlogDetail blog = blogService.getBlogByIdAndIsPublished(id);
        //对密码保护的文章校验Token
        if (!"".equals(blog.getPassword())) {
            if (JwtUtils.judgeTokenIsExist(jwt)) {
                try {
                    String subject = JwtUtils.getTokenBody(jwt).getSubject();
                    if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
                        //博主身份Token
                        String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
                        User admin = (User) userService.loadUserByUsername(username);
                        if (admin == null) {
                            return Result.create(403, "博主身份Token已失效，请重新登录！");
                        }
                    } else {
                        //经密码验证后的Token
                        Long tokenBlogId = Long.parseLong(subject);
                        //博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
                        if (!tokenBlogId.equals(id)) {
                            return Result.create(403, "Token不匹配，请重新验证密码！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.create(403, "Token已失效，请重新验证密码！");
                }
            } else {
                return Result.create(403, "此文章受密码保护，请验证密码！");
            }
            blog.setPassword("");
        }
        blogService.updateViewsToRedis(id);
        return Result.ok("获取成功", blog);
    }

    /**
     * 校验受保护文章密码是否正确，正确则返回jwt
     *
     * @param blogPassword 博客id、密码
     * @return
     */
    @VisitLogger(VisitBehavior.CHECK_PASSWORD)
    @PostMapping("/view/checkBlogPassword")
    public Result checkBlogPassword(@RequestBody BlogPassword blogPassword) {
        String password = blogService.getBlogPassword(blogPassword.getBlogId());
        if (password.equals(blogPassword.getPassword())) {
            //生成有效时间一个月的Token
            String jwt = JwtUtils.generateToken(blogPassword.getBlogId().toString(), 1000 * 3600 * 24 * 30L);
            return Result.ok("密码正确", jwt);
        } else {
            return Result.create(403, "密码错误");
        }
    }

    /**
     * 按关键字根据文章内容搜索公开且无密码保护的博客文章
     *
     * @param query 关键字字符串
     * @return
     */
    @VisitLogger(VisitBehavior.SEARCH)
    @GetMapping("/view/searchBlog")
    public Result searchBlog(@RequestParam String query) {
        //校验关键字字符串合法性
        if (StringUtils.isBlank(query) || MyStringUtils.hasSpecialChar(query) || query.trim().length() > 20) {
            return Result.failure("参数错误");
        }
        List<SearchBlog> searchBlogs = blogService.getSearchBlogListByQueryAndIsPublished(query.trim());
        return Result.ok("获取成功", searchBlogs);
    }
}
