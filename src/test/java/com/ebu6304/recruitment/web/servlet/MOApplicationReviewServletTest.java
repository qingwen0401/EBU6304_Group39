package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.ApplicationService;
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MOApplicationReviewServletTest {

    @Mock private ApplicationService applicationService;
    @Mock private JobRepository jobRepository;
    @Mock private UserRepository userRepository;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher dispatcher;

    private MOApplicationReviewServlet servlet;
    private User moUser;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new MOApplicationReviewServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        when(context.getAttribute("applicationService")).thenReturn(applicationService);
        when(context.getAttribute("jobRepository")).thenReturn(jobRepository);
        when(context.getAttribute("userRepository")).thenReturn(userRepository);

        moUser = new User("MO001", "mo.user", "hash", "mo@example.com", "MO", "MO User");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(moUser);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Nested
    @DisplayName("GET /mo/applications - 获取申请列表")
    class GetApplications {

        @Test
        @DisplayName("按状态分类返回所有申请")
        void returnsApplicationsGroupedByStatus() throws Exception {
            when(request.getParameter("action")).thenReturn(null);
            when(request.getParameter("jobId")).thenReturn(null);

            Application pending = application("APP001", Application.STATUS_PENDING);
            Application accepted = application("APP002", Application.STATUS_ACCEPTED);
            Application rejected = application("APP003", Application.STATUS_REJECTED);
            when(applicationService.getApplicationsByMo("MO001"))
                    .thenReturn(List.of(pending, accepted, rejected));
            when(request.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp"))
                    .thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).setAttribute(eq("applications"), anyList());
            verify(request).setAttribute(eq("applicationsByStatus"), anyMap());
            verify(dispatcher).forward(request, response);
        }

        @Test
        @DisplayName("按jobId过滤申请")
        void filtersApplicationsByJobId() throws Exception {
            when(request.getParameter("action")).thenReturn(null);
            when(request.getParameter("jobId")).thenReturn("JOB001");

            Application app1 = application("APP001", Application.STATUS_PENDING);
            app1.setJobId("JOB001");
            Application app2 = application("APP002", Application.STATUS_PENDING);
            app2.setJobId("JOB002");
            when(applicationService.getApplicationsByMo("MO001"))
                    .thenReturn(new ArrayList<>(List.of(app1, app2)));
            when(request.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp"))
                    .thenReturn(dispatcher);

            servlet.doGet(request, response);

            verify(request).setAttribute(eq("filterJobId"), eq("JOB001"));
            verify(dispatcher).forward(request, response);
        }

        @Test
        @DisplayName("action=get-jobs 返回MO的职位列表JSON")
        void getJobsReturnsJsonList() throws Exception {
            when(request.getParameter("action")).thenReturn("get-jobs");

            JobPosting job = new JobPosting("JOB001", "MO001", "MO User",
                    "EBU6304", "SE", "TA Role", "desc", 8, 2, "2026-06-30", "2026 Spring");
            when(jobRepository.findByMoId("MO001")).thenReturn(List.of(job));

            servlet.doGet(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("JOB001"));
            assertTrue(json.contains("TA Role"));
            verify(response).setContentType("application/json");
        }
    }

    @Nested
    @DisplayName("POST action=review - 审查单个申请")
    class ReviewApplication {

        @Test
        @DisplayName("接受申请成功")
        void acceptApplicationSuccessfully() throws Exception {
            when(request.getParameter("action")).thenReturn("review");
            when(request.getParameter("applicationId")).thenReturn("APP001");
            when(request.getParameter("decision")).thenReturn("accept");
            when(request.getParameter("note")).thenReturn("Good candidate");

            Application app = application("APP001", Application.STATUS_PENDING);
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app));

            servlet.doPost(request, response);

            verify(applicationService).acceptApplication("MO001", "APP001", "Good candidate");
            String json = responseWriter.toString();
            assertTrue(json.contains("\"success\":true"));
        }

        @Test
        @DisplayName("拒绝申请成功")
        void rejectApplicationSuccessfully() throws Exception {
            when(request.getParameter("action")).thenReturn("review");
            when(request.getParameter("applicationId")).thenReturn("APP001");
            when(request.getParameter("decision")).thenReturn("reject");
            when(request.getParameter("note")).thenReturn("Not qualified");

            Application app = application("APP001", Application.STATUS_PENDING);
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app));

            servlet.doPost(request, response);

            verify(applicationService).rejectApplication("MO001", "APP001", "Not qualified");
            String json = responseWriter.toString();
            assertTrue(json.contains("\"success\":true"));
        }

        @Test
        @DisplayName("申请不存在返回错误")
        void applicationNotFoundReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("review");
            when(request.getParameter("applicationId")).thenReturn("APP999");
            when(request.getParameter("decision")).thenReturn("accept");

            when(applicationService.getApplicationById("APP999")).thenReturn(Optional.empty());

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("\"success\":false"));
            assertTrue(json.contains("Application not found"));
        }

        @Test
        @DisplayName("无权审查他人的申请")
        void unauthorizedReviewReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("review");
            when(request.getParameter("applicationId")).thenReturn("APP001");
            when(request.getParameter("decision")).thenReturn("accept");

            Application app = application("APP001", Application.STATUS_PENDING);
            app.setMoId("MO_OTHER");
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app));

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("Unauthorized"));
        }

        @Test
        @DisplayName("无效的decision返回错误")
        void invalidDecisionReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("review");
            when(request.getParameter("applicationId")).thenReturn("APP001");
            when(request.getParameter("decision")).thenReturn("maybe");

            Application app = application("APP001", Application.STATUS_PENDING);
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app));

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("Invalid decision"));
        }
    }

    @Nested
    @DisplayName("POST action=bulk-reject - 批量拒绝")
    class BulkReject {

        @Test
        @DisplayName("批量拒绝多个申请成功")
        void bulkRejectMultipleApplications() throws Exception {
            when(request.getParameter("action")).thenReturn("bulk-reject");
            when(request.getParameterValues("applicationIds[]"))
                    .thenReturn(new String[]{"APP001", "APP002"});
            when(request.getParameter("note")).thenReturn("Position filled");

            Application app1 = application("APP001", Application.STATUS_PENDING);
            Application app2 = application("APP002", Application.STATUS_PENDING);
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app1));
            when(applicationService.getApplicationById("APP002")).thenReturn(Optional.of(app2));

            servlet.doPost(request, response);

            verify(applicationService).rejectApplication("MO001", "APP001", "Position filled");
            verify(applicationService).rejectApplication("MO001", "APP002", "Position filled");
            String json = responseWriter.toString();
            assertTrue(json.contains("Rejected 2 applications"));
        }

        @Test
        @DisplayName("未选择申请返回错误")
        void noApplicationsSelectedReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("bulk-reject");
            when(request.getParameterValues("applicationIds[]")).thenReturn(null);

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("No applications selected"));
        }

        @Test
        @DisplayName("跳过不属于当前MO的申请")
        void skipsUnauthorizedApplications() throws Exception {
            when(request.getParameter("action")).thenReturn("bulk-reject");
            when(request.getParameterValues("applicationIds[]"))
                    .thenReturn(new String[]{"APP001", "APP002"});
            when(request.getParameter("note")).thenReturn(null);

            Application app1 = application("APP001", Application.STATUS_PENDING);
            Application app2 = application("APP002", Application.STATUS_PENDING);
            app2.setMoId("MO_OTHER");
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app1));
            when(applicationService.getApplicationById("APP002")).thenReturn(Optional.of(app2));

            servlet.doPost(request, response);

            verify(applicationService).rejectApplication("MO001", "APP001", "Bulk rejection");
            verify(applicationService, never()).rejectApplication(eq("MO001"), eq("APP002"), any());
            String json = responseWriter.toString();
            assertTrue(json.contains("Rejected 1 applications"));
        }
    }

    @Nested
    @DisplayName("POST action=add-note - 添加备注")
    class AddNote {

        @Test
        @DisplayName("添加备注成功")
        void addNoteSuccessfully() throws Exception {
            when(request.getParameter("action")).thenReturn("add-note");
            when(request.getParameter("applicationId")).thenReturn("APP001");
            when(request.getParameter("note")).thenReturn("Internal note");

            Application app = application("APP001", Application.STATUS_PENDING);
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app));

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("\"success\":true"));
        }

        @Test
        @DisplayName("无权添加备注返回错误")
        void unauthorizedAddNoteReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("add-note");
            when(request.getParameter("applicationId")).thenReturn("APP001");
            when(request.getParameter("note")).thenReturn("Note");

            Application app = application("APP001", Application.STATUS_PENDING);
            app.setMoId("MO_OTHER");
            when(applicationService.getApplicationById("APP001")).thenReturn(Optional.of(app));

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("Unauthorized"));
        }
    }

    @Nested
    @DisplayName("POST action=save-api-key - 保存API密钥")
    class SaveApiKey {

        @Test
        @DisplayName("保存API密钥成功")
        void saveApiKeySuccessfully() throws Exception {
            when(request.getParameter("action")).thenReturn("save-api-key");
            when(request.getParameter("apiKey")).thenReturn("sk-test-key");

            ModuleOrganiser mo = new ModuleOrganiser("MO001", "mo.user", "hash",
                    "mo@example.com", "MO User", "EECS", "EBU6304", "SE");
            when(userRepository.findMOById("MO001")).thenReturn(Optional.of(mo));

            servlet.doPost(request, response);

            verify(userRepository).saveMO(mo);
            assertEquals("sk-test-key", mo.getDeepseekApiKey());
            String json = responseWriter.toString();
            assertTrue(json.contains("\"success\":true"));
        }

        @Test
        @DisplayName("MO不存在返回错误")
        void moNotFoundReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("save-api-key");
            when(request.getParameter("apiKey")).thenReturn("sk-test-key");

            when(userRepository.findMOById("MO001")).thenReturn(Optional.empty());

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("MO not found"));
        }
    }

    @Nested
    @DisplayName("POST action=unknown - 未知操作")
    class UnknownAction {

        @Test
        @DisplayName("未知action返回错误")
        void unknownActionReturnsError() throws Exception {
            when(request.getParameter("action")).thenReturn("invalid-action");

            servlet.doPost(request, response);

            String json = responseWriter.toString();
            assertTrue(json.contains("Unknown action"));
        }
    }

    private static Application application(String id, String status) {
        Application app = new Application(id, "TA001", "TA One", "JOB001",
                "TA Role", "MO001", "Cover letter");
        app.setStatus(status);
        return app;
    }
}
