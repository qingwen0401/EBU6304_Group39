package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 注册 Servlet（TA注册）
 * GET  /register → 显示注册页面
 * POST /register → 处理注册，成功后跳转到登录页
 *
 * @author Group39
 * @version 1.0
 */
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username   = request.getParameter("username");
        String password   = request.getParameter("password");
        String email      = request.getParameter("email");
        String fullName   = request.getParameter("fullName");
        String studentId  = request.getParameter("studentId");
        String department = request.getParameter("department");
        String major      = request.getParameter("major");

        // 基本非空校验
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || studentId == null || studentId.trim().isEmpty()) {
            request.setAttribute("error", "Please fill in all required fields.");
            // 回填已输入的值
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("studentId", studentId);
            request.setAttribute("department", department);
            request.setAttribute("major", major);
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }

        AuthService authService = (AuthService) getServletContext().getAttribute("authService");

        try {
            authService.registerTA(
                    username.trim(), password, email.trim(),
                    fullName.trim(), studentId.trim(),
                    department != null ? department.trim() : "",
                    major != null ? major.trim() : ""
            );
            // 注册成功，跳转到登录页并显示成功提示
            response.sendRedirect(request.getContextPath() + "/login?registered=true");

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullName", fullName);
            request.setAttribute("studentId", studentId);
            request.setAttribute("department", department);
            request.setAttribute("major", major);
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }
}
