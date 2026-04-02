package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin 仪表盘 Servlet
 * GET /admin/dashboard → 显示管理员首页（用户总数统计）
 *
 * @author Group39 / Fang Zixi
 * @version 1.0
 */
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");

        long taCount  = userRepository.findAllTAs().size();
        long moCount  = userRepository.findAllMOs().size();

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("taCount",  taCount);
        request.setAttribute("moCount",  moCount);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(request, response);
    }
}