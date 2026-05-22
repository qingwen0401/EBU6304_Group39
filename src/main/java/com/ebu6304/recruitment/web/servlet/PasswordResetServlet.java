package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.PasswordResetEmail;
import com.ebu6304.recruitment.models.PasswordResetToken;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.PasswordResetEmailRepository;
import com.ebu6304.recruitment.repositories.PasswordResetRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class PasswordResetServlet extends HttpServlet {

    private final PasswordResetRepository resetRepository = new PasswordResetRepository();
    private final PasswordResetEmailRepository emailRepository = new PasswordResetEmailRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("token", trim(request.getParameter("token")));
        request.getRequestDispatcher("/WEB-INF/jsp/password-reset.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = trim(request.getParameter("action"));
        if ("complete".equals(action)) {
            completeReset(request, response);
        } else {
            requestReset(request, response);
        }
    }

    private void requestReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = trim(request.getParameter("email"));
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        Optional<User> admin = userRepository.findAllAdmins().stream()
                .filter(user -> email.equalsIgnoreCase(user.getEmail()))
                .findFirst();

        if (admin.isPresent()) {
            String token = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            PasswordResetToken resetToken = new PasswordResetToken(
                    token, admin.get().getUserId(), admin.get().getEmail(),
                    now.toString(), now.plusMinutes(30).toString());
            resetRepository.save(resetToken);

            String resetLink = buildResetLink(request, token);
            emailRepository.save(new PasswordResetEmail(
                    "MAIL" + System.currentTimeMillis(),
                    admin.get().getEmail(),
                    "Admin password reset",
                    resetLink,
                    now.toString()));
            writeAudit(admin.get().getUsername(), admin.get().getUserId(), "ADMIN",
                    "PASSWORD_RESET_REQUEST", "SUCCESS", request,
                    "Password reset email queued for " + admin.get().getEmail());
            request.setAttribute("devResetLink", resetLink);
        } else {
            writeAudit(email, null, "UNKNOWN", "PASSWORD_RESET_REQUEST", "FAILED",
                    request, "No admin account found for reset email.");
        }

        request.setAttribute("message",
                "If the email belongs to an admin account, a reset email has been sent.");
        request.getRequestDispatcher("/WEB-INF/jsp/password-reset.jsp").forward(request, response);
    }

    private void completeReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tokenValue = trim(request.getParameter("token"));
        String password = trim(request.getParameter("password"));
        String confirm = trim(request.getParameter("confirmPassword"));
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");

        try {
            if (password.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long.");
            }
            if (!password.equals(confirm)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            PasswordResetToken token = resetRepository.findUsable(tokenValue)
                    .orElseThrow(() -> new IllegalArgumentException("Reset link is invalid or expired."));
            User admin = userRepository.findAllAdmins().stream()
                    .filter(user -> token.getUserId().equals(user.getUserId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Admin account not found."));
            admin.setPasswordHash(PasswordUtil.hashPassword(password));
            userRepository.saveAdmin(admin);
            token.setUsed(true);
            resetRepository.save(token);
            writeAudit(admin.getUsername(), admin.getUserId(), "ADMIN",
                    "PASSWORD_RESET_COMPLETE", "SUCCESS", request,
                    "Admin password reset completed.");
            request.setAttribute("message", "Password reset complete. Please sign in with the new password.");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("token", tokenValue);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/password-reset.jsp").forward(request, response);
    }

    private String buildResetLink(HttpServletRequest request, String token) {
        String base = request.getRequestURL().toString();
        return base + "?token=" + token;
    }

    private void writeAudit(String username, String userId, String role, String action,
                            String outcome, HttpServletRequest request, String details) {
        AuditLogRepository auditLogRepository =
                (AuditLogRepository) getServletContext().getAttribute("auditLogRepository");
        if (auditLogRepository == null) {
            return;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.trim().isEmpty()) {
            ip = request.getRemoteAddr();
        }
        auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                username, userId, role, action, outcome, ip, details));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
