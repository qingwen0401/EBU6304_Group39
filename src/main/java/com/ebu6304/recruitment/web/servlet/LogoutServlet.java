package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 登出 Servlet
 * GET /logout → 清除会话，跳转到登录页
 *
 * @author Group39
 * @version 1.0
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 清除 AuthService 中的 token
            String token = (String) session.getAttribute("authToken");
            if (token != null) {
                AuthService authService = (AuthService) getServletContext().getAttribute("authService");
                if (authService != null) {
                    authService.logout(token);
                }
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
