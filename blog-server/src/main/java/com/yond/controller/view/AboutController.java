package com.yond.controller.view;

import com.yond.common.annotation.VisitLogger;
import com.yond.common.enums.VisitBehavior;
import com.yond.common.resp.Result;
import com.yond.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 关于我页面
 * @Author: Naccl
 * @Date: 2020-08-31
 */
@RestController
public class AboutController {
    @Autowired
    AboutService aboutService;

    /**
     * 获取关于我页面信息
     *
     * @return
     */
    @VisitLogger(VisitBehavior.ABOUT)
    @GetMapping("/about")
    public Result about() {
        return Result.ok("获取成功", aboutService.getAboutInfo());
    }
}
