package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.NotificationService;
import com.ebu6304.recruitment.web.AppInitializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MODashboardServlet extends HttpServlet {

    private JobService jobService;
    private ApplicationService applicationService;
    private NotificationService notificationService;

    @Override
    public void init() throws ServletException {
        this.jobService = AppInitializer.getJobService();
        this.applicationService = AppInitializer.getApplicationService();
        this.notificationService = AppInitializer.getNotificationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        List<JobPosting> allJobs = jobService.getJobsByMo(moId);
        List<Application> allApplications = applicationService.getApplicationsByMo(moId);

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalJobs", allJobs.size());
        stats.put("openJobs", allJobs.stream().filter(JobPosting::isOpen).count());
        stats.put("closedJobs", allJobs.stream()
                .filter(j -> JobPosting.STATUS_CLOSED.equals(j.getStatus())).count());

        stats.put("totalApplications", allApplications.size());
        stats.put("pendingApplications", allApplications.stream()
                .filter(app -> Application.STATUS_PENDING.equals(app.getStatus())).count());
        stats.put("acceptedApplications", allApplications.stream()
                .filter(app -> Application.STATUS_ACCEPTED.equals(app.getStatus())).count());
        stats.put("rejectedApplications", allApplications.stream()
                .filter(app -> Application.STATUS_REJECTED.equals(app.getStatus())).count());

        int totalVacancies = allJobs.stream().mapToInt(JobPosting::getVacancies).sum();
        int totalFilled = allJobs.stream().mapToInt(JobPosting::getFilledCount).sum();
        stats.put("totalVacancies", totalVacancies);
        stats.put("totalFilled", totalFilled);
        stats.put("fillRate", totalVacancies > 0
                ? String.format("%.1f", (double) totalFilled / totalVacancies * 100)
                : "0.0");

        List<Application> recentApplications = allApplications.stream()
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .limit(5)
                .toList();

        List<JobPosting> jobsNeedingAttention = allJobs.stream()
                .filter(job -> {
                    long pendingCount = allApplications.stream()
                            .filter(app -> app.getJobId().equals(job.getJobId()))
                            .filter(app -> Application.STATUS_PENDING.equals(app.getStatus()))
                            .count();
                    return pendingCount > 0;
                })
                .limit(5)
                .toList();

        List<Notification> recentNotifications = notificationService == null
                ? Collections.emptyList()
                : notificationService.getRecentNotificationsForUser(moId, 5);

        long unreadNotificationCount = notificationService == null
                ? 0
                : notificationService.countUnreadNotifications(moId);

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("stats", stats);
        request.setAttribute("recentApplications", recentApplications);
        request.setAttribute("jobsNeedingAttention", jobsNeedingAttention);
        request.setAttribute("allJobs", allJobs);
        request.setAttribute("recentNotifications", recentNotifications);
        request.setAttribute("unreadNotificationCount", unreadNotificationCount);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(request, response);
    }
}