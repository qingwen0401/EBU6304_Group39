package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.CvFileData;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MOApplicationCvAccessTest {
    private Path tempUploadDir;
    private InMemoryApplicationRepository applicationRepository;
    private MOApplicationController controller;

    @BeforeEach
    void setUp() throws Exception {
        tempUploadDir = Files.createTempDirectory("cv-access-test");
        applicationRepository = new InMemoryApplicationRepository();

        ApplicationService applicationService = new ApplicationService(
                applicationRepository,
                new InMemoryJobRepository(),
                new InMemoryUserRepository(),
                new WorkloadRepository(),
                tempUploadDir.toString()
        );
        controller = new MOApplicationController(applicationService, new WorkloadService(new WorkloadRepository(), new UserRepository()));

        String cvPath = applicationService.uploadCv("TA_TEST_1", "cv.pdf", "cv-data".getBytes());
        Application app = new Application(
                "APP_TEST_2", "TA_TEST_1", "Test TA",
                "JOB_TEST_1", "Test Job", "MO_OWNER", "cover"
        );
        app.setCvPath(cvPath);
        applicationRepository.save(app);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.walk(tempUploadDir)
                .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
    }

    @Test
    void shouldAllowJobOwnerMoToViewCv() {
        ControllerResult<CvFileData> result = controller.getApplicationCv("MO_OWNER", "APP_TEST_2");
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("application/pdf", result.getData().getContentType());
    }

    @Test
    void shouldRejectNonOwnerMo() {
        ControllerResult<CvFileData> result = controller.getApplicationCv("MO_OTHER", "APP_TEST_2");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("permission"));
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
    }

    private static class InMemoryJobRepository extends JobRepository {
        @Override
        public Optional<JobPosting> findById(String jobId) {
            return Optional.empty();
        }
    }

    private static class InMemoryUserRepository extends UserRepository {
        @Override
        public Optional<TA> findTAById(String userId) {
            TA ta = new TA();
            ta.setUserId(userId);
            ta.setFullName("Test TA");
            return Optional.of(ta);
        }
    }
}
