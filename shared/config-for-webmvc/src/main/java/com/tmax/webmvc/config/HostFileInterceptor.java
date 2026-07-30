package com.tmax.webmvc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@Component
public class HostFileInterceptor implements HandlerInterceptor {
    public static final String PathConstants_DIVIDER = "/";
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String wildcardPath = antPathMatcher.extractPathWithinPattern(pattern, path);
        log.info("pattern: {}, path: {}, wildcardPath: {}", pattern, path, wildcardPath);
        request.setAttribute("path", PathConstants_DIVIDER + wildcardPath);
        return true;
    }
}
