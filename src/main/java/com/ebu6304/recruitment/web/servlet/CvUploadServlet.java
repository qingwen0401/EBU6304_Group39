package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.controllers.ControllerResult;
import com.ebu6304.recruitment.controllers.TAController;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 15 * 1024 * 1024
)
public class CvUploadServlet extends HttpServlet {
    private static final Gson GSON = new Gson();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        addCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        addCorsHeaders(resp);

        String taId = req.getParameter("taId");
        if (taId == null || taId.trim().isEmpty()) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, ControllerResult.failure("taId is required"));
            return;
        }

        Part cvPart = req.getPart("cvFile");
        if (cvPart == null || cvPart.getSize() <= 0) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, ControllerResult.failure("cvFile is required"));
            return;
        }

        String fileName = cvPart.getSubmittedFileName();
        if (fileName == null || fileName.isBlank()) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, ControllerResult.failure("cvFile filename is invalid"));
            return;
        }

        byte[] fileContent = cvPart.getInputStream().readAllBytes();
        TAController taController = buildTaController();
        ControllerResult<String> result = taController.uploadCv(taId.trim(), fileName, fileContent);
        int status = result.isSuccess() ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST;
        writeJson(resp, status, result);
    }

    private TAController buildTaController() {
        JobService jobService = (JobService) getServletContext().getAttribute("jobService");
        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");
        WorkloadService workloadService =
                (WorkloadService) getServletContext().getAttribute("workloadService");
        return new TAController(jobService, applicationService, workloadService);
    }

    private void writeJson(HttpServletResponse resp, int statusCode, Object payload) throws IOException {
        byte[] bytes = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
        resp.setStatus(statusCode);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }

    private void addCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
