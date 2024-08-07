package com.yond.blog.web.blog.view.controller;

import com.github.pagehelper.PageInfo;
import com.yond.blog.entity.MomentDO;
import com.yond.blog.service.MomentService;
import com.yond.blog.service.impl.UserServiceImpl;
import com.yond.blog.util.jwt.JwtUtil;
import com.yond.blog.web.blog.view.vo.PageResult;
import com.yond.common.annotation.AccessLimit;
import com.yond.common.annotation.VisitLogger;
import com.yond.common.constant.JwtConstant;
import com.yond.common.enums.VisitBehavior;
import com.yond.common.resp.Response;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Description: 动态
 * @Author: Naccl
 * @Date: 2020-08-25
 */
@RestController
public class MomentController {
    @Autowired
    MomentService momentService;
    @Autowired
    UserServiceImpl userService;

    /**
     * 分页查询动态List
     *
     * @param pageNum 页码
     * @param jwt     博主访问Token
     * @return
     */
    @VisitLogger(VisitBehavior.MOMENT)
    @GetMapping("/view/moments")
    public Response<PageResult<MomentDO>> moments(@RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestHeader(value = JwtConstant.TOKEN_HEADER, defaultValue = "") String jwt) {
        boolean adminIdentity = false;
        if (JwtUtil.judgeTokenIsExist(jwt)) {
            try {
                Claims claims = JwtUtil.validateJwt(jwt, JwtConstant.DEFAULT_SECRET);
                String subject = claims.getSubject();
                if (subject.startsWith(JwtConstant.ADMIN_PREFIX)) {
                    //博主身份Token
                    if (claims.getExpiration().after(new Date())) {
                        adminIdentity = true;
                    }
                }
            } catch (Exception e) {
                return Response.custom(403, "博主身份Token已失效，请重新登录！");
            }
        }
        PageInfo<MomentDO> pageInfo = new PageInfo<>(momentService.getMomentVOList(pageNum, adminIdentity));
        PageResult<MomentDO> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
        return Response.success(pageResult);
    }

    /**
     * 给动态点赞
     * 简单限制一下点赞
     *
     * @param id 动态id
     * @return
     */
    @AccessLimit(seconds = 86400, maxCount = 1, msg = "不可以重复点赞哦")
    @VisitLogger(VisitBehavior.LIKE_MOMENT)
    @PostMapping("/view/moment/like/{id}")
    public Response like(@PathVariable Long id) {
        momentService.addLikeByMomentId(id);
        return Response.ok("点赞成功");
    }
}
