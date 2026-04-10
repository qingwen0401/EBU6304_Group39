package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.controllers.ControllerResult;
import com.ebu6304.recruitment.controllers.MOJobController;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.JobService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MOCreateJobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        JobService jobService = (JobService) getServletContext().getAttribute("jobService");
        MOJobController controller = new MOJobController(jobService);

        ControllerResult<List<JobPosting>> result = controller.getMyJobs(currentUser.getUserId());

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("myJobs", result.isSuccess() ? result.getData() : Collections.emptyList());

        request.getRequestDispatcher("/WEB-INF/jsp/mo/create-job.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String moduleCode = request.getParameter("moduleCode");
        String moduleName = request.getParameter("moduleName");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String skillsText = request.getParameter("requiredSkills");
        String hoursPerWeekText = request.getParameter("hoursPerWeek");
        String vacanciesText = request.getParameter("vacancies");
        String deadline = request.getParameter("deadline");
        String semester = request.getParameter("semester");
        String jobType = request.getParameter("jobType");
        String minGpaText = request.getParameter("minGpa");
        String hourlyRateText = request.getParameter("hourlyRate");

        request.setAttribute("moduleCode", moduleCode);
        request.setAttribute("moduleName", moduleName);
        request.setAttribute("title", title);
        request.setAttribute("description", description);
        request.setAttribute("requiredSkills", skillsText);
        request.setAttribute("hoursPerWeek", hoursPerWeekText);
        request.setAttribute("vacancies", vacanciesText);
        request.setAttribute("deadline", deadline);
        request.setAttribute("semester", semester);
        request.setAttribute("jobType", jobType);
        request.setAttribute("minGpa", minGpaText);
        request.setAttribute("hourlyRate", hourlyRateText);

        try {
            List<String> requiredSkills = Arrays.stream(skillsText.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            int hoursPerWeek = Integer.parseInt(hoursPerWeekText);
            int vacancies = Integer.parseInt(vacanciesText);
            double minGpa = Double.parseDouble(minGpaText);
            double hourlyRate = Double.parseDouble(hourlyRateText);

            JobService jobService = (JobService) getServletContext().getAttribute("jobService");
            MOJobController controller = new MOJobController(jobService);

            ControllerResult<JobPosting> result = controller.postJob(
                    currentUser.getUserId(),
                    moduleCode,
                    moduleName,
                    title,
                    description,
                    requiredSkills,
                    hoursPerWeek,
                    vacancies,
                    deadline,
                    semester,
                    jobType,
                    minGpa,
                    hourlyRate
            );

            if (result.isSuccess()) {
                request.setAttribute("successMessage", result.getMessage());

                request.setAttribute("moduleCode", "");
                request.setAttribute("moduleName", "");
                request.setAttribute("title", "");
                request.setAttribute("description", "");
                request.setAttribute("requiredSkills", "");
                request.setAttribute("hoursPerWeek", "");
                request.setAttribute("vacancies", "");
                request.setAttribute("deadline", "");
                request.setAttribute("semester", "");
                request.setAttribute("jobType", "");
                request.setAttribute("minGpa", "");
                request.setAttribute("hourlyRate", "");
            } else {
                request.setAttribute("errorMessage", result.getMessage());
            }

            ControllerResult<List<JobPosting>> jobsResult = controller.getMyJobs(currentUser.getUserId());
            request.setAttribute("currentUser", currentUser);
            request.setAttribute("myJobs", jobsResult.isSuccess() ? jobsResult.getData() : Collections.emptyList());

            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-job.jsp").forward(request, response);

        } catch (Exception e) {
            JobService jobService = (JobService) getServletContext().getAttribute("jobService");
            MOJobController controller = new MOJobController(jobService);

            ControllerResult<List<JobPosting>> jobsResult = controller.getMyJobs(currentUser.getUserId());

            request.setAttribute("currentUser", currentUser);
            request.setAttribute("myJobs", jobsResult.isSuccess() ? jobsResult.getData() : Collections.emptyList());
            request.setAttribute("errorMessage", "Please enter valid values for skills, hours, vacancies, GPA and hourly rate.");

            request.getRequestDispatcher("/WEB-INF/jsp/mo/create-job.jsp").forward(request, response);
        }
    }
}