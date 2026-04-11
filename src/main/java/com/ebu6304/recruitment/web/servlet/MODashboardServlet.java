package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.web.AppInitializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MO 仪表盘 Servlet
 * GET /mo/dashboard → 显示 MO 首页及统计数据
 *
 * @author Group39 / Fang Zixi
 * @version 1.0
 */
public class MODashboardServlet extends HttpServlet {

    private JobService jobService;
    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        this.jobService = AppInitializer.getJobService();
        this.applicationService = AppInitializer.getApplicationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        // 获取该MO的所有职位
        List<JobPosting> allJobs = jobService.getAllJobs().stream()
                .filter(job -> job.getMoId().equals(moId))
                .toList();

        // 获取该MO的所有申请
        List<Application> allApplications = applicationService.getAllApplications().stream()
                .filter(app -> app.getMoId().equals(moId))
                .toList();

        // 统计数据
        Map<String, Object> stats = new HashMap<>();

        // 职位统计
        stats.put("totalJobs", allJobs.size());
        stats.put("openJobs", allJobs.stream().filter(JobPosting::isOpen).count());
        stats.put("closedJobs", allJobs.stream()
                .filter(j -> JobPosting.STATUS_CLOSED.equals(j.getStatus())).count());

        // 申请统计
        stats.put("totalApplications", allApplications.size());
        stats.put("pendingApplications", allApplications.stream()
                .filter(app -> Application.STATUS_PENDING.equals(app.getStatus())).count());
        stats.put("reviewingApplications", allApplications.stream()
                .filter(app -> Application.STATUS_REVIEWING.equals(app.getStatus())).count());
        stats.put("acceptedApplications", allApplications.stream()
                .filter(app -> Application.STATUS_ACCEPTED.equals(app.getStatus())).count());
        stats.put("rejectedApplications", allApplications.stream()
                .filter(app -> Application.STATUS_REJECTED.equals(app.getStatus())).count());

        // 名额统计
        int totalVacancies = allJobs.stream().mapToInt(JobPosting::getVacancies).sum();
        int totalFilled = allJobs.stream().mapToInt(JobPosting::getFilledCount).sum();
        stats.put("totalVacancies", totalVacancies);
        stats.put("totalFilled", totalFilled);
        stats.put("fillRate", totalVacancies > 0 ?
                String.format("%.1f", (double) totalFilled / totalVacancies * 100) : "0.0");

        // 最近的申请 (最新5条)
        List<Application> recentApplications = allApplications.stream()
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .limit(5)
                .toList();

        // 需要关注的职位 (有待审核申请的职位)
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

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("stats", stats);
        request.setAttribute("recentApplications", recentApplications);
        request.setAttribute("jobsNeedingAttention", jobsNeedingAttention);
        request.setAttribute("allJobs", allJobs);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(request, response);
    }
}