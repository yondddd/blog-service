package com.wonder.blog.cache.local;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * @Author Yond
 */
public class IpCache {

    private final static String KEY = "ip";

    private final static Cache<String, String> cache = LocalCache.buildCache(500, 24 * 60 * 60);


    public static String get(String ip) {
        return cache.getIfPresent(getKey(ip));
    }

    public static void set(String ip, String value) {
        cache.put(getKey(ip), value);
    }

    private static String getKey(String ip) {
        return KEY + ip;
    }

}
