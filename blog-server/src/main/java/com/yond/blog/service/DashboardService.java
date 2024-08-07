package com.yond.blog.service;

import com.yond.blog.entity.CityVisitorDO;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    int countVisitLogByToday();

    int getBlogCount();

    int getCommentCount();

    Map<String, List> getCategoryBlogCountMap();

    Map<String, List> getTagBlogCountMap();

    Map<String, List> getVisitRecordMap();

    List<CityVisitorDO> getCityVisitorList();
}
