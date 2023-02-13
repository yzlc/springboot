package com.yzlc.common.filter;

import com.yzlc.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
//@WebFilter(filterName = "Filter", urlPatterns = "/*")
public class GlobalFilter implements Filter {
    private static final String GLOBAL_FILTER_IGNORES = "/login";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String requestURI = request.getRequestURI(); // 设置字符编码链锁
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Content-Type, Accept, x-requested-with, Cache-Control");

        String tags = GLOBAL_FILTER_IGNORES;
        if (StringUtils.isNotEmpty(tags)) {
            String[] str = tags.split(",");
            for (String s : str) {
                if (requestURI.contains(s)) {
                    chain.doFilter(request, resp);
                    return;
                }
            }
        }

        String authorization = request.getHeader(AUTHORIZATION);
        if (Objects.isNull(authorization) || !authorization.startsWith(BEARER)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authorization.substring(7);
        String key;
        try {
            String userId = JwtUtil.parseJwt(token).getSubject();
            key = "01234567890123456789012345678901";//password
        } catch (Exception e) {
            log.error("", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            JwtUtil.parseJws(token, key);
            chain.doFilter(request, resp);
        } catch (Exception e) {
            log.error("", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
