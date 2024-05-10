package com.yond.controller.view;

import com.github.pagehelper.PageInfo;
import com.yond.common.annotation.AccessLimit;
import com.yond.common.annotation.VisitLogger;
import com.yond.common.constant.JwtConstants;
import com.yond.common.enums.VisitBehavior;
import com.yond.common.resp.Result;
import com.yond.entity.Moment;
import com.yond.entity.User;
import com.yond.model.vo.PageResult;
import com.yond.service.MomentService;
import com.yond.service.impl.UserServiceImpl;
import com.yond.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/moments")
    public Result moments(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
        boolean adminIdentity = false;
        if (JwtUtils.judgeTokenIsExist(jwt)) {
            try {
                String subject = JwtUtils.getTokenBody(jwt).getSubject();
                if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
                    //博主身份Token
                    String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
                    User admin = (User) userService.loadUserByUsername(username);
                    if (admin != null) {
                        adminIdentity = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentVOList(pageNum, adminIdentity));
        PageResult<Moment> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
        return Result.ok("获取成功", pageResult);
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
    @PostMapping("/moment/like/{id}")
    public Result like(@PathVariable Long id) {
        momentService.addLikeByMomentId(id);
        return Result.ok("点赞成功");
    }
}
