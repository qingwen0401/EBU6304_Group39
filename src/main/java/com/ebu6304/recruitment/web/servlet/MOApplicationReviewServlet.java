package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MO申请审查Servlet
 * GET /mo/applications → 显示所有申请列表
 * POST /mo/applications/review → 审查单个申请(录用/拒绝)
 * POST /mo/applications/bulk-reject → 批量拒绝申请
 *
 * @author Group39
 * @version 1.0
 */
public class MOApplicationReviewServlet extends HttpServlet {

    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.gson = new Gson();
    }

    private ApplicationService getApplicationService() {
        return (ApplicationService) getServletContext().getAttribute("applicationService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();
        String jobId = request.getParameter("jobId");

        // 获取该MO的所有职位的申请，如果提供了jobId则过滤
        ApplicationService applicationService = getApplicationService();
        List<Application> applications = applicationService.getApplicationsByMo(moId);
        if (jobId != null && !jobId.isEmpty()) {
            applications = applications.stream()
                    .filter(app -> jobId.equals(app.getJobId()))
                    .toList();
        }

        // 按状态分类
        Map<String, List<Application>> applicationsByStatus = new HashMap<>();
        applicationsByStatus.put("PENDING", applications.stream()
                .filter(app -> Application.STATUS_PENDING.equals(app.getStatus()))
                .toList());
        applicationsByStatus.put("ACCEPTED", applications.stream()
                .filter(app -> Application.STATUS_ACCEPTED.equals(app.getStatus()))
                .toList());
        applicationsByStatus.put("REJECTED", applications.stream()
                .filter(app -> Application.STATUS_REJECTED.equals(app.getStatus()))
                .toList());

        request.setAttribute("applications", applications);
        request.setAttribute("applicationsByStatus", applicationsByStatus);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("filterJobId", jobId);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("review".equals(action)) {
            handleReview(request, response);
        } else if ("bulk-reject".equals(action)) {
            handleBulkReject(request, response);
        } else if ("add-note".equals(action)) {
            handleAddNote(request, response);
        } else {
            sendJsonError(response, "Unknown action");
        }
    }

    /**
     * 处理单个申请审查(录用/拒绝)
     */
    private void handleReview(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String applicationId = request.getParameter("applicationId");
        String decision = request.getParameter("decision"); // "accept" or "reject"
        String note = request.getParameter("note");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();
        ApplicationService applicationService = getApplicationService();
        if (applicationService == null) {
            sendJsonError(response, "Application service is not available");
            return;
        }

        try {
            Optional<Application> appOpt = applicationService.getApplicationById(applicationId);
            if (!appOpt.isPresent()) {
                sendJsonError(response, "Application not found");
                return;
            }

            Application application = appOpt.get();

            // 验证该申请是否属于该MO
            if (!application.getMoId().equals(moId)) {
                sendJsonError(response, "Unauthorized");
                return;
            }

            if ("accept".equals(decision)) {
                applicationService.acceptApplication(moId, applicationId, note);
            } else if ("reject".equals(decision)) {
                applicationService.rejectApplication(moId, applicationId, note);
            } else {
                sendJsonError(response, "Invalid decision");
                return;
            }

            sendJsonSuccess(response, "Application reviewed successfully");

        } catch (Exception e) {
            sendJsonError(response, "Failed to review application: " + e.getMessage());
        }
    }

    /**
     * 处理批量拒绝申请
     */
    private void handleBulkReject(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String[] applicationIds = request.getParameterValues("applicationIds[]");
        String note = request.getParameter("note");

        if (applicationIds == null || applicationIds.length == 0) {
            sendJsonError(response, "No applications selected");
            return;
        }

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();
        ApplicationService applicationService = getApplicationService();
        if (applicationService == null) {
            sendJsonError(response, "Application service is not available");
            return;
        }

        int rejectedCount = 0;
        for (String applicationId : applicationIds) {
            try {
                Optional<Application> appOpt = applicationService.getApplicationById(applicationId);
                if (appOpt.isPresent() && appOpt.get().getMoId().equals(moId)) {
                    applicationService.rejectApplication(moId, applicationId,
                            note != null ? note : "Bulk rejection");
                    rejectedCount++;
                }
            } catch (Exception e) {
                // 继续处理其他申请
            }
        }

        sendJsonSuccess(response, "Rejected " + rejectedCount + " applications");
    }

    /**
     * 处理添加内部备注
     */
    private void handleAddNote(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String applicationId = request.getParameter("applicationId");
        String note = request.getParameter("note");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();
        ApplicationService applicationService = getApplicationService();
        if (applicationService == null) {
            sendJsonError(response, "Application service is not available");
            return;
        }

        try {
            Optional<Application> appOpt = applicationService.getApplicationById(applicationId);
            if (!appOpt.isPresent()) {
                sendJsonError(response, "Application not found");
                return;
            }

            Application application = appOpt.get();

            if (!application.getMoId().equals(moId)) {
                sendJsonError(response, "Unauthorized");
                return;
            }

            application.setReviewNote(note);
            applicationService.getApplicationById(applicationId); // 重新保存
            sendJsonSuccess(response, "Note added successfully");

        } catch (Exception e) {
            sendJsonError(response, "Failed to add note: " + e.getMessage());
        }
    }

    private void sendJsonSuccess(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        response.getWriter().write(gson.toJson(result));
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        response.getWriter().write(gson.toJson(result));
    }
}
