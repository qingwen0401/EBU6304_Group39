package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private WorkloadService workloadService;

    private AdminController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminController(jobService, applicationService, workloadService);
    }

    @Test
    void getWorkloadReportReturnsReportRows() {
        List<Map<String, Object>> report = List.of(Map.of(
                "taId", "TA001",
                "taName", "Test TA",
                "totalWeeklyHours", 18,
                "isOverloaded", false));
        when(workloadService.getWorkloadReport("2026 Spring")).thenReturn(report);

        ControllerResult<List<Map<String, Object>>> result =
                controller.getWorkloadReport("2026 Spring");

        assertTrue(result.isSuccess());
        assertSame(report, result.getData());
        assertTrue(result.getMessage().contains("1 TA"));
    }

    @Test
    void getOverloadedTAsReturnsOverloadedUsers() {
        TA overloaded = new TA("TA001", "ta.one", "hash", "ta@example.com",
                "TA One", "S001", "EECS", "CS");
        when(workloadService.getOverloadedTAs("2026 Spring")).thenReturn(List.of(overloaded));

        ControllerResult<List<TA>> result = controller.getOverloadedTAs("2026 Spring");

        assertTrue(result.isSuccess());
        assertEquals(List.of(overloaded), result.getData());
        assertTrue(result.getMessage().contains("1 overloaded"));
    }

    @Test
    void getAllWorkloadRecordsReturnsRecords() {
        WorkloadRecord record = workloadRecord("WL001", 8);
        when(workloadService.getAllWorkloadRecords()).thenReturn(List.of(record));

        ControllerResult<List<WorkloadRecord>> result = controller.getAllWorkloadRecords();

        assertTrue(result.isSuccess());
        assertEquals(List.of(record), result.getData());
    }

    @Test
    void completeAndCancelWorkloadRecordsDelegateToService() {
        assertTrue(controller.completeWorkloadRecord("WL001").isSuccess());
        assertTrue(controller.cancelWorkloadRecord("WL002").isSuccess());

        verify(workloadService).completeWorkloadRecord("WL001");
        verify(workloadService).cancelWorkloadRecord("WL002");
    }

    @Test
    void getAllJobsAndOpenJobsReturnServiceData() {
        JobPosting openJob = job("JOB001", JobPosting.STATUS_OPEN);
        JobPosting closedJob = job("JOB002", JobPosting.STATUS_CLOSED);
        when(jobService.getAllJobs()).thenReturn(List.of(openJob, closedJob));
        when(jobService.getOpenJobs()).thenReturn(List.of(openJob));

        ControllerResult<List<JobPosting>> allJobs = controller.getAllJobs();
        ControllerResult<List<JobPosting>> openJobs = controller.getAllOpenJobs();

        assertTrue(allJobs.isSuccess());
        assertEquals(2, allJobs.getData().size());
        assertTrue(openJobs.isSuccess());
        assertEquals(List.of(openJob), openJobs.getData());
    }

    @Test
    void applicationQueriesReturnServiceData() {
        Application application = application("APP001", "TA001", "JOB001");
        when(applicationService.getAllApplications()).thenReturn(List.of(application));
        when(applicationService.getApplicationsByTA("TA001")).thenReturn(List.of(application));
        when(applicationService.getApplicationsByJob("JOB001")).thenReturn(List.of(application));

        assertEquals(List.of(application), controller.getAllApplications().getData());
        assertEquals(List.of(application), controller.getApplicationsByTA("TA001").getData());
        assertEquals(List.of(application), controller.getApplicationsByJob("JOB001").getData());
    }

    @Test
    void getSystemStatsCountsJobsAndApplications() {
        JobPosting openJob = job("JOB001", JobPosting.STATUS_OPEN);
        JobPosting closedJob = job("JOB002", JobPosting.STATUS_CLOSED);
        Application pending = application("APP001", "TA001", "JOB001");
        Application accepted = application("APP002", "TA002", "JOB002");
        accepted.setStatus(Application.STATUS_ACCEPTED);
        when(jobService.getAllJobs()).thenReturn(List.of(openJob, closedJob));
        when(jobService.getOpenJobs()).thenReturn(List.of(openJob));
        when(applicationService.getAllApplications()).thenReturn(List.of(pending, accepted));

        ControllerResult<Map<String, Object>> result = controller.getSystemStats();

        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().get("totalJobs"));
        assertEquals(1, result.getData().get("openJobs"));
        assertEquals(2, result.getData().get("totalApplications"));
        assertEquals(1L, result.getData().get("pendingApplications"));
        assertEquals(1L, result.getData().get("acceptedApplications"));
    }

    @Test
    void controllerMethodsReturnFailureWhenServiceThrows() {
        when(workloadService.getWorkloadReport("2026 Spring"))
                .thenThrow(new IllegalStateException("service unavailable"));
        when(jobService.getAllJobs()).thenThrow(new IllegalStateException("job store down"));
        when(applicationService.getAllApplications())
                .thenThrow(new IllegalStateException("application store down"));

        assertFalse(controller.getWorkloadReport("2026 Spring").isSuccess());
        assertTrue(controller.getAllJobs().getMessage().contains("job store down"));
        assertTrue(controller.getAllApplications().getMessage()
                .contains("application store down"));
    }

    private static JobPosting job(String id, String status) {
        JobPosting job = new JobPosting(id, "MO001", "MO One", "EBU6304",
                "Software Engineering", "TA Role", "Support labs", 8, 2,
                "2026-06-30", "2026 Spring");
        job.setStatus(status);
        job.setPostedAt("2026-05-01T10:00:00");
        return job;
    }

    private static Application application(String id, String taId, String jobId) {
        return new Application(id, taId, "TA One", jobId, "TA Role", "MO001",
                "I can help.");
    }

    private static WorkloadRecord workloadRecord(String id, int hours) {
        return new WorkloadRecord(id, "TA001", "TA One", "JOB001", "TA Role",
                "EBU6304", "MO001", hours, "2026 Spring", "APP001");
    }
}
