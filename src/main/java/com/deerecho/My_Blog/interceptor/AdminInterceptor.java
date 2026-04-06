package com.deerecho.My_Blog.interceptor;

import com.deerecho.My_Blog.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer role = (Integer) request.getAttribute(LoginInterceptor.USER_ROLE);

        if (role == null || role == User.ROLE_GUEST) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
            return false;
        }

        if (role != User.ROLE_ADMIN) {
            response.setStatus(403);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无管理员权限\"}");
            return false;
        }

        return true;
    }
}
