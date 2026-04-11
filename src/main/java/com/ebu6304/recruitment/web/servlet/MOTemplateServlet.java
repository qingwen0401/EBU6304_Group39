package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.JobTemplate;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.utils.IdGenerator;
import com.ebu6304.recruitment.utils.JsonFileUtil;
import com.ebu6304.recruitment.web.AppInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MO模板库Servlet
 * GET /mo/templates → 显示模板库页面
 * POST /mo/templates/save → 保存职位为模板
 * POST /mo/templates/use → 使用模板创建新职位
 * POST /mo/templates/delete → 删除模板
 *
 * @author Group39
 * @version 1.0
 */
public class MOTemplateServlet extends HttpServlet {

    private static final String TEMPLATES_FILE = "data/job_templates.json";
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

        // 加载该MO的所有模板
        List<JobTemplate> templates = loadTemplates(moId);

        request.setAttribute("templates", templates);
        request.setAttribute("currentUser", currentUser);

        request.getRequestDispatcher("/WEB-INF/jsp/mo/templates.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("save".equals(action)) {
            handleSaveTemplate(request, response);
        } else if ("use".equals(action)) {
            handleUseTemplate(request, response);
        } else if ("delete".equals(action)) {
            handleDeleteTemplate(request, response);
        } else {
            sendJsonError(response, "Unknown action");
        }
    }

    /**
     * 保存职位为模板
     */
    private void handleSaveTemplate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String jobId = request.getParameter("jobId");
        String templateName = request.getParameter("templateName");

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

            // 创建模板
            String templateId = IdGenerator.generateTemplateId();
            JobTemplate template = new JobTemplate(templateId, templateName, job);

            // 保存模板
            List<JobTemplate> templates = loadAllTemplates();
            templates.add(template);
            saveAllTemplates(templates);

            sendJsonSuccess(response, "Template saved successfully");

        } catch (Exception e) {
            sendJsonError(response, "Failed to save template: " + e.getMessage());
        }
    }

    /**
     * 使用模板创建新职位
     */
    private void handleUseTemplate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String templateId = request.getParameter("templateId");
        String vacancies = request.getParameter("vacancies");
        String deadline = request.getParameter("deadline");
        String semester = request.getParameter("semester");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        try {
            JobTemplate template = findTemplateById(templateId);
            if (template == null) {
                sendJsonError(response, "Template not found");
                return;
            }

            if (!template.getMoId().equals(moId)) {
                sendJsonError(response, "Unauthorized");
                return;
            }

            // 从模板创建新职位
            String jobId = IdGenerator.generateJobId();
            JobPosting newJob = new JobPosting(
                    jobId,
                    moId,
                    currentUser.getFullName(),
                    template.getModuleCode(),
                    template.getModuleName(),
                    template.getTitle(),
                    template.getDescription(),
                    template.getHoursPerWeek(),
                    Integer.parseInt(vacancies),
                    deadline,
                    semester
            );

            newJob.setRequiredSkills(new ArrayList<>(template.getRequiredSkills()));
            newJob.setJobType(template.getJobType());
            newJob.setMinGpa(template.getMinGpa());
            newJob.setHourlyRate(template.getHourlyRate());

            // 保存新职位 - 使用 postJob 方法
            jobService.postJob(
                    moId,
                    template.getModuleCode(),
                    template.getModuleName(),
                    template.getTitle(),
                    template.getDescription(),
                    new ArrayList<>(template.getRequiredSkills()),
                    template.getHoursPerWeek(),
                    Integer.parseInt(vacancies),
                    deadline,
                    semester,
                    template.getJobType(),
                    template.getMinGpa(),
                    template.getHourlyRate()
            );

            // 更新模板使用次数
            template.recordUsage();
            updateTemplate(template);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Job created from template successfully");
            result.put("jobId", "created");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            sendJsonError(response, "Failed to use template: " + e.getMessage());
        }
    }

    /**
     * 删除模板
     */
    private void handleDeleteTemplate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String templateId = request.getParameter("templateId");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        String moId = currentUser.getUserId();

        try {
            List<JobTemplate> templates = loadAllTemplates();
            boolean removed = templates.removeIf(t ->
                    t.getTemplateId().equals(templateId) && t.getMoId().equals(moId));

            if (removed) {
                saveAllTemplates(templates);
                sendJsonSuccess(response, "Template deleted successfully");
            } else {
                sendJsonError(response, "Template not found or unauthorized");
            }

        } catch (Exception e) {
            sendJsonError(response, "Failed to delete template: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private List<JobTemplate> loadAllTemplates() {
        Type listType = new TypeToken<List<JobTemplate>>() {}.getType();
        List<JobTemplate> templates = JsonFileUtil.readList(TEMPLATES_FILE, JobTemplate.class);
        return templates != null ? templates : new ArrayList<>();
    }

    private List<JobTemplate> loadTemplates(String moId) {
        return loadAllTemplates().stream()
                .filter(t -> t.getMoId().equals(moId))
                .toList();
    }

    private void saveAllTemplates(List<JobTemplate> templates) {
        JsonFileUtil.writeList(TEMPLATES_FILE, templates);
    }

    private JobTemplate findTemplateById(String templateId) {
        return loadAllTemplates().stream()
                .filter(t -> t.getTemplateId().equals(templateId))
                .findFirst()
                .orElse(null);
    }

    private void updateTemplate(JobTemplate template) {
        List<JobTemplate> templates = loadAllTemplates();
        for (int i = 0; i < templates.size(); i++) {
            if (templates.get(i).getTemplateId().equals(template.getTemplateId())) {
                templates.set(i, template);
                break;
            }
        }
        saveAllTemplates(templates);
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
