package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.controllers.MOJobController;
import com.ebu6304.recruitment.controllers.ControllerResult;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

public class MOJobManagementServlet extends HttpServlet {

    private JobService jobService;

    @Override
    public void init() throws ServletException {
        jobService = (JobService) getServletContext().getAttribute("jobService");
        if (jobService == null) {
            throw new ServletException("JobService not found in context");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        if (!"MO".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        MOJobController controller = new MOJobController(jobService);
        ControllerResult<List<JobPosting>> result = controller.getMyJobs(user.getUserId());

        if (result.isSuccess()) {
            List<JobPosting> jobs = result.getData();
            // Sort jobs: CANCELLED jobs at the bottom
            jobs.sort((j1, j2) -> {
                boolean isCancelled1 = JobPosting.STATUS_CANCELLED.equals(j1.getStatus());
                boolean isCancelled2 = JobPosting.STATUS_CANCELLED.equals(j2.getStatus());
                if (isCancelled1 && !isCancelled2) return 1;
                if (!isCancelled1 && isCancelled2) return -1;
                return 0;
            });
            request.setAttribute("jobs", jobs);
        } else {
            request.setAttribute("errorMessage", result.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/mo/jobs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        if (!"MO".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        String action = request.getParameter("action");
        String jobId = request.getParameter("jobId");

        if ("cancel".equals(action) && jobId != null) {
            MOJobController controller = new MOJobController(jobService);
            ControllerResult<Void> result = controller.cancelJob(user.getUserId(), jobId);

            if (result.isSuccess()) {
                request.setAttribute("successMessage", "Job cancelled successfully");
            } else {
                request.setAttribute("errorMessage", result.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/mo/jobs");
    }
}
