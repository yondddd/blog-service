package com.yond.blog.web.interceptor;

import com.yond.blog.cache.remote.AccessLimitCache;
import com.yond.blog.util.IpAddressUtils;
import com.yond.blog.util.web.WebFilterUtil;
import com.yond.common.annotation.AccessLimit;
import com.yond.common.resp.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description: 访问控制拦截器
 * @Author: Naccl
 * @Date: 2021-04-04
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    private final AccessLimitCache accessLimitCache;

    public AccessLimitInterceptor(AccessLimitCache accessLimitCache) {
        this.accessLimitCache = accessLimitCache;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean process = handler instanceof HandlerMethod;
        if (!process) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
        if (accessLimit == null) {
            return true;
        }
        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        String ip = IpAddressUtils.getIpAddress(request);
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String key = ip + ":" + method + ":" + requestURI;
        Integer count = accessLimitCache.getCount(key);
        if (count == null) {
            accessLimitCache.incrBy(key, 1, seconds);
            return true;
        }
        if (count < maxCount) {
            accessLimitCache.increment(key, 1);
            return true;
        }
        WebFilterUtil.returnFail(response, HttpStatus.SC_FORBIDDEN, Response.custom(403, accessLimit.msg()));
        return false;
    }

}
