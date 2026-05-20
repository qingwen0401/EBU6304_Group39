package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.controllers.MOJobController;
import com.ebu6304.recruitment.controllers.ControllerResult;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MOJobManagementServlet extends HttpServlet {

    private JobService jobService;
    private ApplicationService applicationService;
    private NotificationService notificationService;

    @Override
    public void init() throws ServletException {
        jobService = (JobService) getServletContext().getAttribute("jobService");
        applicationService = (ApplicationService) getServletContext().getAttribute("applicationService");
        notificationService = (NotificationService) getServletContext().getAttribute("notificationService");

        if (jobService == null) {
            throw new ServletException("JobService not found in context");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        if (!"MO".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        MOJobController controller = new MOJobController(jobService);
        ControllerResult<List<JobPosting>> result = controller.getMyJobs(user.getUserId());

        if (result.isSuccess()) {
            List<JobPosting> jobs = result.getData();
            // Sort jobs: CANCELLED jobs at the bottom
            jobs.sort((j1, j2) -> {
                boolean isCancelled1 = JobPosting.STATUS_CANCELLED.equals(j1.getStatus());
                boolean isCancelled2 = JobPosting.STATUS_CANCELLED.equals(j2.getStatus());
                if (isCancelled1 && !isCancelled2) return 1;
                if (!isCancelled1 && isCancelled2) return -1;
                return 0;
            });
            request.setAttribute("jobs", jobs);
        } else {
            request.setAttribute("errorMessage", result.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/mo/jobs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        if (!"MO".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        String action = request.getParameter("action");
        String jobId = request.getParameter("jobId");

        if ("cancel".equals(action) && jobId != null) {
            MOJobController controller = new MOJobController(jobService);
            ControllerResult<Void> result = controller.cancelJob(user.getUserId(), jobId);

            if (result.isSuccess()) {
                request.setAttribute("successMessage", "Job cancelled successfully");

// ================= 新增：通知 TA 逻辑 =================
                try {
                    // 获取所有申请了这个职位的记录
                    List<Application> apps = applicationService.getApplicationsByJob(jobId);
                    if (apps != null) {
                        for (Application app : apps) {
                            // 直接调用队友写好的 Service 方法，传 6 个参数即可！
                            notificationService.createNotification(
                                    app.getTaId(),          // 1. recipientUserId (收件人ID)
                                    "TA",                   // 2. recipientRole (收件人角色)
                                    "JOB_CANCELLED",        // 3. type (通知类型)
                                    "Job Cancelled Notice", // 4. title (标题)
                                    "The position you applied for (Job ID: " + jobId + ") has been cancelled by the Module Organiser.", // 5. message (正文)
                                    jobId                   // 6. relatedEntityId (关联的职位ID)
                            );
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
                // ======================================================

            } else {
                request.setAttribute("errorMessage", result.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/mo/jobs");
    }
}