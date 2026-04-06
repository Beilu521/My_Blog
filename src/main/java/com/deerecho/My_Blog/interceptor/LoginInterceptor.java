package com.deerecho.My_Blog.interceptor;

import com.deerecho.My_Blog.entity.User;
import com.deerecho.My_Blog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    public static final String USER_ID = "userId";
    public static final String USER_ROLE = "userRole";
    public static final String USER_NICKNAME = "userNickname";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank()) {
            request.setAttribute(USER_ROLE, User.ROLE_GUEST);
            return true;
        }

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期\"}");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String nickname = jwtUtil.getUsernameFromToken(token);

        request.setAttribute(USER_ID, userId);
        request.setAttribute(USER_NICKNAME, nickname);
        request.setAttribute(USER_ROLE, User.ROLE_USER);
        return true;
    }
}
