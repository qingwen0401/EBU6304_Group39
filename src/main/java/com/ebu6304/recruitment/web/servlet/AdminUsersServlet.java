package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminUsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        String role = trim(request.getParameter("role"));
        String status = trim(request.getParameter("status"));

        List<User> users = new ArrayList<>();
        users.addAll(userRepository.findAllTAs());
        users.addAll(userRepository.findAllMOs());
        users.addAll(userRepository.findAllAdmins());
        users = users.stream()
                .filter(u -> isBlank(role) || role.equalsIgnoreCase(u.getRole()))
                .filter(u -> isBlank(status)
                        || ("ACTIVE".equals(status) && u.isActive())
                        || ("INACTIVE".equals(status) && !u.isActive()))
                .sorted(Comparator.comparing(User::getRole).thenComparing(User::getUsername))
                .collect(Collectors.toList());

        request.setAttribute("users", users);
        request.setAttribute("selectedRole", role);
        request.setAttribute("selectedStatus", status);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        AuditLogRepository auditLogRepository =
                (AuditLogRepository) getServletContext().getAttribute("auditLogRepository");
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String userId = trim(request.getParameter("userId"));
        String action = trim(request.getParameter("action"));

        Optional<User> target = userRepository.findAllTAs().stream()
                .map(u -> (User) u).filter(u -> userId.equals(u.getUserId())).findFirst();
        if (!target.isPresent()) {
            target = userRepository.findAllMOs().stream()
                    .map(u -> (User) u).filter(u -> userId.equals(u.getUserId())).findFirst();
        }
        if (!target.isPresent()) {
            target = userRepository.findAllAdmins().stream()
                    .filter(u -> userId.equals(u.getUserId())).findFirst();
        }

        String outcome = "FAILED";
        String detail = "User not found.";
        if (target.isPresent()) {
            User user = target.get();
            if (!user.getUserId().equals(currentUser.getUserId())) {
                user.setActive("activate".equals(action));
                userRepository.saveUser(user);
                outcome = "SUCCESS";
                detail = ("activate".equals(action) ? "Activated " : "Deactivated ")
                        + user.getUsername();
            } else {
                detail = "Admins cannot deactivate their own active session.";
            }
        }
        if (auditLogRepository != null && currentUser != null) {
            auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                    currentUser.getUsername(), currentUser.getUserId(), currentUser.getRole(),
                    "USER_STATUS_UPDATE", outcome, request.getRemoteAddr(), detail));
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
