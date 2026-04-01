package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TA 仪表盘 Servlet
 * GET /ta/dashboard → 显示 TA 仪表盘（统计数据 + 最近申请 + 推荐职位）
 *
 * @author Group39
 * @version 1.0
 */
public class TADashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");
        JobService jobService =
                (JobService) getServletContext().getAttribute("jobService");

        // 获取该 TA 的所有申请
        List<Application> allApps = applicationService.getApplicationsByTA(currentUser.getUserId());

        // 统计待审核申请数
        long activeCount = allApps.stream()
                .filter(a -> Application.STATUS_PENDING.equals(a.getStatus()))
                .count();

        // 最近 5 条申请（按申请时间倒序）
        List<Application> recentApps = allApps.stream()
                .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 获取开放职位（推荐最多 3 个）
        List<JobPosting> openJobs = jobService.getOpenJobs();
        List<JobPosting> recommendedJobs = openJobs.stream()
                .limit(3)
                .collect(Collectors.toList());

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("totalCount", (long) allApps.size());
        request.setAttribute("openJobCount", (long) openJobs.size());
        request.setAttribute("recentApps", recentApps);
        request.setAttribute("recommendedJobs", recommendedJobs);

        request.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(request, response);
    }
}
