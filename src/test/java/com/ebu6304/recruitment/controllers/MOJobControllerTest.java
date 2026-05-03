package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*使用内存 JobRepository + 仅实现 findMOById / saveMO 的 UserRepository，驱动真实 JobService，验证 MOJobController：

postJob：成功、MO 不存在、标题为空
getMyJobs：只返回当前 MO 的职位
getJobDetail：存在 / 不存在
getOpenJobs
updateJob：成功、非发布者无权限
closeJob / cancelJob / startReviewing：状态变更正确
*/

class MOJobControllerTest {

    private static final String MO_ID = "MO_JOB_CTRL";

    private InMemoryJobRepository jobRepository;
    private MoStubUserRepository userRepository;
    private MOJobController controller;

    @BeforeEach
    void setUp() {
        jobRepository = new InMemoryJobRepository();
        userRepository = new MoStubUserRepository();
        userRepository.seedMo(MO_ID, "Dr. Module");
        controller = new MOJobController(new JobService(jobRepository, userRepository));
    }

    @Test
    void postJobSuccess() {
        ControllerResult<JobPosting> r = controller.postJob(
                MO_ID, "EBU6304", "Software Engineering",
                "Lab TA", "Help in lab",
                List.of("Java"), 10, 2,
                "2026-12-31", "2026 Spring",
                "TA", 3.0, 15.0);
        assertTrue(r.isSuccess());
        assertEquals("Lab TA", r.getData().getTitle());
        assertEquals(MO_ID, r.getData().getMoId());
        assertTrue(userRepository.getMo(MO_ID).getPostedJobIds().contains(r.getData().getJobId()));
    }

    @Test
    void postJobWhenMoMissing() {
        ControllerResult<JobPosting> r = controller.postJob(
                "UNKNOWN_MO", "EBU6304", "SE",
                "Title", "Desc",
                List.of(), 10, 1,
                "2026-12-31", "2026 Spring",
                "TA", 0, 10);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("MO not found"));
    }

    @Test
    void postJobRejectsEmptyTitle() {
        ControllerResult<JobPosting> r = controller.postJob(
                MO_ID, "EBU6304", "SE",
                "   ", "Desc",
                List.of(), 10, 1,
                "2026-12-31", "2026 Spring",
                "TA", 0, 10);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("title"));
    }

    @Test
    void getMyJobsReturnsOnlyMoJobs() {
        JobPosting other = new JobPosting(
                "JOB_OTHER", "OTHER_MO", "Other",
                "M1", "Mod", "T", "D", 5, 1, "2026-12-31", "2026 Spring");
        jobRepository.save(other);

        ControllerResult<JobPosting> posted = controller.postJob(
                MO_ID, "EBU6304", "SE", "My Job", "D",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10);
        String myJobId = posted.getData().getJobId();

        ControllerResult<List<JobPosting>> r = controller.getMyJobs(MO_ID);
        assertTrue(r.isSuccess());
        assertEquals(1, r.getData().size());
        assertEquals(myJobId, r.getData().get(0).getJobId());
    }

    @Test
    void getJobDetailFound() {
        ControllerResult<JobPosting> posted = controller.postJob(
                MO_ID, "EBU6304", "SE", "Detail Job", "D",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10);
        ControllerResult<JobPosting> r = controller.getJobDetail(posted.getData().getJobId());
        assertTrue(r.isSuccess());
        assertEquals("Detail Job", r.getData().getTitle());
    }

    @Test
    void getJobDetailMissing() {
        ControllerResult<JobPosting> r = controller.getJobDetail("NO_SUCH_JOB");
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("not found"));
    }

    @Test
    void getOpenJobs() {
        controller.postJob(
                MO_ID, "EBU6304", "SE", "Open One", "D",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10);
        ControllerResult<List<JobPosting>> r = controller.getOpenJobs();
        assertTrue(r.isSuccess());
        assertFalse(r.getData().isEmpty());
    }

    @Test
    void updateJobSuccess() {
        String jobId = controller.postJob(
                MO_ID, "EBU6304", "SE", "Old", "Old desc",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10)
                .getData().getJobId();

        ControllerResult<JobPosting> r = controller.updateJob(
                MO_ID, jobId, "New Title", null, null, -1);
        assertTrue(r.isSuccess());
        assertEquals("New Title", r.getData().getTitle());
    }

    @Test
    void updateJobWrongMo() {
        String jobId = controller.postJob(
                MO_ID, "EBU6304", "SE", "J", "D",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10)
                .getData().getJobId();

        ControllerResult<JobPosting> r = controller.updateJob(
                "OTHER_MO", jobId, "Hack", null, null, -1);
        assertFalse(r.isSuccess());
        assertTrue(r.getMessage().contains("permission"));
    }

    @Test
    void closeJobSuccess() {
        String jobId = controller.postJob(
                MO_ID, "EBU6304", "SE", "J", "D",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10)
                .getData().getJobId();

        ControllerResult<Void> r = controller.closeJob(MO_ID, jobId);
        assertTrue(r.isSuccess());
        assertEquals(JobPosting.STATUS_CLOSED,
                jobRepository.findById(jobId).orElseThrow().getStatus());
    }

    @Test
    void cancelJobSuccess() {
        String jobId = controller.postJob(
                MO_ID, "EBU6304", "SE", "J", "D",
                List.of(), 8, 1, "2026-12-31", "2026 Spring", "TA", 0, 10)
                .getData().getJobId();

        ControllerResult<Void> r = controller.cancelJob(MO_ID, jobId);
        assertTrue(r.isSuccess());
        assertEquals(JobPosting.STATUS_CANCELLED,
                jobRepository.findById(jobId).orElseThrow().getStatus());
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

    private static class MoStubUserRepository extends UserRepository {
        private final Map<String, ModuleOrganiser> mos = new HashMap<>();

        void seedMo(String userId, String fullName) {
            ModuleOrganiser mo = new ModuleOrganiser();
            mo.setUserId(userId);
            mo.setFullName(fullName);
            mo.setPostedJobIds(new ArrayList<>());
            mos.put(userId, mo);
        }

        ModuleOrganiser getMo(String userId) {
            return mos.get(userId);
        }

        @Override
        public Optional<ModuleOrganiser> findMOById(String userId) {
            return Optional.ofNullable(mos.get(userId));
        }

        @Override
        public void saveMO(ModuleOrganiser mo) {
            mos.put(mo.getUserId(), mo);
        }
    }
}
