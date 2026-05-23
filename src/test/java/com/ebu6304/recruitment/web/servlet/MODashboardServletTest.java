package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.NotificationService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MODashboardServletTest {

    @Mock private JobService jobService;
    @Mock private ApplicationService applicationService;
    @Mock private NotificationService notificationService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    private MODashboardServlet servlet;
    private User moUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new MODashboardServlet();
        setField(servlet, "jobService", jobService);
        setField(servlet, "applicationService", applicationService);
        setField(servlet, "notificationService", notificationService);

        moUser = new User("MO001", "mo.user", "hash", "mo@example.com", "MO", "MO User");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(moUser);
        when(request.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp")).thenReturn(dispatcher);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Dashboard统计数据正确聚合")
    void dashboardAggregatesStats() throws Exception {
        JobPosting openJob = job("JOB001", JobPosting.STATUS_OPEN, 2, 1);
        JobPosting closedJob = job("JOB002", JobPosting.STATUS_CLOSED, 3, 3);
        List<JobPosting> jobs = List.of(openJob, closedJob);

        Application pending = application("APP001", Application.STATUS_PENDING, "JOB001");
        Application accepted = application("APP002", Application.STATUS_ACCEPTED, "JOB001");
        Application rejected = application("APP003", Application.STATUS_REJECTED, "JOB002");
        List<Application> apps = new ArrayList<>(List.of(pending, accepted, rejected));

        when(jobService.getJobsByMo("MO001")).thenReturn(jobs);
        when(applicationService.getApplicationsByMo("MO001")).thenReturn(apps);
        when(notificationService.getNotificationsForUser("MO001")).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("stats"), argThat(arg -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) arg;
            return (int) stats.get("totalJobs") == 2
                    && (long) stats.get("openJobs") == 1L
                    && (long) stats.get("pendingApplications") == 1L
                    && (long) stats.get("acceptedApplications") == 1L
                    && (int) stats.get("totalVacancies") == 5
                    && (int) stats.get("totalFilled") == 4;
        }));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Dashboard正确分类通知为已读和未读")
    void dashboardSeparatesReadAndUnreadNotifications() throws Exception {
        when(jobService.getJobsByMo("MO001")).thenReturn(Collections.emptyList());
        when(applicationService.getApplicationsByMo("MO001")).thenReturn(Collections.emptyList());

        Notification unread = new Notification("NOT001", "MO001", "MO",
                "APPLICATION_RECEIVED", "New Application", "msg", "APP001");
        Notification read = new Notification("NOT002", "MO001", "MO",
                "APPLICATION_WITHDRAWN", "Withdrawn", "msg2", "APP002");
        read.setRead(true);

        when(notificationService.getNotificationsForUser("MO001")).thenReturn(List.of(unread, read));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("unreadNotifs"), argThat(arg -> {
            @SuppressWarnings("unchecked")
            List<Notification> list = (List<Notification>) arg;
            return list.size() == 1 && !list.get(0).isRead();
        }));
        verify(request).setAttribute(eq("readNotifs"), argThat(arg -> {
            @SuppressWarnings("unchecked")
            List<Notification> list = (List<Notification>) arg;
            return list.size() == 1 && list.get(0).isRead();
        }));
        verify(request).setAttribute("unreadCount", 1);
        verify(request).setAttribute("readCount", 1);
    }

    @Test
    @DisplayName("Dashboard识别需要关注的职位（有待审核申请）")
    void dashboardIdentifiesJobsNeedingAttention() throws Exception {
        JobPosting jobWithPending = job("JOB001", JobPosting.STATUS_OPEN, 2, 0);
        JobPosting jobNoPending = job("JOB002", JobPosting.STATUS_OPEN, 2, 0);
        List<JobPosting> jobs = List.of(jobWithPending, jobNoPending);

        Application pending = application("APP001", Application.STATUS_PENDING, "JOB001");
        Application accepted = application("APP002", Application.STATUS_ACCEPTED, "JOB002");
        List<Application> apps = new ArrayList<>(List.of(pending, accepted));

        when(jobService.getJobsByMo("MO001")).thenReturn(jobs);
        when(applicationService.getApplicationsByMo("MO001")).thenReturn(apps);
        when(notificationService.getNotificationsForUser("MO001")).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("jobsNeedingAttention"), argThat(arg -> {
            @SuppressWarnings("unchecked")
            List<JobPosting> list = (List<JobPosting>) arg;
            return list.size() == 1 && "JOB001".equals(list.get(0).getJobId());
        }));
    }

    @Test
    @DisplayName("Dashboard在无数据时正常工作")
    void dashboardHandlesEmptyData() throws Exception {
        when(jobService.getJobsByMo("MO001")).thenReturn(Collections.emptyList());
        when(applicationService.getApplicationsByMo("MO001")).thenReturn(Collections.emptyList());
        when(notificationService.getNotificationsForUser("MO001")).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("stats"), argThat(arg -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) arg;
            return (int) stats.get("totalJobs") == 0
                    && "0.0".equals(stats.get("fillRate"));
        }));
        verify(dispatcher).forward(request, response);
    }

    private static JobPosting job(String id, String status, int vacancies, int filled) {
        JobPosting job = new JobPosting(id, "MO001", "MO User", "EBU6304",
                "SE", "TA Role", "desc", 8, vacancies, "2026-06-30", "2026 Spring");
        job.setStatus(status);
        job.setFilledCount(filled);
        job.setPostedAt("2026-05-01T10:00:00");
        return job;
    }

    private static Application application(String id, String status, String jobId) {
        Application app = new Application(id, "TA001", "TA One", jobId,
                "TA Role", "MO001", "Cover letter");
        app.setStatus(status);
        return app;
    }
}
