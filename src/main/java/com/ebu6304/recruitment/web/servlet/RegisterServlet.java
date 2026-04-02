package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 注册 Servlet（仅 TA 注册）
 * GET  /register → 显示注册页面
 * POST /register → 校验并处理注册，成功后跳转到登录页
 *
 * 校验规则：
 *   - 必填字段不为空
 *   - 邮箱必须是大学邮箱（.ac.uk 或 .edu.cn 结尾）
 *   - 密码至少 8 位，且包含字母和数字
 *   - 用户名 3-20 位，只允许字母、数字、下划线
 *
 * @author Group39 / Fang Zixi
 * @version 1.1
 */
public class RegisterServlet extends HttpServlet {

    /** 大学邮箱正则：支持 @xxx.ac.uk 和 @xxx.edu.cn */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.(ac\\.uk|edu\\.cn|edu)$"
    );

    /** 用户名正则：3-20位，只允许字母、数字、下划线 */
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_]{3,20}$"
    );

    /** 密码正则：至少8位，必须同时包含字母和数字 */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d).{8,}$"
    );

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
        String confirm    = request.getParameter("confirmPassword");
        String email      = request.getParameter("email");
        String fullName   = request.getParameter("fullName");
        String studentId  = request.getParameter("studentId");
        String department = request.getParameter("department");
        String major      = request.getParameter("major");

        // ===== 服务端校验 =====
        String error = validate(username, password, confirm, email, fullName, studentId);
        if (error != null) {
            setAttributes(request, username, email, fullName, studentId, department, major);
            request.setAttribute("error", error);
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
            setAttributes(request, username, email, fullName, studentId, department, major);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }

    /**
     * 统一校验逻辑，返回第一个错误信息，全部通过返回 null。
     */
    private String validate(String username, String password, String confirm,
                            String email, String fullName, String studentId) {
        // 必填字段非空检查
        if (isBlank(username) || isBlank(password) || isBlank(email)
                || isBlank(fullName) || isBlank(studentId)) {
            return "Please fill in all required fields.";
        }
        // 用户名格式
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            return "Username must be 3–20 characters and contain only letters, numbers, or underscores.";
        }
        // 邮箱格式（大学邮箱）
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Please use a valid university email address (e.g. ending in .ac.uk or .edu.cn).";
        }
        // 密码强度
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "Password must be at least 8 characters and contain both letters and numbers.";
        }
        // 两次密码一致
        if (confirm != null && !password.equals(confirm)) {
            return "Passwords do not match.";
        }
        return null;
    }

    /** 回填表单数据（注册失败时保留用户已输入的内容） */
    private void setAttributes(HttpServletRequest request, String username, String email,
                               String fullName, String studentId,
                               String department, String major) {
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("fullName", fullName);
        request.setAttribute("studentId", studentId);
        request.setAttribute("department", department);
        request.setAttribute("major", major);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}