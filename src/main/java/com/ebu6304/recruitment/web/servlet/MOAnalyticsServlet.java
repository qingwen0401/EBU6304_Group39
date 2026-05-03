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

/**
 * MO数据分析Servlet
 * GET /mo/analytics → 显示招聘数据分析页面
 * 提供录取率、填充率、申请趋势等统计数据
 *
 * @author Group39
 * @version 1.0
 */
public class MOAnalyticsServlet extends HttpServlet {

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
        List<JobPosting> jobs = jobService.getJobsByMo(moId);

        // Sort jobs: CANCELLED jobs at the bottom
        jobs.sort((j1, j2) -> {
            boolean isCancelled1 = JobPosting.STATUS_CANCELLED.equals(j1.getStatus());
            boolean isCancelled2 = JobPosting.STATUS_CANCELLED.equals(j2.getStatus());
            if (isCancelled1 && !isCancelled2) return 1;
            if (!isCancelled1 && isCancelled2) return -1;
            return 0;
        });

        // 统计数据
        Map<String, Object> analytics = calculateAnalytics(jobs, moId);

        request.setAttribute("analytics", analytics);
        request.setAttribute("jobs", jobs);
        request.setAttribute("currentUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/analytics.jsp").forward(request, response);
    }

    /**
     * 计算招聘分析数据
     */
    private Map<String, Object> calculateAnalytics(List<JobPosting> jobs, String moId) {
        Map<String, Object> analytics = new HashMap<>();

        // 基础统计
        int totalJobs = jobs.size();
        int openJobs = (int) jobs.stream().filter(JobPosting::isOpen).count();
        int closedJobs = (int) jobs.stream().filter(j -> JobPosting.STATUS_CLOSED.equals(j.getStatus())).count();
        int cancelledJobs = (int) jobs.stream().filter(j -> JobPosting.STATUS_CANCELLED.equals(j.getStatus())).count();

        // 职位统计
        int totalVacancies = jobs.stream().mapToInt(JobPosting::getVacancies).sum();
        int totalFilled = jobs.stream().mapToInt(JobPosting::getFilledCount).sum();
        double fillRate = totalVacancies > 0 ? (double) totalFilled / totalVacancies * 100 : 0;

        // 申请统计 - 获取该MO所有职位的申请
        List<Application> allApplications = applicationService.getApplicationsByMo(moId);

        int totalApplications = allApplications.size();
        int pendingApplications = (int) allApplications.stream()
                .filter(app -> Application.STATUS_PENDING.equals(app.getStatus())).count();
        int acceptedApplications = (int) allApplications.stream()
                .filter(app -> Application.STATUS_ACCEPTED.equals(app.getStatus())).count();
        int rejectedApplications = (int) allApplications.stream()
                .filter(app -> Application.STATUS_REJECTED.equals(app.getStatus())).count();

        // 录取率
        double acceptanceRate = totalApplications > 0 ?
                (double) acceptedApplications / totalApplications * 100 : 0;

        // 按职位分组的统计
        Map<String, Map<String, Object>> jobStats = new HashMap<>();
        for (JobPosting job : jobs) {
            Map<String, Object> stat = new HashMap<>();
            List<Application> jobApps = allApplications.stream()
                    .filter(app -> app.getJobId().equals(job.getJobId()))
                    .toList();

            stat.put("totalApplications", jobApps.size());
            stat.put("accepted", jobApps.stream().filter(Application::isAccepted).count());
            stat.put("pending", jobApps.stream().filter(Application::isPending).count());
            stat.put("fillRate", job.getVacancies() > 0 ?
                    (double) job.getFilledCount() / job.getVacancies() * 100 : 0);

            jobStats.put(job.getJobId(), stat);
        }

        // 饼图数据 - 申请状态分布
        Map<String, Integer> applicationStatusData = new HashMap<>();
        applicationStatusData.put("Pending", pendingApplications);
        applicationStatusData.put("Accepted", acceptedApplications);
        applicationStatusData.put("Rejected", rejectedApplications);

        // 饼图数据 - 职位状态分布
        Map<String, Integer> jobStatusData = new HashMap<>();
        jobStatusData.put("Open", openJobs);
        jobStatusData.put("Closed", closedJobs);
        jobStatusData.put("Cancelled", cancelledJobs);

        // 组装结果
        analytics.put("totalJobs", totalJobs);
        analytics.put("openJobs", openJobs);
        analytics.put("closedJobs", closedJobs);
        analytics.put("cancelledJobs", cancelledJobs);
        analytics.put("totalVacancies", totalVacancies);
        analytics.put("totalFilled", totalFilled);
        analytics.put("fillRate", String.format("%.1f", fillRate));
        analytics.put("totalApplications", totalApplications);
        analytics.put("pendingApplications", pendingApplications);
        analytics.put("acceptedApplications", acceptedApplications);
        analytics.put("rejectedApplications", rejectedApplications);
        analytics.put("acceptanceRate", String.format("%.1f", acceptanceRate));
        analytics.put("jobStats", jobStats);
        analytics.put("applicationStatusData", applicationStatusData);
        analytics.put("jobStatusData", jobStatusData);

        return analytics;
    }
}
