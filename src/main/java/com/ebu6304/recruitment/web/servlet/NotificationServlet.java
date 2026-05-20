package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.NotificationService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知管理 Servlet
 * 处理通知相关的异步请求，例如标记为已读
 */
public class NotificationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 设置请求和响应的编码格式为 JSON
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 获取前端传来的通知 ID
        String notificationId = request.getParameter("notificationId");

        // 从全局上下文中获取 NotificationService
        NotificationService notificationService =
                (NotificationService) getServletContext().getAttribute("notificationService");

        Map<String, Object> result = new HashMap<>();

        try {
            if (notificationId != null && !notificationId.trim().isEmpty()) {
                // 调用 Service 层将该通知在 JSON 数据中标记为已读
                notificationService.markAsRead(notificationId);
                result.put("success", true);
                result.put("message", "Notification marked as read.");
            } else {
                result.put("success", false);
                result.put("message", "Notification ID is missing.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Failed to mark notification: " + e.getMessage());
        }

        // 返回 JSON 结果给前端
        response.getWriter().write(new Gson().toJson(result));
    }
}