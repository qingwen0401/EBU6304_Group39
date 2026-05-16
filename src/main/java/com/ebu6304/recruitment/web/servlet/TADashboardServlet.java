package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.NotificationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TADashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");
        JobService jobService =
                (JobService) getServletContext().getAttribute("jobService");
        NotificationService notificationService =
                (NotificationService) getServletContext().getAttribute("notificationService");

        List<Application> allApps = applicationService.getApplicationsByTA(currentUser.getUserId());

        long activeCount = allApps.stream()
                .filter(a -> Application.STATUS_PENDING.equals(a.getStatus()))
                .count();

        List<Application> recentApps = allApps.stream()
                .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<JobPosting> openJobs = jobService.getOpenJobs();
        List<JobPosting> recommendedJobs = openJobs.stream()
                .limit(3)
                .collect(Collectors.toList());

        List<Notification> recentNotifications = notificationService == null
                ? Collections.emptyList()
                : notificationService.getRecentNotificationsForUser(currentUser.getUserId(), 5);

        long unreadNotificationCount = notificationService == null
                ? 0
                : notificationService.countUnreadNotifications(currentUser.getUserId());

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("totalCount", (long) allApps.size());
        request.setAttribute("openJobCount", (long) openJobs.size());
        request.setAttribute("recentApps", recentApps);
        request.setAttribute("recommendedJobs", recommendedJobs);
        request.setAttribute("recentNotifications", recentNotifications);
        request.setAttribute("unreadNotificationCount", unreadNotificationCount);

        request.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(request, response);
    }
}