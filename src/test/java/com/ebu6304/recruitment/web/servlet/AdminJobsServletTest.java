package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.repositories.JobRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminJobsServletTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AdminJobsServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminJobsServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        when(context.getAttribute("jobRepository")).thenReturn(jobRepository);
    }

    @Test
    void getJobsFiltersByStatusModuleAndSemester() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        JobPosting matching = job("JOB001", JobPosting.STATUS_OPEN, "EBU6304", "2026 Spring");
        JobPosting closed = job("JOB002", JobPosting.STATUS_CLOSED, "EBU6304", "2026 Spring");
        JobPosting otherModule = job("JOB003", JobPosting.STATUS_OPEN, "EBU6405", "2026 Spring");
        when(request.getParameter("status")).thenReturn(JobPosting.STATUS_OPEN);
        when(request.getParameter("module")).thenReturn("EBU6304");
        when(request.getParameter("semester")).thenReturn("2026 Spring");
        when(jobRepository.findAll()).thenReturn(List.of(closed, otherModule, matching));
        when(request.getRequestDispatcher("/WEB-INF/jsp/admin/jobs.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        ArgumentCaptor<List<JobPosting>> jobsCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("jobs"), jobsCaptor.capture());
        assertEquals(1, jobsCaptor.getValue().size());
        assertEquals("JOB001", jobsCaptor.getValue().get(0).getJobId());
        verify(request).setAttribute("selectedStatus", JobPosting.STATUS_OPEN);
        verify(request).setAttribute("selectedModule", "EBU6304");
        verify(request).setAttribute("selectedSemester", "2026 Spring");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void getJobsExportsFilteredCsv() throws Exception {
        ByteArrayOutputStream csv = new ByteArrayOutputStream();
        JobPosting matching = job("JOB001", JobPosting.STATUS_OPEN, "EBU6304", "2026 Spring");
        JobPosting other = job("JOB002", JobPosting.STATUS_OPEN, "EBU6405", "2026 Spring");
        when(request.getParameter("export")).thenReturn("csv");
        when(request.getParameter("module")).thenReturn("EBU6304");
        when(jobRepository.findAll()).thenReturn(List.of(matching, other));
        when(response.getOutputStream()).thenReturn(AdminServletTestSupport.outputStream(csv));

        servlet.doGet(request, response);

        verify(response).setContentType("text/csv; charset=UTF-8");
        String body = csv.toString(StandardCharsets.UTF_8);
        assertTrue(body.contains("Job ID,Title,Owner,Module,Semester,Deadline,Hours,Vacancies,Filled,Status"));
        assertTrue(body.contains("\"JOB001\""));
        assertFalse(body.contains("\"JOB002\""));
    }

    private static JobPosting job(String id, String status, String module, String semester) {
        JobPosting job = new JobPosting(id, "MO001", "MO One", module,
                "Module Name", "TA Role " + id, "Support teaching", 8, 2,
                "2026-06-30", semester);
        job.setStatus(status);
        job.setPostedAt("2026-05-01T10:00:00");
        return job;
    }
}
