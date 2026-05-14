package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.repositories.AuditLogRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminAuditServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AuditLogRepository auditLogRepository =
                (AuditLogRepository) getServletContext().getAttribute("auditLogRepository");
        String action = trim(request.getParameter("action"));
        String role = trim(request.getParameter("role"));
        String outcome = trim(request.getParameter("outcome"));

        List<AuditLogEntry> entries = auditLogRepository.findByFilters(action, role, outcome);
        if ("csv".equalsIgnoreCase(request.getParameter("export"))) {
            exportCsv(response, entries);
            return;
        }

        request.setAttribute("entries", entries);
        request.setAttribute("selectedAction", action);
        request.setAttribute("selectedRole", role);
        request.setAttribute("selectedOutcome", outcome);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/audit.jsp").forward(request, response);
    }

    private void exportCsv(HttpServletResponse response, List<AuditLogEntry> entries) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=admin-audit-log.csv");
        StringBuilder csv = new StringBuilder();
        csv.append("Timestamp,Username,Role,Action,Outcome,IP Address,Details\n");
        for (AuditLogEntry entry : entries) {
            csv.append(csv(entry.getTimestamp())).append(',')
                    .append(csv(entry.getUsername())).append(',')
                    .append(csv(entry.getRole())).append(',')
                    .append(csv(entry.getAction())).append(',')
                    .append(csv(entry.getOutcome())).append(',')
                    .append(csv(entry.getIpAddress())).append(',')
                    .append(csv(entry.getDetails())).append('\n');
        }
        response.getOutputStream().write(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ") + "\"";
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
