package com.ebu6304.recruitment.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CvViewServletTest {

    private static final Path CV_ROOT = Paths.get("data/uploads/cv");
    private static final Path LEGACY_ROOT = Paths.get("data/cv");

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CvViewServlet servlet;
    private Path uploadedPdf;
    private Path legacyPdf;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CvViewServlet();
        Files.createDirectories(CV_ROOT);
        Files.createDirectories(LEGACY_ROOT);

        uploadedPdf = CV_ROOT.resolve("cv_view_unit_test.pdf");
        Files.writeString(uploadedPdf, "%PDF-1.4 test");

        legacyPdf = LEGACY_ROOT.resolve("legacy_unit_test.pdf");
        Files.writeString(legacyPdf, "%PDF-1.4 legacy");
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(uploadedPdf);
        Files.deleteIfExists(legacyPdf);
    }

    @Test
    void getMissingPathReturnsBadRequestJson() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(request.getParameter("path")).thenReturn("  ");
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("path is required"));
    }

    @Test
    void getPathTraversalReturnsForbiddenJson() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(request.getParameter("path")).thenReturn("../secrets.pdf");
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("Access denied"));
    }

    @Test
    void getInvalidExtensionReturnsBadRequestJson() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(request.getParameter("path")).thenReturn("bad.txt");
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("Invalid CV file type"));
    }

    @Test
    void getMissingFileReturnsNotFoundJson() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(request.getParameter("path")).thenReturn("does_not_exist.pdf");
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("CV file not found"));
    }

    @Test
    void getServesPdfFromUploadsDirectory() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(request.getParameter("path")).thenReturn(uploadedPdf.getFileName().toString());
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/pdf");
        assertTrue(out.size() > 0);
    }

    @Test
    void getServesPdfFromLegacyCvPrefix() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(request.getParameter("path")).thenReturn("cv/" + legacyPdf.getFileName());
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals("%PDF-1.4 legacy", out.toString(StandardCharsets.UTF_8).trim());
    }

    @Test
    void getServesPdfFromExplicitUploadsPath() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String explicit = "data/uploads/cv/" + uploadedPdf.getFileName();
        when(request.getParameter("path")).thenReturn(explicit);
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(out));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(out.size() > 0);
    }
}
