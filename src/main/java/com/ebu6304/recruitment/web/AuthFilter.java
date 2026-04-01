package com.ebu6304.recruitment.web;

import com.ebu6304.recruitment.models.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 认证过滤器
 * 拦截所有 /ta/* 请求，检查用户是否已登录。
 * 未登录则重定向到登录页面。
 *
 * @author Group39
 * @version 1.0
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 无需初始化
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // 未登录，重定向到登录页
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // 已登录，继续处理请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 无需清理
    }
}
