package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * 登录 Servlet
 * GET  /login  → 显示登录页面
 * POST /login  → 处理登录，成功后跳转到对应角色的首页
 *
 * @author Group39
 * @version 1.0
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 如果已登录，直接跳转到 TA 仪表盘
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/ta/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        AuthService authService = (AuthService) getServletContext().getAttribute("authService");

        try {
            String token = authService.login(username.trim(), password);
            Optional<User> userOpt = authService.getUserByToken(token);
            if (!userOpt.isPresent()) {
                throw new IllegalArgumentException("Login failed");
            }
            User user = userOpt.get();

            // 创建会话，存储用户信息
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("authToken", token);

            // 根据角色跳转
            String role = user.getRole();
            if ("TA".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/ta/dashboard");
            } else if ("MO".equals(role)) {
                // TODO: MO dashboard (暂时跳转到 TA dashboard)
                response.sendRedirect(request.getContextPath() + "/ta/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/ta/dashboard");
            }

        } catch (Exception e) {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}
