package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
/*
针对 TAController，用内存版 JobRepository / ApplicationRepository / WorkloadRepository 和仅识别固定测试 TA（TA_CTRL_1）的 UserRepository，配合真实的 JobService、ApplicationService、WorkloadService（CV 目录为临时目录），覆盖：

浏览开放职位、按学期筛选、职位详情（存在 / 不存在）
投递申请（成功 / TA 不存在）
上传 CV（成功 / 非法扩展名）
将 CV 绑定到申请
撤回申请（成功 / 非 PENDING 失败）
我的申请列表、申请详情（本人 / 非本人）
工作量查询消息中的无警告、[WARNING]、[OVERLOADED] 三种情况
browseOpenJobs 在底层抛出异常时的失败分支
StubUserRepository 只对 TA_CTRL_1 返回 TA，其余 userId 返回空，从而能稳定测到「TA 不存在」和权限类场景。
 */
class TAControllerTest {

    private static final String TA_ID = "TA_CTRL_1";
    private static final String SEM = "2026 Spring";

    private Path tempCvDir;
    private InMemoryJobRepository jobRepository;
    private InMemoryApplicationRepository applicationRepository;
    private InMemoryWorkloadRepository workloadRepository;
    private StubUserRepository userRepository;
    private TAController controller;

    @BeforeEach
    void setUp() throws Exception {
        tempCvDir = Files.createTempDirectory("ta-controller-cv");
        jobRepository = new InMemoryJobRepository();
        applicationRepository = new InMemoryApplicationRepository();
        workloadRepository = new InMemoryWorkloadRepository();
        userRepository = new StubUserRepository();

        JobPosting job = new JobPosting(
                "JOB_CTRL_1", "MO1", "MO One",
                "EBU6304", "Software Engineering",
                "Lab TA", "Assist labs",
                8, 3,
                "2026-12-31T23:59:59", SEM
        );
        jobRepository.save(job);

        JobService jobService = new JobService(jobRepository, userRepository);
        ApplicationService applicationService = new ApplicationService(
                applicationRepository,
                jobRepository,
                userRepository,
                workloadRepository,
                tempCvDir.toString()
        );
        WorkloadService workloadService = new WorkloadService(workloadRepository, userRepository);
        controller = new TAController(jobService, applicationService, workloadService);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.walk(tempCvDir)
                .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
    }

    @Test
    void browseOpenJobsReturnsList() {
        ControllerResult<List<JobPosting>> r = controller.browseOpenJobs();
        assertTrue(r.isSuccess());
        assertEquals(1, r.getData().size());
        assertTrue(r.getMessage().contains("1 open"));
    }

    @Test
    void browseJobsBySemesterFilters() {
        ControllerResult<List<JobPosting>> r = controller.browseJobsBySemester(SEM);
        assertTrue(r.isSuccess());
        assertEquals(1, r.getData().size());
    }

    @Test
    void viewJobDetailWhenFound() {
        ControllerResult<JobPosting> r = controller.viewJobDetail("JOB_CTRL_1");
        assertTrue(r.isSuccess());
        assertEquals("Lab TA", r.getData().getTitle());
    }

    @Test
    void viewJobDetailWhenMissing() {
        ControllerResult<JobPosting> r = controller.viewJobDetail("UNKNOWN");
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("not found"));
    }

    @Test
    void applyForJobSuccess() {
        ControllerResult<Application> r = controller.applyForJob(
                TA_ID, "JOB_CTRL_1", "Please hire me", null);
        assertTrue(r.isSuccess());
        assertNotNull(r.getData().getApplicationId());
        assertEquals(TA_ID, r.getData().getTaId());
    }

    @Test
    void applyForJobWhenTaMissing() {
        ControllerResult<Application> r = controller.applyForJob(
                "NO_SUCH_TA", "JOB_CTRL_1", "x", null);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("TA not found"));
    }

    @Test
    void uploadCvSuccess() {
        ControllerResult<String> r = controller.uploadCv(TA_ID, "cv.pdf", "data".getBytes());
        assertTrue(r.isSuccess());
        assertTrue(r.getData().endsWith(".pdf"));
    }

    @Test
    void uploadCvRejectsBadExtension() {
        ControllerResult<String> r = controller.uploadCv(TA_ID, "cv.exe", "x".getBytes());
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("Only .pdf or .doc"));
    }

    @Test
    void attachCvToApplicationSuccess() throws Exception {
        Application app = new Application(
                "APP_CTRL_1", TA_ID, "Test TA",
                "JOB_CTRL_1", "Lab TA", "MO1", "cover");
        applicationRepository.save(app);
        Path cvFile = tempCvDir.resolve("hand.pdf");
        Files.writeString(cvFile, "dummy");
        String cvPath = cvFile.toString().replace("\\", "/");

        ControllerResult<Application> r =
                controller.attachCvToApplication(TA_ID, "APP_CTRL_1", cvPath);
        assertTrue(r.isSuccess());
        assertEquals(cvPath, r.getData().getCvPath());
    }

    @Test
    void withdrawApplicationSuccess() {
        Application app = new Application(
                "APP_WD_1", TA_ID, "Test TA",
                "JOB_CTRL_1", "Lab TA", "MO1", "cover");
        applicationRepository.save(app);

        ControllerResult<Void> r = controller.withdrawApplication(TA_ID, "APP_WD_1");
        assertTrue(r.isSuccess());
        assertEquals(Application.STATUS_WITHDRAWN,
                applicationRepository.findById("APP_WD_1").orElseThrow().getStatus());
    }

    @Test
    void withdrawApplicationWhenNotPendingFails() {
        Application app = new Application(
                "APP_WD_2", TA_ID, "Test TA",
                "JOB_CTRL_1", "Lab TA", "MO1", "cover");
        app.setStatus(Application.STATUS_ACCEPTED);
        applicationRepository.save(app);

        ControllerResult<Void> r = controller.withdrawApplication(TA_ID, "APP_WD_2");
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("Cannot withdraw"));
    }

    @Test
    void getMyApplications() {
        Application app = new Application(
                "APP_LIST_1", TA_ID, "Test TA",
                "JOB_CTRL_1", "Lab TA", "MO1", "cover");
        applicationRepository.save(app);

        ControllerResult<List<Application>> r = controller.getMyApplications(TA_ID);
        assertTrue(r.isSuccess());
        assertEquals(1, r.getData().size());
    }

    @Test
    void getApplicationDetailWhenOwner() {
        Application app = new Application(
                "APP_DET_1", TA_ID, "Test TA",
                "JOB_CTRL_1", "Lab TA", "MO1", "cover");
        applicationRepository.save(app);

        ControllerResult<Application> r =
                controller.getApplicationDetail(TA_ID, "APP_DET_1");
        assertTrue(r.isSuccess());
        assertEquals("APP_DET_1", r.getData().getApplicationId());
    }

    @Test
    void getApplicationDetailWhenNotOwner() {
        Application app = new Application(
                "APP_DET_2", "OTHER_TA", "Other",
                "JOB_CTRL_1", "Lab TA", "MO1", "cover");
        applicationRepository.save(app);

        ControllerResult<Application> r =
                controller.getApplicationDetail(TA_ID, "APP_DET_2");
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("permission") || r.getMessage().contains("not found"));
    }

    @Test
    void getMyWorkloadNoWarningSuffix() {
        seedWorkloadHours(10);

        ControllerResult<List<WorkloadRecord>> r =
                controller.getMyWorkload(TA_ID, SEM);
        assertTrue(r.isSuccess());
        assertTrue(r.getMessage().contains("Total weekly hours: 10"));
        assertFalse(r.getMessage().contains("[WARNING]"));
        assertFalse(r.getMessage().contains("[OVERLOADED]"));
    }

    @Test
    void getMyWorkloadWarningSuffix() {
        seedWorkloadHours(16);

        ControllerResult<List<WorkloadRecord>> r =
                controller.getMyWorkload(TA_ID, SEM);
        assertTrue(r.isSuccess());
        assertTrue(r.getMessage().contains("16"));
        assertTrue(r.getMessage().contains("[WARNING]"));
    }

    @Test
    void getMyWorkloadOverloadedSuffix() {
        seedWorkloadHours(21);

        ControllerResult<List<WorkloadRecord>> r =
                controller.getMyWorkload(TA_ID, SEM);
        assertTrue(r.isSuccess());
        assertTrue(r.getMessage().contains("21"));
        assertTrue(r.getMessage().contains("[OVERLOADED]"));
    }

    @Test
    void browseOpenJobsWhenJobLayerThrows() {
        JobRepository throwing = new JobRepository() {
            @Override
            public List<JobPosting> findAll() {
                throw new RuntimeException("storage unavailable");
            }
        };
        TAController fragile = new TAController(
                new JobService(throwing, userRepository),
                new ApplicationService(
                        applicationRepository,
                        jobRepository,
                        userRepository,
                        workloadRepository,
                        tempCvDir.toString()),
                new WorkloadService(workloadRepository, userRepository));

        ControllerResult<List<JobPosting>> r = fragile.browseOpenJobs();
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("Failed to retrieve jobs"));
    }

    private void seedWorkloadHours(int weeklyHours) {
        WorkloadRecord rec = new WorkloadRecord(
                "WR1", TA_ID, "Test TA",
                "JOB_CTRL_1", "Lab TA", "EBU6304",
                "MO1", weeklyHours, SEM,
                "APP_X");
        rec.setStatus("ACTIVE");
        workloadRepository.save(rec);
    }

    private static class InMemoryJobRepository extends JobRepository {
        private final Map<String, JobPosting> storage = new HashMap<>();

        @Override
        public void save(JobPosting job) {
            storage.put(job.getJobId(), job);
        }

        @Override
        public Optional<JobPosting> findById(String jobId) {
            return Optional.ofNullable(storage.get(jobId));
        }

        @Override
        public List<JobPosting> findAll() {
            return new ArrayList<>(storage.values());
        }
    }

    private static class InMemoryApplicationRepository extends ApplicationRepository {
        private final Map<String, Application> storage = new HashMap<>();

        @Override
        public void save(Application application) {
            storage.put(application.getApplicationId(), application);
        }

        @Override
        public Optional<Application> findById(String applicationId) {
            return Optional.ofNullable(storage.get(applicationId));
        }

        @Override
        public List<Application> findAll() {
            return new ArrayList<>(storage.values());
        }
    }

    private static class InMemoryWorkloadRepository extends WorkloadRepository {
        private final List<WorkloadRecord> storage = new ArrayList<>();

        @Override
        public void save(WorkloadRecord record) {
            storage.removeIf(r -> r.getRecordId().equals(record.getRecordId()));
            storage.add(record);
        }

        @Override
        public List<WorkloadRecord> findAll() {
            return new ArrayList<>(storage);
        }
    }

    private static class StubUserRepository extends UserRepository {
        @Override
        public Optional<TA> findTAById(String userId) {
            if (!TA_ID.equals(userId)) {
                return Optional.empty();
            }
            TA ta = new TA();
            ta.setUserId(userId);
            ta.setFullName("Test TA");
            return Optional.of(ta);
        }
    }
}
