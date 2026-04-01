package com.ebu6304.recruitment;

import com.ebu6304.recruitment.controllers.ControllerResult;
import com.ebu6304.recruitment.controllers.TAController;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 最小可运行 Web 服务：
 * 1) 提供静态页面
 * 2) 提供 TA CV 上传接口 /api/ta/cv/upload
 */
public class WebServerApp {
    private static final int SERVER_PORT = 8081;
    private static final Gson GSON = new Gson();
    private static final Path CV_ROOT = Paths.get("data/uploads/cv").toAbsolutePath().normalize();

    public static void main(String[] args) throws IOException {
        ApplicationRepository applicationRepository = new ApplicationRepository();
        JobRepository jobRepository = new JobRepository();
        UserRepository userRepository = new UserRepository();
        WorkloadRepository workloadRepository = new WorkloadRepository();

        ApplicationService applicationService = new ApplicationService(
                applicationRepository, jobRepository, userRepository, workloadRepository
        );
        JobService jobService = new JobService(jobRepository, userRepository);
        WorkloadService workloadService = new WorkloadService(workloadRepository, userRepository);
        TAController taController = new TAController(jobService, applicationService, workloadService);

        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        server.createContext("/api/ta/cv/upload", new CvUploadHandler(taController));
        server.createContext("/api/ta/cv/view", new CvViewHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("Web server started at http://localhost:" + SERVER_PORT);
        System.out.println("Open: http://localhost:" + SERVER_PORT + "/ta-edit.html");
    }

    private static class CvUploadHandler implements HttpHandler {
        private final TAController taController;

        private CvUploadHandler(TAController taController) {
            this.taController = taController;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                writeJson(exchange, 405, ControllerResult.failure("Method not allowed"));
                return;
            }

            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            if (contentType == null || !contentType.toLowerCase().startsWith("multipart/form-data")) {
                writeJson(exchange, 400, ControllerResult.failure("Content-Type must be multipart/form-data"));
                return;
            }

            String boundary = extractBoundary(contentType);
            if (boundary == null || boundary.isBlank()) {
                writeJson(exchange, 400, ControllerResult.failure("Missing multipart boundary"));
                return;
            }

            byte[] body = exchange.getRequestBody().readAllBytes();
            MultipartData multipartData;
            try {
                multipartData = parseMultipart(body, boundary);
            } catch (IllegalArgumentException e) {
                writeJson(exchange, 400, ControllerResult.failure("Invalid multipart payload: " + e.getMessage()));
                return;
            }

            String taId = multipartData.fields.getOrDefault("taId", "").trim();
            if (taId.isEmpty()) {
                writeJson(exchange, 400, ControllerResult.failure("taId is required"));
                return;
            }
            if (multipartData.fileName == null || multipartData.fileContent == null) {
                writeJson(exchange, 400, ControllerResult.failure("cvFile is required"));
                return;
            }

            ControllerResult<String> result = taController.uploadCv(taId, multipartData.fileName, multipartData.fileContent);
            int status = result.isSuccess() ? 200 : 400;
            writeJson(exchange, status, result);
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path == null || "/".equals(path)) {
                path = "/ta-edit.html";
            }
            String sanitized = sanitizePath(path);
            if (sanitized == null) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            String resourcePath = "static" + sanitized;
            try (InputStream in = WebServerApp.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (in == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] content = in.readAllBytes();
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", guessContentType(sanitized));
                exchange.sendResponseHeaders(200, content.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(content);
                }
            }
        }
    }

    private static class CvViewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String query = exchange.getRequestURI().getRawQuery();
            String pathParam = getQueryParam(query, "path");
            if (pathParam == null || pathParam.isBlank()) {
                writeJson(exchange, 400, ControllerResult.failure("path is required"));
                return;
            }

            Path requested = Paths.get(pathParam.replace('\\', '/')).toAbsolutePath().normalize();
            if (!requested.startsWith(CV_ROOT)) {
                writeJson(exchange, 403, ControllerResult.failure("Access denied"));
                return;
            }
            String lower = requested.getFileName().toString().toLowerCase();
            if (!(lower.endsWith(".pdf") || lower.endsWith(".doc"))) {
                writeJson(exchange, 400, ControllerResult.failure("Invalid CV file type. Only .pdf or .doc is allowed"));
                return;
            }
            if (!Files.exists(requested) || !Files.isRegularFile(requested)) {
                writeJson(exchange, 404, ControllerResult.failure("CV file not found"));
                return;
            }

            byte[] bytes = Files.readAllBytes(requested);
            String contentType = lower.endsWith(".pdf") ? "application/pdf" : "application/msword";
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", contentType);
            String disposition = lower.endsWith(".pdf") ? "inline" : "attachment";
            headers.set("Content-Disposition", disposition + "; filename=\"" + requested.getFileName() + "\"");

            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private static String sanitizePath(String path) {
        String normalized = path.replace('\\', '/');
        if (normalized.contains("..")) {
            return null;
        }
        return normalized;
    }

    private static String guessContentType(String path) {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) {
            return type;
        }
        if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        return "application/octet-stream";
    }

    private static void writeJson(HttpExchange exchange, int statusCode, Object payload) throws IOException {
        byte[] bytes = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
        addCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String getQueryParam(String rawQuery, String key) {
        if (rawQuery == null || rawQuery.isBlank()) return null;
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            String k = pair.substring(0, eq);
            if (!key.equals(k)) continue;
            String v = pair.substring(eq + 1);
            return java.net.URLDecoder.decode(v, StandardCharsets.UTF_8);
        }
        return null;
    }

    private static String extractBoundary(String contentType) {
        for (String part : contentType.split(";")) {
            String p = part.trim();
            if (p.startsWith("boundary=")) {
                String value = p.substring("boundary=".length());
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }
        return null;
    }

    private static MultipartData parseMultipart(byte[] body, String boundary) {
        byte[] delimiter = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        byte[] headerSep = "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);

        MultipartData data = new MultipartData();
        int cursor = 0;
        while (true) {
            int partStart = indexOf(body, delimiter, cursor);
            if (partStart < 0) {
                break;
            }
            partStart += delimiter.length;
            if (partStart + 1 < body.length && body[partStart] == '-' && body[partStart + 1] == '-') {
                break; // end boundary
            }
            if (partStart + 1 < body.length && body[partStart] == '\r' && body[partStart + 1] == '\n') {
                partStart += 2;
            }

            int nextBoundary = indexOf(body, delimiter, partStart);
            if (nextBoundary < 0) {
                break;
            }
            int partEnd = nextBoundary;
            if (partEnd >= 2 && body[partEnd - 2] == '\r' && body[partEnd - 1] == '\n') {
                partEnd -= 2;
            }

            byte[] part = Arrays.copyOfRange(body, partStart, partEnd);
            int headerEnd = indexOf(part, headerSep, 0);
            if (headerEnd < 0) {
                throw new IllegalArgumentException("part header is missing");
            }

            String headers = new String(part, 0, headerEnd, StandardCharsets.UTF_8);
            byte[] content = Arrays.copyOfRange(part, headerEnd + headerSep.length, part.length);
            String disposition = findHeader(headers, "Content-Disposition");
            if (disposition == null) {
                cursor = nextBoundary;
                continue;
            }
            String name = extractDispositionValue(disposition, "name");
            String fileName = extractDispositionValue(disposition, "filename");
            if (name == null || name.isBlank()) {
                cursor = nextBoundary;
                continue;
            }

            if (fileName != null && !fileName.isBlank()) {
                data.fileName = fileName;
                data.fileContent = content;
            } else {
                data.fields.put(name, new String(content, StandardCharsets.UTF_8).trim());
            }
            cursor = nextBoundary;
        }
        return data;
    }

    private static String findHeader(String headers, String headerName) {
        String[] lines = headers.split("\r\n");
        for (String line : lines) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                if (headerName.equalsIgnoreCase(key)) {
                    return line.substring(idx + 1).trim();
                }
            }
        }
        return null;
    }

    private static String extractDispositionValue(String disposition, String key) {
        String[] segments = disposition.split(";");
        for (String seg : segments) {
            String s = seg.trim();
            if (s.startsWith(key + "=")) {
                String value = s.substring((key + "=").length()).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }
        return null;
    }

    private static int indexOf(byte[] source, byte[] target, int fromIndex) {
        if (target.length == 0) return fromIndex;
        outer:
        for (int i = Math.max(0, fromIndex); i <= source.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static class MultipartData {
        private final Map<String, String> fields = new HashMap<>();
        private String fileName;
        private byte[] fileContent;
    }
}
