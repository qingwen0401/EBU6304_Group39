package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.JobTemplate;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.web.AppInitializer;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MO模板库Servlet
 * GET /mo/templates → 显示所有已发布职位作为模板
 * POST /mo/templates/use → 使用模板创建新职位
 * POST /mo/templates/delete → 删除职位
 *
 * @author Group39
 * @version 1.0
 */
public class MOTemplateServlet extends HttpServlet {

    private JobService jobService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.jobService = AppInitializer.getJobService();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        // 从该MO的所有职位生成模板列表
        List<JobPosting> myJobs = jobService.getJobsByMo(moId);
        List<JobTemplate> templates = new ArrayList<>();

        for (JobPosting job : myJobs) {
            JobTemplate template = new JobTemplate(job.getJobId(), job.getTitle(), job);
            templates.add(template);
        }

        request.setAttribute("templates", templates);
        request.setAttribute("currentUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/templates.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("use".equals(action)) {
            handleUseTemplate(request, response);
        } else if ("delete".equals(action)) {
            handleDeleteTemplate(request, response);
        } else {
            sendJsonError(response, "Unknown action");
        }
    }

    /**
     * 使用模板创建新职位（基于已有职位）
     */
    private void handleUseTemplate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String jobId = request.getParameter("templateId"); // templateId 实际上是 jobId
        String vacancies = request.getParameter("vacancies");
        String deadline = request.getParameter("deadline");
        String semester = request.getParameter("semester");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        try {
            Optional<JobPosting> jobOpt = jobService.getJobById(jobId);
            if (!jobOpt.isPresent()) {
                sendJsonError(response, "Job not found");
                return;
            }

            JobPosting job = jobOpt.get();

            if (!job.getMoId().equals(moId)) {
                sendJsonError(response, "Unauthorized");
                return;
            }

            // 从职位创建临时模板对象
            JobTemplate template = new JobTemplate(job.getJobId(), job.getTitle(), job);

            // 将模板数据存入 session，供 create-job 页面使用
            request.getSession().setAttribute("templateData", template);
            request.getSession().setAttribute("templateVacancies", vacancies);
            request.getSession().setAttribute("templateDeadline", deadline);
            request.getSession().setAttribute("templateSemester", semester);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Template loaded successfully");
            result.put("redirect", true);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            sendJsonError(response, "Failed to use template: " + e.getMessage());
        }
    }

    /**
     * 删除模板（实际上是关闭职位）
     */
    private void handleDeleteTemplate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String jobId = request.getParameter("templateId"); // templateId 实际上是 jobId

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        try {
            Optional<JobPosting> jobOpt = jobService.getJobById(jobId);
            if (!jobOpt.isPresent()) {
                sendJsonError(response, "Job not found");
                return;
            }

            JobPosting job = jobOpt.get();
            if (!job.getMoId().equals(moId)) {
                sendJsonError(response, "Unauthorized");
                return;
            }

            // 关闭职位而不是删除
            jobService.closeJob(moId, jobId);
            sendJsonSuccess(response, "Job closed successfully");

        } catch (Exception e) {
            sendJsonError(response, "Failed to close job: " + e.getMessage());
        }
    }

    private void sendJsonSuccess(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        response.getWriter().write(gson.toJson(result));
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        response.getWriter().write(gson.toJson(result));
    }
}
