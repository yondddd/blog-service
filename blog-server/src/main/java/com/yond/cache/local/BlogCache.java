package com.yond.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.yond.model.vo.BlogInfo;
import com.yond.model.vo.PageResult;

import java.util.Map;

/**
 * @author yond
 * @date 6/22/2024
 * @description blog cache
 */
public class BlogCache {

    // 文章归档缓存

    private final static String BLOG_GROUP_KEY = "blogGroup";

    private final static Cache<String, Map<String, Object>> cache = LocalCache.buildCache(1);

    public static Map<String, Object> getBlogGroup() {
        return cache.getIfPresent(BLOG_GROUP_KEY);
    }

    public static void setBlogGroup(Map<String, Object> blogGroup) {
        cache.put(BLOG_GROUP_KEY, blogGroup);
    }

    public static void delBlogGroup() {
        cache.invalidate(BLOG_GROUP_KEY);
    }


    // 按页缓存首页文章

    private final static String BLOG_INFO_KEY = "blogInfo";

    private final static Cache<String, PageResult<BlogInfo>> infoCache = LocalCache.buildCache(1000);


    public static void delInfo() {
        infoCache.invalidateAll();
    }

    public static PageResult<BlogInfo> getInfoByPage(int page) {
        return infoCache.getIfPresent(buildInfoKey(page));
    }

    public static void setInfoByPage(int page, PageResult<BlogInfo> data) {
        infoCache.put(buildInfoKey(page), data);
    }

    public static String buildInfoKey(int page) {
        return BLOG_INFO_KEY + page;
    }

}