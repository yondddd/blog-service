package com.wonder.blog.web.filter;


import com.wonder.blog.service.UserService;
import com.wonder.blog.util.web.WebFilterUtil;
import com.wonder.blog.web.filter.local.LocalHttpContext;
import com.wonder.blog.web.filter.local.LocalHttpFilter;
import com.wonder.blog.web.filter.local.LocalHttpFilterChain;
import com.wonder.common.resp.Response;
import com.wonder.common.utils.trace.TraceId;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class ServletFilter implements Filter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletFilter.class);
    
    private final UserService userService;
    
    public ServletFilter(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LocalHttpFilterChain.getDefaultChain()
                // 鉴权
                .addFilter(new LocalAuthFilter(userService));
        Filter.super.init(filterConfig);
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 日志traceId
        MDC.put("trace", TraceId.nextId());
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            List<LocalHttpFilter> customFilters = request.getServletPath().startsWith("/ping")
                    ? Collections.emptyList()
                    : LocalHttpFilterChain.getDefaultFilters();
            
            LocalHttpFilterChain
                    .custom()
                    .setFilters(customFilters)
                    .setLastFilter((req, resp) -> filterChain.doFilter(request, response))
                    .doFilter(LocalHttpContext.custom().setRequest(request).setResponse(response));
        } catch (Exception e) {
            WebFilterUtil.returnFail(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Response.custom(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务异常"));
            LOGGER.error("<|>servletPath:{}<|>", request.getServletPath(), e);
        } finally {
            MDC.clear();
        }
    }
    
}
