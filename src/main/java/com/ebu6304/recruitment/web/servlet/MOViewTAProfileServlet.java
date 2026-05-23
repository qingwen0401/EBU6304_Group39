package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * MO查看TA详细信息Servlet
 * GET /mo/view-ta?taId=xxx → 显示TA的完整profile信息
 *
 * @author Group39
 * @version 1.0
 */
public class MOViewTAProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String taId = request.getParameter("taId");

        if (taId == null || taId.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "TA ID is required");
            return;
        }

        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");

        Optional<TA> taOpt = userRepository.findTAById(taId);
        if (!taOpt.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "TA not found");
            return;
        }

        TA ta = taOpt.get();
        request.setAttribute("ta", ta);
        request.setAttribute("currentUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/view-ta-profile.jsp").forward(request, response);
    }
}
