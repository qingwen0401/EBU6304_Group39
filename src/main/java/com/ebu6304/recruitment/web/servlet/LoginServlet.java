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
 * POST /login  → 处理登录，成功后根据角色跳转到对应首页
 *
 * @author Group39 / Fang Zixi
 * @version 1.1
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 如果已登录，根据角色跳转到对应 dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            redirectByRole(user.getRole(), request, response);
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

            // 创建会话
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("authToken", token);

            // 根据角色跳转到各自首页
            redirectByRole(user.getRole(), request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }

    /**
     * 根据角色跳转到对应 dashboard。
     */
    private void redirectByRole(String role, HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        if ("TA".equals(role)) {
            response.sendRedirect(ctx + "/ta/dashboard");
        } else if ("MO".equals(role)) {
            response.sendRedirect(ctx + "/mo/dashboard");
        } else if ("ADMIN".equals(role)) {
            response.sendRedirect(ctx + "/admin/dashboard");
        } else {
            response.sendRedirect(ctx + "/login");
        }
    }
}