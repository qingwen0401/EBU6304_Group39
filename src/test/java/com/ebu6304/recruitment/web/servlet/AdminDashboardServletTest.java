package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.WorkloadService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminDashboardServletTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private WorkloadService workloadService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AdminDashboardServlet servlet;
    private User admin;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminDashboardServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        when(context.getAttribute("userRepository")).thenReturn(userRepository);
        when(context.getAttribute("jobRepository")).thenReturn(jobRepository);
        when(context.getAttribute("applicationRepository")).thenReturn(applicationRepository);
        when(context.getAttribute("workloadService")).thenReturn(workloadService);
        when(context.getAttribute("auditLogRepository")).thenReturn(auditLogRepository);
        admin = AdminServletTestSupport.adminUser();
    }

    @Test
    void getDashboardAggregatesAdminSummaryStats() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        AdminServletTestSupport.withCurrentUser(request, admin);
        TA activeTa = new TA("TA001", "ta.one", "hash", "ta@example.com",
                "TA One", "S001", "EECS", "CS");
        ModuleOrganiser inactiveMo = new ModuleOrganiser("MO001", "mo.one", "hash",
                "mo@example.com", "MO One", "EECS", "EBU6304", "Software Engineering");
        inactiveMo.setActive(false);
        JobPosting openJob = job("JOB001", JobPosting.STATUS_OPEN);
        JobPosting closedJob = job("JOB002", JobPosting.STATUS_CLOSED);
        Application pending = application("APP001", Application.STATUS_PENDING);
        Application accepted = application("APP002", Application.STATUS_ACCEPTED);
        WorkloadRecord workloadRecord = workloadRecord();
        AuditLogEntry audit = new AuditLogEntry("AUD001", "admin", "ADMIN001", "ADMIN",
                "MO_CREATED", "SUCCESS", "127.0.0.1", "Created MO");
        when(jobRepository.findAll()).thenReturn(new ArrayList<>(List.of(openJob, closedJob)));
        when(applicationRepository.findAll()).thenReturn(List.of(pending, accepted));
        when(workloadService.getAllWorkloadRecords()).thenReturn(List.of(workloadRecord));
        when(workloadService.getWorkloadReport("2026 Spring")).thenReturn(List.of(
                Map.of("totalWeeklyHours", 24, "isOverloaded", true),
                Map.of("totalWeeklyHours", 8, "isOverloaded", false)));
        when(workloadService.getMaxWeeklyHours()).thenReturn(20);
        when(userRepository.findAllTAs()).thenReturn(List.of(activeTa));
        when(userRepository.findAllMOs()).thenReturn(List.of(inactiveMo));
        when(userRepository.findAllAdmins()).thenReturn(List.of(admin));
        when(auditLogRepository.findAll()).thenReturn(List.of(audit));
        when(request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp"))
                .thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("totalUsers", 3L);
        verify(request).setAttribute("activeUsers", 2L);
        verify(request).setAttribute("inactiveUsers", 1L);
        verify(request).setAttribute("taCount", 1L);
        verify(request).setAttribute("moCount", 1L);
        verify(request).setAttribute("adminCount", 1L);
        verify(request).setAttribute("totalJobs", 2);
        verify(request).setAttribute("openJobs", 1L);
        verify(request).setAttribute("closedJobs", 1L);
        verify(request).setAttribute("totalWorkloadHours", 32L);
        verify(request).setAttribute("overloadedCount", 1L);
        verify(request).setAttribute("recentAudit", List.of(audit));
        verify(dispatcher).forward(request, response);
    }

    private static JobPosting job(String id, String status) {
        JobPosting job = new JobPosting(id, "MO001", "MO One", "EBU6304",
                "Software Engineering", "TA Role", "Support labs", 8, 2,
                "2026-06-30", "2026 Spring");
        job.setStatus(status);
        job.setPostedAt("2026-05-01T10:00:00");
        return job;
    }

    private static Application application(String id, String status) {
        Application app = new Application(id, "TA001", "TA One", "JOB001",
                "TA Role", "MO001", "I can help.");
        app.setStatus(status);
        return app;
    }

    private static WorkloadRecord workloadRecord() {
        WorkloadRecord record = new WorkloadRecord("WL001", "TA001", "TA One",
                "JOB001", "TA Role", "EBU6304", "MO001", 8, "2026 Spring", "APP001");
        record.setCreatedAt("2026-05-01T10:00:00");
        return record;
    }
}
