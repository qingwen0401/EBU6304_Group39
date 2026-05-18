package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.NotificationService;
import com.ebu6304.recruitment.services.WorkloadService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminWorkloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        WorkloadService workloadService =
                (WorkloadService) getServletContext().getAttribute("workloadService");
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        NotificationService notificationService =
                (NotificationService) getServletContext().getAttribute("notificationService");

        String semester = defaultIfBlank(request.getParameter("semester"), "2026 Spring");
        String module = trim(request.getParameter("module"));
        String status = trim(request.getParameter("status"));

        List<WorkloadRecord> allRecords = workloadService.getAllWorkloadRecords();
        List<Map<String, Object>> workloadReport =
                buildReport(userRepository.findAllTAs(), allRecords, semester, module,
                        workloadService, notificationService);

        if (!isBlank(status)) {
            workloadReport = workloadReport.stream()
                    .filter(row -> status.equals(row.get("workloadStatus")))
                    .collect(Collectors.toList());
        }

        if ("csv".equalsIgnoreCase(request.getParameter("export"))) {
            exportWorkloadCsv(response, workloadReport, workloadService.getMaxWeeklyHours());
            return;
        }

        request.setAttribute("workloadReport", workloadReport);
        request.setAttribute("records", filterRecords(allRecords, semester, module));
        request.setAttribute("semester", semester);
        request.setAttribute("selectedModule", module);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("semesterOptions", collectSemesters(allRecords));
        request.setAttribute("moduleOptions", collectModules(allRecords));
        request.setAttribute("maxWeeklyHours", workloadService.getMaxWeeklyHours());
        request.setAttribute("warningWeeklyHours", workloadService.getWarningWeeklyHours());
        request.setAttribute("fairnessIndex", calculateFairnessIndex(workloadReport));

        request.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");

        WorkloadService workloadService =
                (WorkloadService) getServletContext().getAttribute("workloadService");
        AuditLogRepository auditLogRepository =
                (AuditLogRepository) getServletContext().getAttribute("auditLogRepository");
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        String action = request.getParameter("action");
        if ("notifyOverload".equals(action)) {
            handleNotifyOverload(request, response, workloadService, auditLogRepository, currentUser);
            return;
        }

        try {
            int threshold = Integer.parseInt(request.getParameter("maxWeeklyHours"));
            workloadService.setMaxWeeklyHours(threshold);
            if (auditLogRepository != null && currentUser != null) {
                auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                        currentUser.getUsername(), currentUser.getUserId(), currentUser.getRole(),
                        "CONFIG_UPDATE", "SUCCESS", request.getRemoteAddr(),
                        "Updated workload threshold to " + threshold + " hours."));
            }
        } catch (Exception e) {
            if (auditLogRepository != null && currentUser != null) {
                auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                        currentUser.getUsername(), currentUser.getUserId(), currentUser.getRole(),
                        "CONFIG_UPDATE", "FAILED", request.getRemoteAddr(), e.getMessage()));
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/workload");
    }

    private void handleNotifyOverload(HttpServletRequest request, HttpServletResponse response,
                                      WorkloadService workloadService,
                                      AuditLogRepository auditLogRepository,
                                      User currentUser) throws IOException {
        NotificationService notificationService =
                (NotificationService) getServletContext().getAttribute("notificationService");

        String taId = trim(request.getParameter("taId"));
        String taName = trim(request.getParameter("taName"));
        String semester = defaultIfBlank(request.getParameter("semester"), "2026 Spring");
        String module = trim(request.getParameter("module"));
        String status = trim(request.getParameter("status"));

        int totalWeeklyHours = 0;
        try {
            totalWeeklyHours = Integer.parseInt(request.getParameter("totalWeeklyHours"));
        } catch (Exception ignored) {
        }

        try {
            if (notificationService == null) {
                throw new IllegalStateException("Notification service is not available");
            }
            if (isBlank(taId)) {
                throw new IllegalArgumentException("TA ID is required");
            }

            boolean isResend = notificationService.hasWorkloadWarningBeenSent(taId, semester);

            notificationService.createWorkloadWarning(
                    taId,
                    taName,
                    totalWeeklyHours,
                    workloadService.getMaxWeeklyHours(),
                    semester
            );

            if (auditLogRepository != null && currentUser != null) {
                String actionDetail = isResend
                        ? "Sent workload reminder to " + taName + "."
                        : "Sent workload warning to " + taName + ".";
                auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                        currentUser.getUsername(), currentUser.getUserId(), currentUser.getRole(),
                        "SEND_NOTIFICATION", "SUCCESS", request.getRemoteAddr(), actionDetail));
            }

            response.sendRedirect(buildWorkloadRedirect(request, semester, module, status, "1", isResend));
        } catch (Exception e) {
            if (auditLogRepository != null && currentUser != null) {
                auditLogRepository.save(new AuditLogEntry("AUD" + System.currentTimeMillis(),
                        currentUser.getUsername(), currentUser.getUserId(), currentUser.getRole(),
                        "SEND_NOTIFICATION", "FAILED", request.getRemoteAddr(), e.getMessage()));
            }

            response.sendRedirect(buildWorkloadRedirect(request, semester, module, status, "0", false));
        }
    }

    private List<Map<String, Object>> buildReport(List<TA> tas, List<WorkloadRecord> allRecords,
                                                  String semester, String module,
                                                  WorkloadService workloadService,
                                                  NotificationService notificationService) {
        List<Map<String, Object>> report = new ArrayList<>();

        for (TA ta : tas) {
            List<WorkloadRecord> records = allRecords.stream()
                    .filter(r -> ta.getUserId().equals(r.getTaId()))
                    .filter(r -> isBlank(semester) || semester.equals(r.getSemester()))
                    .filter(r -> isBlank(module) || module.equalsIgnoreCase(r.getModuleCode()))
                    .collect(Collectors.toList());

            int activeHours = records.stream()
                    .filter(r -> "ACTIVE".equals(r.getStatus()))
                    .mapToInt(WorkloadRecord::getWeeklyHours)
                    .sum();

            Map<String, Object> row = new HashMap<>();
            row.put("taId", ta.getUserId());
            row.put("taName", emptyFallback(ta.getFullName(), ta.getUsername()));
            row.put("studentId", ta.getStudentId());
            row.put("totalWeeklyHours", activeHours);
            row.put("jobCount", records.stream().filter(r -> "ACTIVE".equals(r.getStatus())).count());
            row.put("records", records);
            row.put("modules", records.stream().map(WorkloadRecord::getModuleCode)
                    .filter(v -> !isBlank(v)).distinct().collect(Collectors.joining(", ")));
            row.put("isOverloaded", activeHours > workloadService.getMaxWeeklyHours());
            row.put("workloadStatus", workloadStatus(activeHours, workloadService));

            if (notificationService != null) {
                List<Notification> warnings =
                        notificationService.getWorkloadWarningsForTa(ta.getUserId(), semester);
                row.put("workloadNotified", !warnings.isEmpty());
                row.put("notificationCount", warnings.size());
                row.put("lastNotifiedAt", warnings.isEmpty() ? "" : warnings.get(0).getCreatedAt());
            } else {
                row.put("workloadNotified", false);
                row.put("notificationCount", 0);
                row.put("lastNotifiedAt", "");
            }

            report.add(row);
        }

        report.sort(Comparator.comparing(row -> String.valueOf(row.get("taName"))));
        return report;
    }

    private List<WorkloadRecord> filterRecords(List<WorkloadRecord> records, String semester, String module) {
        return records.stream()
                .filter(r -> isBlank(semester) || semester.equals(r.getSemester()))
                .filter(r -> isBlank(module) || module.equalsIgnoreCase(r.getModuleCode()))
                .sorted(Comparator.comparing(WorkloadRecord::getCreatedAt,
                        Comparator.nullsLast(String::compareTo)).reversed())
                .collect(Collectors.toList());
    }

    private String workloadStatus(int hours, WorkloadService workloadService) {
        if (hours == 0) {
            return "IDLE";
        }
        if (hours <= workloadService.getWarningWeeklyHours()) {
            return "NORMAL";
        }
        if (hours <= workloadService.getMaxWeeklyHours()) {
            return "WARNING";
        }
        return "OVERLOADED";
    }

    private double calculateFairnessIndex(List<Map<String, Object>> report) {
        if (report.isEmpty()) {
            return 0.0;
        }

        double mean = report.stream()
                .mapToInt(row -> (Integer) row.get("totalWeeklyHours"))
                .average()
                .orElse(0.0);

        if (mean == 0.0) {
            return 0.0;
        }

        double totalDiff = 0.0;
        for (Map<String, Object> a : report) {
            for (Map<String, Object> b : report) {
                totalDiff += Math.abs((Integer) a.get("totalWeeklyHours")
                        - (Integer) b.get("totalWeeklyHours"));
            }
        }

        return Math.round((totalDiff / (2 * report.size() * report.size() * mean)) * 100.0) / 100.0;
    }

    private void exportWorkloadCsv(HttpServletResponse response, List<Map<String, Object>> report,
                                   int threshold) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=admin-workload-report.csv");

        StringBuilder csv = new StringBuilder();
        csv.append("TA Name,Student ID,Modules,Active Jobs,Weekly Hours,Threshold,Status\n");

        for (Map<String, Object> row : report) {
            csv.append(csv(row.get("taName"))).append(',')
                    .append(csv(row.get("studentId"))).append(',')
                    .append(csv(row.get("modules"))).append(',')
                    .append(row.get("jobCount")).append(',')
                    .append(row.get("totalWeeklyHours")).append(',')
                    .append(threshold).append(',')
                    .append(csv(row.get("workloadStatus"))).append('\n');
        }

        response.getOutputStream().write(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Set<String> collectSemesters(List<WorkloadRecord> records) {
        return records.stream().map(WorkloadRecord::getSemester)
                .filter(v -> !isBlank(v))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> collectModules(List<WorkloadRecord> records) {
        return records.stream().map(WorkloadRecord::getModuleCode)
                .filter(v -> !isBlank(v))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String buildWorkloadRedirect(HttpServletRequest request, String semester,
                                         String module, String status, String notified,
                                         boolean again) {
        return request.getContextPath()
                + "/admin/workload?semester=" + url(semester)
                + "&module=" + url(module)
                + "&status=" + url(status)
                + "&notified=" + url(notified)
                + (again ? "&again=1" : "");
    }

    private String url(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    private String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ") + "\"";
    }

    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String emptyFallback(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }
}