package com.example.myapp.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 放行登录相关请求
        if (requestURI.startsWith("/login") || requestURI.startsWith("/logout")) {
            return true;
        }

        // 放行静态资源
        if (requestURI.startsWith("/css") || requestURI.startsWith("/js")
                || requestURI.startsWith("/images") || requestURI.startsWith("/h2-console")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }
}