package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.WorkloadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Admin 工作量监控 Servlet
 * GET /admin/workload → 显示所有 TA 的工作量及超载状态
 *
 * @author Group39 / Guojiayi
 * @version 1.0
 */
public class AdminWorkloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 从全局上下文中获取队友写好的 WorkloadService
        WorkloadService workloadService =
                (WorkloadService) getServletContext().getAttribute("workloadService");

        // 假设当前学期为 "2026 Spring"（根据你们代码中的设定）
        // 调用底层方法，获取包含所有 TA 工作量状态的详细报告
        List<Map<String, Object>> workloadReport = workloadService.getWorkloadReport("2026 Spring");

        // 将数据打包传给前端页面
        request.setAttribute("workloadReport", workloadReport);

        // 转发到 JSP 页面进行渲染
        request.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp").forward(request, response);
    }
}