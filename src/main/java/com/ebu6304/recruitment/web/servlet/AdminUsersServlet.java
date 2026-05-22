package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        request.setAttribute("tas", userRepository.findAllTAs());
        request.setAttribute("selectedRole", role);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("error", request.getParameter("error"));
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
        AuthService authService =
                (AuthService) getServletContext().getAttribute("authService");
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String userId = trim(request.getParameter("userId"));
        String action = trim(request.getParameter("action"));

        if ("create-mo".equals(action)) {
            handleCreateMO(request, response, authService, auditLogRepository, currentUser);
            return;
        }
        if ("create-ta".equals(action)) {
            handleCreateTA(request, response, authService, auditLogRepository, currentUser);
            return;
        }
        if ("update-ta".equals(action)) {
            handleUpdateTA(request, response, userRepository, auditLogRepository, currentUser, userId);
            return;
        }
        if ("delete-ta".equals(action)) {
            handleDeleteTA(request, response, userRepository, auditLogRepository, currentUser, userId);
            return;
        }
        if ("delete-mo".equals(action)) {
            handleDeleteMO(request, response, userRepository, auditLogRepository, currentUser, userId);
            return;
        }

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
        redirect(response, request, "message", detail);
    }

    private void handleCreateMO(HttpServletRequest request, HttpServletResponse response,
                                AuthService authService, AuditLogRepository auditLogRepository,
                                User currentUser) throws IOException {
        String username = trim(request.getParameter("username"));
        String password = trim(request.getParameter("password"));
        String email = trim(request.getParameter("email"));
        String fullName = trim(request.getParameter("fullName"));
        String department = trim(request.getParameter("department"));
        String moduleCode = trim(request.getParameter("moduleCode"));
        String moduleName = trim(request.getParameter("moduleName"));

        String outcome = "FAILED";
        String detail;
        try {
            if (isBlank(username) || isBlank(password) || isBlank(email)
                    || isBlank(fullName) || isBlank(department)
                    || isBlank(moduleCode) || isBlank(moduleName)) {
                throw new IllegalArgumentException("All MO fields are required.");
            }
            if (!email.contains("@")) {
                throw new IllegalArgumentException("A valid email address is required.");
            }
            ModuleOrganiser mo = authService.registerMO(
                    username, password, email, fullName, department, moduleCode, moduleName);
            outcome = "SUCCESS";
            detail = "Created MO account " + mo.getUsername();
            writeAudit(auditLogRepository, currentUser, "MO_CREATED", outcome,
                    request.getRemoteAddr(), detail);
            redirect(response, request, "message", detail);
        } catch (Exception e) {
            detail = e.getMessage();
            writeAudit(auditLogRepository, currentUser, "MO_CREATED", outcome,
                    request.getRemoteAddr(), detail);
            redirect(response, request, "error", detail);
        }
    }

    private void handleCreateTA(HttpServletRequest request, HttpServletResponse response,
                                AuthService authService, AuditLogRepository auditLogRepository,
                                User currentUser) throws IOException {
        String username = trim(request.getParameter("taUsername"));
        String password = trim(request.getParameter("taPassword"));
        String email = trim(request.getParameter("taEmail"));
        String fullName = trim(request.getParameter("taFullName"));
        String studentId = trim(request.getParameter("studentId"));
        String department = trim(request.getParameter("taDepartment"));
        String major = trim(request.getParameter("major"));
        String assignedModule = trim(request.getParameter("assignedModule"));

        String outcome = "FAILED";
        String detail;
        try {
            if (isBlank(username) || isBlank(password) || isBlank(email)
                    || isBlank(fullName) || isBlank(studentId)
                    || isBlank(department) || isBlank(major)) {
                throw new IllegalArgumentException("All TA fields are required.");
            }
            if (!email.contains("@")) {
                throw new IllegalArgumentException("A valid email address is required.");
            }
            TA ta = authService.registerTA(
                    username, password, email, fullName, studentId, department, major);
            ta.setAssignedModule(assignedModule);
            ((UserRepository) getServletContext().getAttribute("userRepository")).saveTA(ta);
            outcome = "SUCCESS";
            detail = "Created TA account " + ta.getUsername();
            writeAudit(auditLogRepository, currentUser, "TA_CREATED", outcome,
                    request.getRemoteAddr(), detail);
            redirect(response, request, "message", detail);
        } catch (Exception e) {
            detail = e.getMessage();
            writeAudit(auditLogRepository, currentUser, "TA_CREATED", outcome,
                    request.getRemoteAddr(), detail);
            redirect(response, request, "error", detail);
        }
    }

    private void handleUpdateTA(HttpServletRequest request, HttpServletResponse response,
                                UserRepository userRepository, AuditLogRepository auditLogRepository,
                                User currentUser, String userId) throws IOException {
        String outcome = "FAILED";
        String detail;
        try {
            TA ta = userRepository.findTAById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("TA account not found."));
            String email = trim(request.getParameter("email"));
            if (!isBlank(email) && !email.equals(ta.getEmail())
                    && userRepository.emailExists(email)) {
                throw new IllegalArgumentException("Email already registered: " + email);
            }
            ta.setFullName(trim(request.getParameter("fullName")));
            ta.setEmail(email);
            ta.setDepartment(trim(request.getParameter("department")));
            ta.setMajor(trim(request.getParameter("major")));
            ta.setAssignedModule(trim(request.getParameter("assignedModule")));
            userRepository.saveTA(ta);
            outcome = "SUCCESS";
            detail = "Updated TA account " + ta.getUsername();
            writeAudit(auditLogRepository, currentUser, "TA_UPDATED", outcome,
                    request.getRemoteAddr(), detail);
            redirect(response, request, "message", detail);
        } catch (Exception e) {
            detail = e.getMessage();
            writeAudit(auditLogRepository, currentUser, "TA_UPDATED", outcome,
                    request.getRemoteAddr(), detail);
            redirect(response, request, "error", detail);
        }
    }

    private void handleDeleteTA(HttpServletRequest request, HttpServletResponse response,
                                UserRepository userRepository, AuditLogRepository auditLogRepository,
                                User currentUser, String userId) throws IOException {
        Optional<TA> target = userRepository.findTAById(userId);
        String outcome = "FAILED";
        String detail = "TA account not found.";
        if (target.isPresent()) {
            boolean deleted = userRepository.deleteTA(userId);
            if (deleted) {
                outcome = "SUCCESS";
                detail = "Deleted TA account " + target.get().getUsername();
            }
        }
        writeAudit(auditLogRepository, currentUser, "TA_DELETED", outcome,
                request.getRemoteAddr(), detail);
        redirect(response, request, "SUCCESS".equals(outcome) ? "message" : "error", detail);
    }

    private void handleDeleteMO(HttpServletRequest request, HttpServletResponse response,
                                UserRepository userRepository, AuditLogRepository auditLogRepository,
                                User currentUser, String userId) throws IOException {
        Optional<ModuleOrganiser> target = userRepository.findMOById(userId);
        String outcome = "FAILED";
        String detail = "MO account not found.";
        if (target.isPresent()) {
            boolean deleted = userRepository.deleteMO(userId);
            if (deleted) {
                outcome = "SUCCESS";
                detail = "Deleted MO account " + target.get().getUsername();
            }
        }
        writeAudit(auditLogRepository, currentUser, "MO_DELETED", outcome,
                request.getRemoteAddr(), detail);
        redirect(response, request, "SUCCESS".equals(outcome) ? "message" : "error", detail);
    }

    private void writeAudit(AuditLogRepository auditLogRepository, User currentUser,
                            String action, String outcome, String ipAddress, String detail) {
        if (auditLogRepository == null || currentUser == null) {
            return;
        }
        auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                currentUser.getUsername(), currentUser.getUserId(), currentUser.getRole(),
                action, outcome, ipAddress, detail));
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request,
                          String key, String value) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/users?" + key + "="
                + URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
