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
 * 拦截 /ta/*、/mo/*、/admin/* 请求，检查用户是否已登录。
 * 已登录但角色不匹配（如 TA 试图访问 /mo/*）时，重定向回各自 dashboard。
 *
 * @author Group39 / Fang Zixi
 * @version 1.1
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpReq  = (HttpServletRequest)  request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        HttpSession session     = httpReq.getSession(false);
        User        currentUser = (session != null)
                ? (User) session.getAttribute("currentUser") : null;

        // 未登录 → 跳转到登录页
        if (currentUser == null) {
            httpResp.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }

        String path = httpReq.getServletPath();   // e.g. "/ta/dashboard"
        String role = currentUser.getRole();       // "TA" / "MO" / "ADMIN"
        String ctx  = httpReq.getContextPath();

        // 角色权限检查：TA 只能访问 /ta/*，MO 只能访问 /mo/*，ADMIN 可访问所有
        if (path.startsWith("/ta/") && !"TA".equals(role) && !"ADMIN".equals(role)) {
            httpResp.sendRedirect(ctx + getDashboard(role));
            return;
        }
        if (path.startsWith("/mo/") && !"MO".equals(role) && !"ADMIN".equals(role)) {
            httpResp.sendRedirect(ctx + getDashboard(role));
            return;
        }
        if (path.startsWith("/admin/") && !"ADMIN".equals(role)) {
            httpResp.sendRedirect(ctx + getDashboard(role));
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    private String getDashboard(String role) {
        if ("MO".equals(role))    return "/mo/dashboard";
        if ("ADMIN".equals(role)) return "/admin/dashboard";
        return "/ta/dashboard";
    }
}