package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.NotificationRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.utils.JsonFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

/**
 * Verifies ApplicationService triggers TA/MO notifications on accept, reject, and withdraw.
 */
class ApplicationNotificationTest {

    private static final String MO_ID = "MO_NOTIF";
    private static final String TA_ID = "TA_NOTIF";
    private static final String JOB_ID = "JOB_NOTIF";

    @TempDir
    Path tempDir;

    private InMemoryApplicationRepository applicationRepository;
    private InMemoryJobRepository jobRepository;
    private MoAppStubUserRepository userRepository;
    private NotificationService notificationService;
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        JsonFileUtil.setBaseDir(tempDir.toString());
        applicationRepository = new InMemoryApplicationRepository();
        jobRepository = new InMemoryJobRepository();
        userRepository = new MoAppStubUserRepository();
        userRepository.seedMo(MO_ID);
        userRepository.seedTa(TA_ID);

        jobRepository.save(new JobPosting(
                JOB_ID, MO_ID, "Dr. MO",
                "EBU6304", "Software Engineering",
                "Lab TA", "Labs",
                10, 3,
                "2026-12-31T23:59:59", "2026 Spring"
        ));

        NotificationRepository notificationRepository = new NotificationRepository();
        notificationService = new NotificationService(notificationRepository);
        applicationService = new ApplicationService(
                applicationRepository,
                jobRepository,
                userRepository,
                new WorkloadRepository()
        );
        applicationService.setNotificationService(notificationService);
    }

    @Test
    void rejectApplicationThroughServiceCreatesUnreadNotificationForTa() {
        applicationRepository.save(pendingApp("APP_REJ"));

        applicationService.rejectApplication(MO_ID, "APP_REJ", "Not selected");

        List<Notification> notifications = notificationService.getNotificationsForUser(TA_ID);
        assertEquals(1, notifications.size());
        assertEquals(Notification.TYPE_APPLICATION_REJECTED, notifications.get(0).getType());
        assertFalse(notifications.get(0).isRead());
        assertTrue(notifications.get(0).getMessage().contains("Lab TA"));
        assertTrue(notifications.get(0).getMessage().contains("Not selected"));
    }

    @Test
    void acceptApplicationThroughServiceCreatesUnreadNotificationForTa() {
        applicationRepository.save(pendingApp("APP_ACC"));

        applicationService.acceptApplication(MO_ID, "APP_ACC", "Welcome aboard");

        List<Notification> notifications = notificationService.getNotificationsForUser(TA_ID);
        assertEquals(1, notifications.size());
        assertEquals(Notification.TYPE_APPLICATION_ACCEPTED, notifications.get(0).getType());
        assertFalse(notifications.get(0).isRead());
        assertTrue(notifications.get(0).getMessage().contains("Lab TA"));
        assertTrue(notifications.get(0).getMessage().contains("Welcome aboard"));
    }

    @Test
    void withdrawApplicationThroughServiceNotifiesMo() {
        applicationRepository.save(pendingApp("APP_WD"));

        applicationService.withdrawApplication(TA_ID, "APP_WD");

        List<Notification> notifications = notificationService.getNotificationsForUser(MO_ID);
        assertEquals(1, notifications.size());
        assertEquals(Notification.TYPE_APPLICATION_WITHDRAWN, notifications.get(0).getType());
        assertTrue(notifications.get(0).getMessage().contains("withdrawn"));
    }

    @Test
    void acceptApplicationWithoutNotificationServiceDoesNotFail() {
        ApplicationService serviceWithoutNotifications = new ApplicationService(
                applicationRepository,
                jobRepository,
                userRepository,
                new WorkloadRepository()
        );
        applicationRepository.save(pendingApp("APP_NO_NS"));

        Application accepted = serviceWithoutNotifications.acceptApplication(
                MO_ID, "APP_NO_NS", "OK");

        assertEquals(Application.STATUS_ACCEPTED, accepted.getStatus());
        assertTrue(notificationService.getNotificationsForUser(TA_ID).isEmpty());
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
        public Optional<TA> findTAById(String userId) {
            return Optional.ofNullable(tas.get(userId));
        }
    }
}
