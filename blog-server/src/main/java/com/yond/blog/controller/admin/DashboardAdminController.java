package com.yond.blog.controller.admin;

import com.yond.blog.cache.remote.VisitCache;
import com.yond.common.resp.Result;
import com.yond.blog.entity.CityVisitorDO;
import com.yond.blog.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 后台管理仪表盘
 * @Author: Naccl
 * @Date: 2020-10-08
 */
@RestController
@RequestMapping("/admin")
public class DashboardAdminController {
    @Autowired
    DashboardService dashboardService;
    @Autowired
    VisitCache visitCache;

    @GetMapping("/dashboard")
    public Result dashboard() {
        int todayPV = dashboardService.countVisitLogByToday();
        int todayUV = visitCache.identitySize();
        int blogCount = dashboardService.getBlogCount();
        int commentCount = dashboardService.getCommentCount();
        Map<String, List> categoryBlogCountMap = dashboardService.getCategoryBlogCountMap();
        Map<String, List> tagBlogCountMap = dashboardService.getTagBlogCountMap();
        Map<String, List> visitRecordMap = dashboardService.getVisitRecordMap();
        List<CityVisitorDO> cityVisitorList = dashboardService.getCityVisitorList();

        Map<String, Object> map = new HashMap<>(16);
        map.put("pv", todayPV);
        map.put("uv", todayUV);
        map.put("blogCount", blogCount);
        map.put("commentCount", commentCount);
        map.put("category", categoryBlogCountMap);
        map.put("tag", tagBlogCountMap);
        map.put("visitRecord", visitRecordMap);
        map.put("cityVisitor", cityVisitorList);
        return Result.ok("获取成功", map);
    }
}
