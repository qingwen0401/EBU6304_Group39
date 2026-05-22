package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminAuditServletTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AdminAuditServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminAuditServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        when(context.getAttribute("auditLogRepository")).thenReturn(auditLogRepository);
    }

    @Test
    void getAuditPageAppliesFiltersAndForwards() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        AuditLogEntry entry = audit("AUD001", "MO_CREATED", "ADMIN", "SUCCESS");
        when(request.getParameter("action")).thenReturn("MO_CREATED");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(request.getParameter("outcome")).thenReturn("SUCCESS");
        when(auditLogRepository.findByFilters("MO_CREATED", "ADMIN", "SUCCESS"))
                .thenReturn(List.of(entry));
        when(request.getRequestDispatcher("/WEB-INF/jsp/admin/audit.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("entries", List.of(entry));
        verify(request).setAttribute("selectedAction", "MO_CREATED");
        verify(request).setAttribute("selectedRole", "ADMIN");
        verify(request).setAttribute("selectedOutcome", "SUCCESS");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void getAuditPageExportsCsv() throws Exception {
        ByteArrayOutputStream csv = new ByteArrayOutputStream();
        AuditLogEntry entry = audit("AUD001", "MO_DELETED", "ADMIN", "SUCCESS");
        when(request.getParameter("export")).thenReturn("csv");
        when(auditLogRepository.findByFilters("", "", "")).thenReturn(List.of(entry));
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(csv));

        servlet.doGet(request, response);

        verify(response).setContentType("text/csv; charset=UTF-8");
        String body = csv.toString(StandardCharsets.UTF_8);
        assertTrue(body.contains("Timestamp,Username,Role,Action,Outcome,IP Address,Details"));
        assertTrue(body.contains("MO_DELETED"));
    }

    private static AuditLogEntry audit(String id, String action, String role, String outcome) {
        return new AuditLogEntry(id, "admin", "ADMIN001", role, action, outcome,
                "127.0.0.1", "details");
    }
}
