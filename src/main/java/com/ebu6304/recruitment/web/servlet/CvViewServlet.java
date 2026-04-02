package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.controllers.ControllerResult;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CvViewServlet extends HttpServlet {
    private static final Gson GSON = new Gson();
    private static final Path CV_ROOT = Paths.get("data/uploads/cv").toAbsolutePath().normalize();
    private static final Path LEGACY_CV_ROOT = Paths.get("data/cv").toAbsolutePath().normalize();

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        addCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addCorsHeaders(resp);

        String pathParam = req.getParameter("path");
        if (pathParam == null || pathParam.isBlank()) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, ControllerResult.failure("path is required"));
            return;
        }

        Path requested = resolveCvPath(pathParam);
        if (requested == null || !(requested.startsWith(CV_ROOT) || requested.startsWith(LEGACY_CV_ROOT))) {
            writeJson(resp, HttpServletResponse.SC_FORBIDDEN, ControllerResult.failure("Access denied"));
            return;
        }

        String lower = requested.getFileName().toString().toLowerCase();
        if (!(lower.endsWith(".pdf") || lower.endsWith(".doc"))) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ControllerResult.failure("Invalid CV file type. Only .pdf or .doc is allowed"));
            return;
        }
        if (!Files.exists(requested) || !Files.isRegularFile(requested)) {
            writeJson(resp, HttpServletResponse.SC_NOT_FOUND, ControllerResult.failure("CV file not found"));
            return;
        }

        byte[] bytes = Files.readAllBytes(requested);
        String contentType = lower.endsWith(".pdf") ? "application/pdf" : "application/msword";
        String disposition = lower.endsWith(".pdf") ? "inline" : "attachment";

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(contentType);
        resp.setHeader("Content-Disposition", disposition + "; filename=\"" + requested.getFileName() + "\"");
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }

    private Path resolveCvPath(String pathParam) {
        String normalized = pathParam.replace('\\', '/').trim();
        if (normalized.isEmpty() || normalized.contains("..")) {
            return null;
        }

        Path raw = Paths.get(normalized);
        if (raw.isAbsolute()) {
            return raw.normalize();
        }

        if (normalized.startsWith("cv/")) {
            return LEGACY_CV_ROOT.resolve(normalized.substring("cv/".length())).normalize();
        }
        if (normalized.startsWith("data/cv/") || normalized.startsWith("data/uploads/cv/")) {
            return Paths.get(normalized).toAbsolutePath().normalize();
        }
        return CV_ROOT.resolve(normalized).normalize();
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
