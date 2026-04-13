package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.services.ApplicationService;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*内存 ApplicationRepository / JobRepository / WorkloadRepository + 带 MO/TA 桩的 UserRepository，验证 MOApplicationController：

getApplicationsForJob：成功、非职位所属 MO 无权限
getApplicationDetail：找到 / 未找到
markAsReviewing：成功、非 PENDING 失败
acceptApplication：录用成功且无工作量文案；预先写入 18h ACTIVE 工作量后再录用且传入 jobHoursPerWeek=5 时，消息中含工作量 WARNING；错误 MO 拒绝录用
rejectApplication
rejectAllPendingApplications：两条 PENDING 被拒绝，已 ACCEPTED 的不变
 */
class MOApplicationControllerTest {

    private static final String MO_ID = "MO_APP_CTRL";
    private static final String JOB_ID = "JOB_APP_1";
    private static final String TA_ID = "TA_APP_1";
    private static final String SEM = "2026 Spring";

    private Path tempCvDir;
    private InMemoryJobRepository jobRepository;
    private InMemoryApplicationRepository applicationRepository;
    private InMemoryWorkloadRepository workloadRepository;
    private MoAppStubUserRepository userRepository;
    private MOApplicationController controller;

    @BeforeEach
    void setUp() throws Exception {
        tempCvDir = Files.createTempDirectory("mo-app-ctrl-cv");
        jobRepository = new InMemoryJobRepository();
        applicationRepository = new InMemoryApplicationRepository();
        workloadRepository = new InMemoryWorkloadRepository();
        userRepository = new MoAppStubUserRepository();
        userRepository.seedMo(MO_ID);
        userRepository.seedTa(TA_ID);

        JobPosting job = new JobPosting(
                JOB_ID, MO_ID, "Dr. MO",
                "EBU6304", "Software Engineering",
                "Lab TA", "Labs",
                10, 3,
                "2026-12-31T23:59:59", SEM
        );
        jobRepository.save(job);

        ApplicationService applicationService = new ApplicationService(
                applicationRepository,
                jobRepository,
                userRepository,
                workloadRepository,
                tempCvDir.toString()
        );
        WorkloadService workloadService = new WorkloadService(workloadRepository, userRepository);
        controller = new MOApplicationController(applicationService, workloadService);
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
    void getApplicationsForJobSuccess() {
        applicationRepository.save(pendingApp("APP1"));

        ControllerResult<List<Application>> r =
                controller.getApplicationsForJob(MO_ID, JOB_ID);
        assertTrue(r.isSuccess());
        assertEquals(1, r.getData().size());
    }

    @Test
    void getApplicationsForJobWrongMo() {
        applicationRepository.save(pendingApp("APP2"));

        ControllerResult<List<Application>> r =
                controller.getApplicationsForJob("OTHER_MO", JOB_ID);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("permission"));
    }

    @Test
    void getApplicationDetailFound() {
        applicationRepository.save(pendingApp("APP3"));

        ControllerResult<Application> r = controller.getApplicationDetail("APP3");
        assertTrue(r.isSuccess());
        assertEquals("APP3", r.getData().getApplicationId());
    }

    @Test
    void getApplicationDetailMissing() {
        ControllerResult<Application> r = controller.getApplicationDetail("NONE");
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("not found"));
    }

    @Test
    void markAsReviewingSuccess() {
        applicationRepository.save(pendingApp("APP4"));

        ControllerResult<Application> r =
                controller.markAsReviewing(MO_ID, "APP4");
        assertTrue(r.isSuccess());
        assertEquals(Application.STATUS_REVIEWING, r.getData().getStatus());
    }

    @Test
    void markAsReviewingWhenNotPendingFails() {
        Application app = pendingApp("APP5");
        app.setStatus(Application.STATUS_ACCEPTED);
        applicationRepository.save(app);

        ControllerResult<Application> r =
                controller.markAsReviewing(MO_ID, "APP5");
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("PENDING"));
    }

    @Test
    void acceptApplicationSuccessWithoutWorkloadWarning() {
        applicationRepository.save(pendingApp("APP6"));

        ControllerResult<Application> r = controller.acceptApplication(
                MO_ID, "APP6", "Welcome", SEM, 5);
        assertTrue(r.isSuccess());
        assertEquals(Application.STATUS_ACCEPTED, r.getData().getStatus());
        assertFalse(r.getMessage().contains("WARNING:"));
    }

    @Test
    void acceptApplicationIncludesWorkloadWarningWhenWouldExceed() {
        WorkloadRecord heavy = new WorkloadRecord(
                "WR1", TA_ID, "TA",
                JOB_ID, "Lab TA", "EBU6304",
                MO_ID, 18, SEM, "PREV");
        heavy.setStatus("ACTIVE");
        workloadRepository.save(heavy);

        applicationRepository.save(pendingApp("APP7"));

        ControllerResult<Application> r = controller.acceptApplication(
                MO_ID, "APP7", "OK", SEM, 5);
        assertTrue(r.isSuccess());
        assertTrue(r.getMessage().contains("WARNING:"));
        assertTrue(r.getMessage().contains("maximum workload"));
    }

    @Test
    void acceptApplicationWrongMo() {
        applicationRepository.save(pendingApp("APP8"));

        ControllerResult<Application> r = controller.acceptApplication(
                "OTHER_MO", "APP8", "x", SEM, 5);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("permission"));
    }

    @Test
    void rejectApplicationSuccess() {
        applicationRepository.save(pendingApp("APP9"));

        ControllerResult<Application> r =
                controller.rejectApplication(MO_ID, "APP9", "No fit");
        assertTrue(r.isSuccess());
        assertEquals(Application.STATUS_REJECTED, r.getData().getStatus());
    }

    @Test
    void rejectAllPendingApplications() {
        applicationRepository.save(pendingApp("P1"));
        applicationRepository.save(pendingApp("P2"));
        Application accepted = pendingApp("A1");
        accepted.setStatus(Application.STATUS_ACCEPTED);
        applicationRepository.save(accepted);

        ControllerResult<Integer> r =
                controller.rejectAllPendingApplications(MO_ID, JOB_ID);
        assertTrue(r.isSuccess());
        assertEquals(2, r.getData().intValue());
        assertEquals(Application.STATUS_REJECTED,
                applicationRepository.findById("P1").orElseThrow().getStatus());
        assertEquals(Application.STATUS_REJECTED,
                applicationRepository.findById("P2").orElseThrow().getStatus());
        assertEquals(Application.STATUS_ACCEPTED,
                applicationRepository.findById("A1").orElseThrow().getStatus());
    }

    private Application pendingApp(String appId) {
        return new Application(
                appId, TA_ID, "TA Name",
                JOB_ID, "Lab TA", MO_ID, "Please consider me");
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

        @Override
        public List<Application> findByJobId(String jobId) {
            return storage.values().stream()
                    .filter(a -> jobId.equals(a.getJobId()))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Application> findAcceptedByJobId(String jobId) {
            return findByJobId(jobId).stream()
                    .filter(a -> Application.STATUS_ACCEPTED.equals(a.getStatus()))
                    .collect(Collectors.toList());
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

    private static class MoAppStubUserRepository extends UserRepository {
        private final Map<String, ModuleOrganiser> mos = new HashMap<>();
        private final Map<String, TA> tas = new HashMap<>();

        void seedMo(String moId) {
            ModuleOrganiser mo = new ModuleOrganiser();
            mo.setUserId(moId);
            mo.setFullName("Dr. MO");
            mos.put(moId, mo);
        }

        void seedTa(String taId) {
            TA ta = new TA();
            ta.setUserId(taId);
            ta.setFullName("TA Student");
            tas.put(taId, ta);
        }

        @Override
        public Optional<ModuleOrganiser> findMOById(String userId) {
            return Optional.ofNullable(mos.get(userId));
        }

        @Override
        public void saveMO(ModuleOrganiser mo) {
            mos.put(mo.getUserId(), mo);
        }

        @Override
        public Optional<TA> findTAById(String userId) {
            return Optional.ofNullable(tas.get(userId));
        }
    }
}
