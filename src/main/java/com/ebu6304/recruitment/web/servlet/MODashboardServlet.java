package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * MO 仪表盘 Servlet
 * GET /mo/dashboard → 显示 MO 首页
 *
 * @author Group39 / Fang Zixi
 * @version 1.0
 */
public class MODashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        request.setAttribute("currentUser", currentUser);
        request.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(request, response);
    }
}