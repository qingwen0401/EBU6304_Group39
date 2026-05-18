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




        // ====== 获取并分类通知 (TA) ======
        // 1. 获取所有通知（只在这里声明一次 List<Notification> allNotifs）
        List<Notification> allNotifs = notificationService == null ? Collections.emptyList()
                : notificationService.getNotificationsForUser(currentUser.getUserId());

        // 2. 重新赋值并排序（直接用 allNotifs，不要重复声明）
        allNotifs = allNotifs.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        // 3. 分组
        List<Notification> unreadNotifs = allNotifs.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
        List<Notification> readNotifs = allNotifs.stream().filter(Notification::isRead).collect(Collectors.toList());

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("totalCount", (long) allApps.size());
        request.setAttribute("openJobCount", (long) openJobs.size());
        request.setAttribute("recentApps", recentApps);
        request.setAttribute("recommendedJobs", recommendedJobs);

        request.setAttribute("unreadNotifs", unreadNotifs);
        request.setAttribute("readNotifs", readNotifs);
        request.setAttribute("unreadCount", unreadNotifs.size());
        request.setAttribute("readCount", readNotifs.size());

        request.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(request, response);
    }
}