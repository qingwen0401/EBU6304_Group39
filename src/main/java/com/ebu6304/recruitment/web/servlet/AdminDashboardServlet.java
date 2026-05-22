package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.WorkloadService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        JobRepository jobRepository =
                (JobRepository) getServletContext().getAttribute("jobRepository");
        ApplicationRepository applicationRepository =
                (ApplicationRepository) getServletContext().getAttribute("applicationRepository");
        WorkloadService workloadService =
                (WorkloadService) getServletContext().getAttribute("workloadService");
        AuditLogRepository auditLogRepository =
                (AuditLogRepository) getServletContext().getAttribute("auditLogRepository");

        List<JobPosting> jobs = jobRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        List<WorkloadRecord> workloadRecords = workloadService.getAllWorkloadRecords();
        String semester = request.getParameter("semester") == null
                ? "2026 Spring" : request.getParameter("semester");
        List<Map<String, Object>> workloadReport = workloadService.getWorkloadReport(semester);

        long taCount = userRepository.findAllTAs().size();
        long moCount = userRepository.findAllMOs().size();
        long adminCount = userRepository.findAllAdmins().size();
        long totalUsers = taCount + moCount + adminCount;
        long activeUsers = userRepository.findAllTAs().stream().filter(User::isActive).count()
                + userRepository.findAllMOs().stream().filter(User::isActive).count()
                + userRepository.findAllAdmins().stream().filter(User::isActive).count();

        Map<String, Long> applicationStats = new HashMap<>();
        applicationStats.put("total", (long) applications.size());
        applicationStats.put("pending", applications.stream()
                .filter(a -> Application.STATUS_PENDING.equals(a.getStatus())).count());
        applicationStats.put("accepted", applications.stream()
                .filter(a -> Application.STATUS_ACCEPTED.equals(a.getStatus())).count());
        applicationStats.put("rejected", applications.stream()
                .filter(a -> Application.STATUS_REJECTED.equals(a.getStatus())).count());
        applicationStats.put("withdrawn", applications.stream()
                .filter(a -> Application.STATUS_WITHDRAWN.equals(a.getStatus())).count());

        long totalWorkloadHours = workloadReport.stream()
                .mapToLong(row -> (Integer) row.get("totalWeeklyHours")).sum();
        long overloadedCount = workloadReport.stream()
                .filter(row -> Boolean.TRUE.equals(row.get("isOverloaded"))).count();

        jobs.sort(Comparator.comparing(JobPosting::getPostedAt,
                Comparator.nullsLast(String::compareTo)).reversed());

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("activeUsers", activeUsers);
        request.setAttribute("inactiveUsers", totalUsers - activeUsers);
        request.setAttribute("taCount", taCount);
        request.setAttribute("moCount", moCount);
        request.setAttribute("adminCount", adminCount);
        request.setAttribute("totalJobs", jobs.size());
        request.setAttribute("openJobs", jobs.stream().filter(JobPosting::isOpen).count());
        request.setAttribute("closedJobs", jobs.stream()
                .filter(j -> JobPosting.STATUS_CLOSED.equals(j.getStatus())).count());
        request.setAttribute("cancelledJobs", jobs.stream()
                .filter(j -> JobPosting.STATUS_CANCELLED.equals(j.getStatus())).count());
        request.setAttribute("applicationStats", applicationStats);
        request.setAttribute("totalWorkloadHours", totalWorkloadHours);
        request.setAttribute("overloadedCount", overloadedCount);
        request.setAttribute("maxWeeklyHours", workloadService.getMaxWeeklyHours());
        request.setAttribute("recentJobs", jobs.stream().limit(5).toList());
        request.setAttribute("recentWorkloadRecords", workloadRecords.stream().limit(5).toList());
        request.setAttribute("recruitmentDistribution", buildRecruitmentDistribution(jobs, applications));

        List<AuditLogEntry> recentAudit = auditLogRepository == null
                ? Collections.emptyList() : auditLogRepository.findAll().stream().limit(5).toList();
        request.setAttribute("recentAudit", recentAudit);
        List<AuditLogEntry> recentLoginActivity = auditLogRepository == null
                ? Collections.emptyList()
                : auditLogRepository.findByFilters("LOGIN", "", "").stream().limit(8).toList();
        request.setAttribute("recentLoginActivity", recentLoginActivity);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(request, response);
    }

    private List<Map<String, Object>> buildRecruitmentDistribution(List<JobPosting> jobs,
                                                                   List<Application> applications) {
        Map<String, Map<String, Object>> distribution = new TreeMap<>();
        Map<String, JobPosting> jobsById = jobs.stream()
                .collect(Collectors.toMap(JobPosting::getJobId, Function.identity(), (a, b) -> a));

        for (JobPosting job : jobs) {
            String module = emptyFallback(job.getModuleCode(), "Unknown");
            Map<String, Object> row = distribution.computeIfAbsent(module, this::newDistributionRow);
            row.put("jobCount", (Integer) row.get("jobCount") + 1);
            row.put("vacancies", (Integer) row.get("vacancies") + job.getVacancies());
            row.put("filled", (Integer) row.get("filled") + job.getFilledCount());
        }

        for (Application application : applications) {
            JobPosting job = jobsById.get(application.getJobId());
            String module = emptyFallback(application.getModuleCode(),
                    job == null ? "Unknown" : job.getModuleCode());
            Map<String, Object> row = distribution.computeIfAbsent(module, this::newDistributionRow);
            row.put("applicationCount", (Integer) row.get("applicationCount") + 1);
            if (Application.STATUS_ACCEPTED.equals(application.getStatus())) {
                row.put("acceptedCount", (Integer) row.get("acceptedCount") + 1);
            }
        }

        return distribution.values().stream().toList();
    }

    private Map<String, Object> newDistributionRow(String module) {
        Map<String, Object> row = new HashMap<>();
        row.put("module", module);
        row.put("jobCount", 0);
        row.put("vacancies", 0);
        row.put("filled", 0);
        row.put("applicationCount", 0);
        row.put("acceptedCount", 0);
        return row;
    }

    private String emptyFallback(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
