package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 职位市场 Servlet
 * GET  /ta/jobs → 显示开放职位列表
 * POST /ta/jobs → 申请职位（AJAX，返回 JSON）
 *
 * @author Group39
 * @version 1.0
 */
public class JobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        JobService jobService = (JobService) getServletContext().getAttribute("jobService");
        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");

        // 获取所有开放职位
        List<JobPosting> openJobs = jobService.getOpenJobs();

        // 获取该 TA 已申请的职位 ID 集合（用于前端标记"已申请"）
        List<Application> myApps = applicationService.getApplicationsByTA(currentUser.getUserId());
        Set<String> appliedJobIds = myApps.stream()
                .filter(a -> !Application.STATUS_WITHDRAWN.equals(a.getStatus()))
                .map(Application::getJobId)
                .collect(Collectors.toSet());

        request.setAttribute("jobs", openJobs);
        request.setAttribute("appliedJobIds", appliedJobIds);
        request.setAttribute("currentUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/jsp/ta/job-market.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String jobId = request.getParameter("jobId");
        String coverLetter = request.getParameter("coverLetter");
        if (coverLetter == null || coverLetter.trim().isEmpty()) {
            coverLetter = "I am interested in this position and would like to apply.";
        }

        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");

        Map<String, Object> result = new HashMap<>();
        try {
            applicationService.applyForJob(currentUser.getUserId(), jobId, coverLetter, null);
            result.put("success", true);
            result.put("message", "Application submitted successfully!");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        response.getWriter().write(new Gson().toJson(result));
    }
}
