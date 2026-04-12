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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 申请管理 Servlet
 * GET  /ta/applications → 显示 TA 的申请列表（活跃 + 历史）
 * POST /ta/applications → 撤回申请（AJAX，返回 JSON）
 *
 * @author Group39
 * @version 1.0
 */
public class ApplicationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");

        List<Application> allApps = applicationService.getApplicationsByTA(currentUser.getUserId());

        // 活跃申请：PENDING 或 REVIEWING
        List<Application> activeApps = allApps.stream()
                .filter(a -> Application.STATUS_PENDING.equals(a.getStatus())
                        || Application.STATUS_REVIEWING.equals(a.getStatus()))
                .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
                .collect(Collectors.toList());

        // 历史申请：ACCEPTED / REJECTED / WITHDRAWN
        List<Application> historyApps = allApps.stream()
                .filter(a -> !Application.STATUS_PENDING.equals(a.getStatus())
                        && !Application.STATUS_REVIEWING.equals(a.getStatus()))
                .sorted(Comparator.comparing(Application::getAppliedAt).reversed())
                .collect(Collectors.toList());

        request.setAttribute("activeApps", activeApps);
        request.setAttribute("historyApps", historyApps);
        request.setAttribute("currentUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/jsp/ta/applications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String applicationId = request.getParameter("applicationId");

        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");

        Map<String, Object> result = new HashMap<>();
        try {
            applicationService.withdrawApplication(currentUser.getUserId(), applicationId);
            result.put("success", true);
            result.put("message", "Application withdrawn successfully.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        response.getWriter().write(new Gson().toJson(result));
    }
}
