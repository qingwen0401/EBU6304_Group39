package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationCvValidationTest {
    private Path tempUploadDir;
    private InMemoryApplicationRepository applicationRepository;
    private ApplicationService service;

    @BeforeEach
    void setUp() throws Exception {
        tempUploadDir = Files.createTempDirectory("cv-upload-test");
        applicationRepository = new InMemoryApplicationRepository();
        service = new ApplicationService(
                applicationRepository,
                new InMemoryJobRepository(),
                new InMemoryUserRepository(),
                new WorkloadRepository(),
                tempUploadDir.toString()
        );
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
    void shouldUploadAllowedPdfCv() {
        String savedPath = service.uploadCv("TA_TEST_1", "profile.pdf", "pdf-content".getBytes());
        assertTrue(savedPath.endsWith(".pdf"));
        assertTrue(Files.exists(Path.of(savedPath)));
    }

    @Test
    void shouldRejectInvalidCvExtension() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadCv("TA_TEST_1", "profile.txt", "bad".getBytes())
        );
        assertTrue(ex.getMessage().contains("Only .pdf or .doc"));
    }

    @Test
    void shouldAttachUploadedCvPathToApplication() {
        Application app = new Application(
                "APP_TEST_1", "TA_TEST_1", "Test TA",
                "JOB_TEST_1", "Test Job", "MO_TEST_1", "cover"
        );
        applicationRepository.save(app);
        String savedPath = service.uploadCv("TA_TEST_1", "profile.doc", "doc-content".getBytes());

        Application updated = service.attachCvToApplication("TA_TEST_1", "APP_TEST_1", savedPath);
        assertEquals(savedPath, updated.getCvPath());
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
