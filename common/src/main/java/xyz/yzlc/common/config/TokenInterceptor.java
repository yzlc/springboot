package xyz.yzlc.common.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import xyz.yzlc.common.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
public class TokenInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String authorization = request.getHeader(jwtConfig.getAuthorization());
        if (Objects.isNull(authorization) || !authorization.startsWith(jwtConfig.getBearer())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = authorization.substring(7);
        String key;
        try {
            String userId = JwtUtil.parseJwt(token).getSubject();
            key = "";
        } catch (Exception e) {
            log.error("", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            JwtUtil.parseJws(token, key);
            return true;
        } catch (Exception e) {
            log.error("", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}