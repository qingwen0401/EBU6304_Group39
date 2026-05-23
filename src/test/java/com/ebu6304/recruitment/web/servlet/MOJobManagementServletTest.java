package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.NotificationService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MOJobManagementServletTest {

    @Mock private JobService jobService;
    @Mock private ApplicationService applicationService;
    @Mock private NotificationService notificationService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    private MOJobManagementServlet servlet;
    private User moUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new MOJobManagementServlet();

        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute("jobService")).thenReturn(jobService);
        when(context.getAttribute("applicationService")).thenReturn(applicationService);
        when(context.getAttribute("notificationService")).thenReturn(notificationService);

        jakarta.servlet.ServletConfig config = mock(jakarta.servlet.ServletConfig.class);
        when(config.getServletContext()).thenReturn(context);
        lenient().when(config.getServletName()).thenReturn("MOJobManagementServlet");
        servlet.init(config);

        moUser = new User("MO001", "mo.user", "hash", "mo@example.com", "MO", "MO User");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(moUser);
        when(request.getContextPath()).thenReturn("");
    }

    @Nested
    @DisplayName("GET /mo/jobs - 获取职位列表")
    class GetJobs {

        @Test
        @DisplayName("返回MO的职位列表，CANCELLED排在最后")
        void returnsJobsSortedWithCancelledLast() throws Exception {
            JobPosting openJob = job("JOB001", JobPosting.STATUS_OPEN);
            JobPosting cancelledJob = job("JOB002", JobPosting.STATUS_CANCELLED);
            JobPosting closedJob = job("JOB003", JobPosting.STATUS_CLOSED);

            when(jobService.getJobsByMo("MO001"))
                    .thenReturn(new ArrayList<>(List.of(cancelledJob, openJob, closedJob)));
            when(request.getRequestDispatcher("/WEB-INF/jsp/mo/jobs.jsp")).thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).setAttribute(eq("jobs"), argThat(arg -> {
                List<JobPosting> jobs = (List<JobPosting>) arg;
                return jobs.size() == 3
                        && !JobPosting.STATUS_CANCELLED.equals(jobs.get(0).getStatus())
                        && JobPosting.STATUS_CANCELLED.equals(jobs.get(2).getStatus());
            }));
            verify(dispatcher).forward(request, response);
        }

        @Test
        @DisplayName("未登录用户重定向到登录页")
        void redirectsToLoginWhenNotAuthenticated() throws Exception {
            when(request.getSession(false)).thenReturn(null);

            servlet.doGet(request, response);

            verify(response).sendRedirect("/login");
        }

        @Test
        @DisplayName("非MO角色返回403")
        void returnsForbiddenForNonMoRole() throws Exception {
            User taUser = new User("TA001", "ta.user", "hash", "ta@example.com", "TA", "TA User");
            when(session.getAttribute("currentUser")).thenReturn(taUser);

            servlet.doGet(request, response);

            verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
        }
    }

    @Nested
    @DisplayName("POST action=cancel - 取消职位")
    class CancelJob {

        @Test
        @DisplayName("取消职位成功并通知相关TA")
        void cancelJobAndNotifyTAs() throws Exception {
            when(request.getParameter("action")).thenReturn("cancel");
            when(request.getParameter("jobId")).thenReturn("JOB001");

            Application app1 = application("APP001", "TA001");
            Application app2 = application("APP002", "TA002");
            when(applicationService.getApplicationsByJob("JOB001"))
                    .thenReturn(List.of(app1, app2));

            servlet.doPost(request, response);

            verify(jobService).cancelJob("MO001", "JOB001");
            verify(notificationService).createNotification(
                    eq("TA001"), eq("TA"), eq("JOB_CANCELLED"),
                    eq("Job Cancelled Notice"), contains("JOB001"), eq("JOB001"));
            verify(notificationService).createNotification(
                    eq("TA002"), eq("TA"), eq("JOB_CANCELLED"),
                    eq("Job Cancelled Notice"), contains("JOB001"), eq("JOB001"));
            verify(response).sendRedirect("/mo/jobs");
        }

        @Test
        @DisplayName("取消职位失败时设置错误消息")
        void cancelJobFailureSetsErrorMessage() throws Exception {
            when(request.getParameter("action")).thenReturn("cancel");
            when(request.getParameter("jobId")).thenReturn("JOB001");

            doThrow(new IllegalArgumentException("You don't have permission"))
                    .when(jobService).cancelJob("MO001", "JOB001");

            servlet.doPost(request, response);

            verify(response).sendRedirect("/mo/jobs");
        }

        @Test
        @DisplayName("非MO角色POST返回403")
        void postReturnsForbiddenForNonMo() throws Exception {
            User taUser = new User("TA001", "ta.user", "hash", "ta@example.com", "TA", "TA User");
            when(session.getAttribute("currentUser")).thenReturn(taUser);

            servlet.doPost(request, response);

            verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
        }
    }

    private static JobPosting job(String id, String status) {
        JobPosting job = new JobPosting(id, "MO001", "MO User", "EBU6304",
                "SE", "TA Role", "desc", 8, 2, "2026-06-30", "2026 Spring");
        job.setStatus(status);
        job.setPostedAt("2026-05-01T10:00:00");
        return job;
    }

    private static Application application(String id, String taId) {
        return new Application(id, taId, "TA Name", "JOB001",
                "TA Role", "MO001", "Cover letter");
    }
}
