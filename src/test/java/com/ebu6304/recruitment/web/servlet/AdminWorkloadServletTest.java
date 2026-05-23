package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.NotificationService;
import com.ebu6304.recruitment.services.WorkloadService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminWorkloadServletTest {

    @Mock
    private WorkloadService workloadService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AdminWorkloadServlet servlet;
    private User admin;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminWorkloadServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        lenient().when(context.getAttribute("workloadService")).thenReturn(workloadService);
        lenient().when(context.getAttribute("userRepository")).thenReturn(userRepository);
        lenient().when(context.getAttribute("notificationService")).thenReturn(notificationService);
        lenient().when(context.getAttribute("auditLogRepository")).thenReturn(auditLogRepository);
        admin = AdminServletTestSupport.adminUser();
    }

    @Test
    void getWorkloadPageBuildsReportAndForwards() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        TA ta = ta("TA001", "TA One");
        WorkloadRecord active = record("WL001", "TA001", 18, "ACTIVE");
        Notification warning = new Notification("N001", "TA001", "TA",
                "WORKLOAD_WARNING", "Warning", "Message", "TA001_2026 Spring");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(workloadService.getAllWorkloadRecords()).thenReturn(List.of(active));
        when(workloadService.getMaxWeeklyHours()).thenReturn(20);
        when(workloadService.getWarningWeeklyHours()).thenReturn(15);
        when(userRepository.findAllTAs()).thenReturn(List.of(ta));
        when(notificationService.getWorkloadWarningsForTa("TA001", "2026 Spring"))
                .thenReturn(List.of(warning));
        when(request.getRequestDispatcher("/WEB-INF/jsp/admin/workload.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        ArgumentCaptor<List<Map<String, Object>>> reportCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("workloadReport"), reportCaptor.capture());
        Map<String, Object> row = reportCaptor.getValue().get(0);
        assertEquals("TA001", row.get("taId"));
        assertEquals(18, row.get("totalWeeklyHours"));
        assertEquals("WARNING", row.get("workloadStatus"));
        assertEquals(1, row.get("notificationCount"));
        verify(request).setAttribute("maxWeeklyHours", 20);
        verify(request).setAttribute("warningWeeklyHours", 15);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void getWorkloadPageExportsCsv() throws Exception {
        ByteArrayOutputStream csv = new ByteArrayOutputStream();
        TA ta = ta("TA001", "TA One");
        WorkloadRecord active = record("WL001", "TA001", 24, "ACTIVE");
        when(request.getParameter("export")).thenReturn("csv");
        when(workloadService.getAllWorkloadRecords()).thenReturn(List.of(active));
        when(workloadService.getMaxWeeklyHours()).thenReturn(20);
        when(workloadService.getWarningWeeklyHours()).thenReturn(15);
        when(userRepository.findAllTAs()).thenReturn(List.of(ta));
        when(notificationService.getWorkloadWarningsForTa("TA001", "2026 Spring"))
                .thenReturn(List.of());
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(csv));

        servlet.doGet(request, response);

        verify(response).setContentType("text/csv; charset=UTF-8");
        String body = csv.toString(StandardCharsets.UTF_8);
        assertTrue(body.contains("TA Name,Student ID,Modules,Active Jobs,Weekly Hours,Threshold,Status"));
        assertTrue(body.contains("\"TA One\""));
        assertTrue(body.contains("\"OVERLOADED\""));
    }

    @Test
    void postThresholdUpdateSavesConfigAndAudit() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("maxWeeklyHours")).thenReturn("22");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        servlet.doPost(request, response);

        verify(workloadService).setMaxWeeklyHours(22);
        verify(response).sendRedirect("/admin/workload");
        AuditLogEntry audit = captureAudit();
        assertEquals("CONFIG_UPDATE", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
        assertTrue(audit.getDetails().contains("22 hours"));
    }

    @Test
    void postAssignTaskCreatesAdminWorkloadRecord() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        WorkloadRecord assigned = record("WL777", "TA001", 6, "ACTIVE");
        when(request.getParameter("action")).thenReturn("assignTask");
        when(request.getParameter("taId")).thenReturn("TA001");
        when(request.getParameter("jobTitle")).thenReturn("Tutorial support");
        when(request.getParameter("moduleCode")).thenReturn("EBU6304");
        when(request.getParameter("moId")).thenReturn("MO001");
        when(request.getParameter("weeklyHours")).thenReturn("6");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(workloadService.assignAdminTask("TA001", "Tutorial support", "EBU6304",
                "MO001", 6, "2026 Spring")).thenReturn(assigned);

        servlet.doPost(request, response);

        verify(workloadService).assignAdminTask("TA001", "Tutorial support",
                "EBU6304", "MO001", 6, "2026 Spring");
        AuditLogEntry audit = captureAudit();
        assertEquals("ASSIGN_WORKLOAD", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
    }

    @Test
    void postUpdateTaskAdjustsWorkloadRecord() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        WorkloadRecord updated = record("WL778", "TA002", 10, "ACTIVE");
        when(request.getParameter("action")).thenReturn("updateTask");
        when(request.getParameter("recordId")).thenReturn("WL778");
        when(request.getParameter("taId")).thenReturn("TA002");
        when(request.getParameter("jobTitle")).thenReturn("Adjusted lab");
        when(request.getParameter("moduleCode")).thenReturn("EBU6405");
        when(request.getParameter("moId")).thenReturn("MO002");
        when(request.getParameter("weeklyHours")).thenReturn("10");
        when(request.getParameter("semester")).thenReturn("2026 Autumn");
        when(request.getParameter("recordStatus")).thenReturn("ACTIVE");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(workloadService.updateWorkloadRecord("WL778", "TA002", "Adjusted lab",
                "EBU6405", "MO002", 10, "2026 Autumn", "ACTIVE")).thenReturn(updated);

        servlet.doPost(request, response);

        verify(workloadService).updateWorkloadRecord("WL778", "TA002", "Adjusted lab",
                "EBU6405", "MO002", 10, "2026 Autumn", "ACTIVE");
        assertEquals("UPDATE_WORKLOAD", captureAudit().getAction());
    }

    @Test
    void postNotifyOverloadCreatesWarningAndAudit() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("notifyOverload");
        when(request.getParameter("taId")).thenReturn("TA001");
        when(request.getParameter("taName")).thenReturn("TA One");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(request.getParameter("totalWeeklyHours")).thenReturn("24");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(workloadService.getMaxWeeklyHours()).thenReturn(20);
        when(notificationService.hasWorkloadWarningBeenSent("TA001", "2026 Spring"))
                .thenReturn(false);

        servlet.doPost(request, response);

        verify(notificationService).createWorkloadWarning("TA001", "TA One", 24, 20,
                "2026 Spring");
        verify(response).sendRedirect(contains("/admin/workload?semester=2026+Spring"));
        AuditLogEntry audit = captureAudit();
        assertEquals("SEND_NOTIFICATION", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
    }

    @Test
    void postNotifyOverloadResendRedirectsWithAgainFlag() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("notifyOverload");
        when(request.getParameter("taId")).thenReturn("TA001");
        when(request.getParameter("taName")).thenReturn("TA One");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(request.getParameter("totalWeeklyHours")).thenReturn("24");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(workloadService.getMaxWeeklyHours()).thenReturn(20);
        when(notificationService.hasWorkloadWarningBeenSent("TA001", "2026 Spring"))
                .thenReturn(true);

        servlet.doPost(request, response);

        verify(notificationService).createWorkloadWarning("TA001", "TA One", 24, 20,
                "2026 Spring");
        verify(response).sendRedirect(contains("again=1"));
        AuditLogEntry audit = captureAudit();
        assertEquals("SEND_NOTIFICATION", audit.getAction());
        assertTrue(audit.getDetails().contains("reminder"));
    }

    @Test
    void postForceCancelRequiresThreeWarningsBeforeCancellingRecords() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("forceCancel");
        when(request.getParameterValues("recordId")).thenReturn(new String[]{"WL001"});
        when(request.getParameterValues("moduleCode")).thenReturn(new String[]{"EBU6304"});
        when(request.getParameterValues("moId")).thenReturn(new String[]{"MO001"});
        when(request.getParameter("taId")).thenReturn("TA001");
        when(request.getParameter("taName")).thenReturn("TA One");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(request.getParameter("reason")).thenReturn("Too many hours");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(notificationService.countWorkloadWarningsForTa("TA001", "2026 Spring"))
                .thenReturn(3);

        servlet.doPost(request, response);

        verify(workloadService).cancelWorkloadRecord("WL001");
        verify(notificationService).createWorkloadCancelledNotificationForTA(
                "TA001", "TA One", "EBU6304", "2026 Spring", "Too many hours");
        verify(notificationService).createWorkloadCancelledNotificationForMO(
                "MO001", "TA One", "EBU6304", "2026 Spring", "Too many hours");
        AuditLogEntry audit = captureAudit();
        assertEquals("FORCE_CANCEL_WORKLOAD", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
    }

    @Test
    void postForceCancelFailsWhenWarningsAreInsufficient() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("forceCancel");
        when(request.getParameterValues("recordId")).thenReturn(new String[]{"WL001"});
        when(request.getParameter("taId")).thenReturn("TA001");
        when(request.getParameter("taName")).thenReturn("TA One");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(notificationService.countWorkloadWarningsForTa("TA001", "2026 Spring"))
                .thenReturn(2);

        servlet.doPost(request, response);

        verify(workloadService, never()).cancelWorkloadRecord(anyString());
        AuditLogEntry audit = captureAudit();
        assertEquals("FORCE_CANCEL_WORKLOAD", audit.getAction());
        assertEquals("FAILED", audit.getOutcome());
        assertTrue(audit.getDetails().contains("at least 3 notifications"));
    }

    private AuditLogEntry captureAudit() {
        ArgumentCaptor<AuditLogEntry> captor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepository).save(captor.capture());
        return captor.getValue();
    }

    private static TA ta(String userId, String name) {
        return new TA(userId, userId.toLowerCase(), "hash", userId.toLowerCase() + "@example.com",
                name, "S" + userId, "EECS", "CS");
    }

    private static WorkloadRecord record(String id, String taId, int hours, String status) {
        WorkloadRecord record = new WorkloadRecord(id, taId, "TA One", "JOB001",
                "TA Role", "EBU6304", "MO001", hours, "2026 Spring", "APP001");
        record.setStatus(status);
        record.setCreatedAt("2026-05-01T10:00:00");
        return record;
    }
}
