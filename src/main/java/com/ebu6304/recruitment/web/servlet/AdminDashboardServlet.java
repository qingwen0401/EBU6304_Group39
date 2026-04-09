package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.JobRepository; // 新增导入 JobRepository

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin 仪表盘 Servlet
 * GET /admin/dashboard → 显示管理员首页（展示系统内的用户总数和职位总数）
 *
 * @author Group39 / Fang Zixi, Guo Jiayi
 * @version 1.1
 */
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        // 获取 UserRepository (用于统计用户)
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");

        // 获取 JobRepository (用于统计职位)
        JobRepository jobRepository =
                (JobRepository) getServletContext().getAttribute("jobRepository");

        // 1. 统计用户总数
        long taCount  = userRepository.findAllTAs().size();
        long moCount  = userRepository.findAllMOs().size();
        long adminCount = userRepository.findAllAdmins().size();
        long totalUsers = taCount + moCount + adminCount; // 系统总人数

        // 2. 统计职位总数
        long totalJobs = jobRepository.findAll().size();

        // 3. 将数据打包发给前端 JSP
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalJobs", totalJobs);
        request.setAttribute("taCount",  taCount);
        request.setAttribute("moCount",  moCount);

        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(request, response);
    }
}