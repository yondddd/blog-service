package com.yond.blog.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.yond.blog.entity.SiteSettingDO;

import java.util.List;

/**
 * @author yond
 * @date 6/22/2024
 * @description site setting cache
 */
public class SiteSettingCache {

    private final static String KEY = "allSiteSetting";

    private final static Cache<String, List<SiteSettingDO>> cache = LocalCache.buildCache(1);

    public static List<SiteSettingDO> get() {
        return cache.getIfPresent(KEY);
    }

    public static void set(List<SiteSettingDO> data) {
        cache.put(KEY, data);
    }

    public static void del() {
        cache.invalidate(KEY);
    }

}
