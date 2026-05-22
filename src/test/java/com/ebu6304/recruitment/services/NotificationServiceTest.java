package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.repositories.NotificationRepository;
import com.ebu6304.recruitment.utils.JsonFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    @TempDir
    Path tempDir;

    private NotificationService notificationService;
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        JsonFileUtil.setBaseDir(tempDir.toString());
        notificationRepository = new NotificationRepository();
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    public void createWorkloadWarningShouldCreateNotificationForTA() {
        Notification notification = notificationService.createWorkloadWarning(
                "TA001",
                "Alice Chen",
                25,
                20,
                "2026 Spring"
        );

        assertNotNull(notification.getNotificationId());
        assertEquals("TA001", notification.getRecipientUserId());
        assertEquals("TA", notification.getRecipientRole());
        assertEquals(Notification.TYPE_WORKLOAD_WARNING, notification.getType());
        assertEquals("Workload Overload Warning", notification.getTitle());
        assertTrue(notification.getMessage().contains("25 hours per week"));
        assertTrue(notification.getMessage().contains("20 hours"));
        assertFalse(notification.isRead());

        List<Notification> saved = notificationRepository.findByRecipientUserId("TA001");
        assertEquals(1, saved.size());
    }

    @Test
    public void createApplicationWithdrawnNotificationShouldCreateNotificationForMO() {
        Application application = new Application(
                "APP001",
                "TA001",
                "Alice Chen",
                "JOB001",
                "EBU6304 Teaching Assistant",
                "MO001",
                "I am interested in this position."
        );

        Notification notification = notificationService.createApplicationWithdrawnNotification(application);

        assertNotNull(notification.getNotificationId());
        assertEquals("MO001", notification.getRecipientUserId());
        assertEquals("MO", notification.getRecipientRole());
        assertEquals(Notification.TYPE_APPLICATION_WITHDRAWN, notification.getType());
        assertEquals("Application Withdrawn", notification.getTitle());
        assertTrue(notification.getMessage().contains("Alice Chen"));
        assertTrue(notification.getMessage().contains("EBU6304 Teaching Assistant"));
        assertEquals("APP001", notification.getRelatedEntityId());

        List<Notification> saved = notificationRepository.findByRecipientUserId("MO001");
        assertEquals(1, saved.size());
    }

    @Test
    public void getRecentNotificationsForUserShouldRespectLimit() {
        notificationService.createNotification("TA001", "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning 1", "Message 1", "TA001");

        notificationService.createNotification("TA001", "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning 2", "Message 2", "TA001");

        notificationService.createNotification("TA001", "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning 3", "Message 3", "TA001");

        List<Notification> recent = notificationService.getRecentNotificationsForUser("TA001", 2);

        assertEquals(2, recent.size());
    }

    @Test
    public void countUnreadNotificationsShouldOnlyCountUnreadOnes() {
        Notification notification1 = notificationService.createNotification(
                "TA001", "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning 1", "Message 1", "TA001"
        );

        notificationService.createNotification(
                "TA001", "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning 2", "Message 2", "TA001"
        );

        notificationService.markAsRead(notification1.getNotificationId());

        long unreadCount = notificationService.countUnreadNotifications("TA001");

        assertEquals(1, unreadCount);
    }

    @Test
    public void createApplicationAcceptedNotificationShouldIncludeReviewNote() {
        Application application = new Application(
                "APP002", "TA002", "Bob Lee",
                "JOB002", "Lab TA", "MO002", "cover");
        application.accept("Strong candidate");

        Notification notification =
                notificationService.createApplicationAcceptedNotification(application);

        assertEquals(Notification.TYPE_APPLICATION_ACCEPTED, notification.getType());
        assertEquals("TA002", notification.getRecipientUserId());
        assertEquals("APP002", notification.getRelatedEntityId());
        assertTrue(notification.getMessage().contains("Lab TA"));
        assertTrue(notification.getMessage().contains("Strong candidate"));
    }

    @Test
    public void createApplicationRejectedNotificationShouldIncludeReviewNote() {
        Application application = new Application(
                "APP003", "TA003", "Carol Wu",
                "JOB003", "Tutorial TA", "MO003", "cover");
        application.reject("GPA below requirement");

        Notification notification =
                notificationService.createApplicationRejectedNotification(application);

        assertEquals(Notification.TYPE_APPLICATION_REJECTED, notification.getType());
        assertTrue(notification.getMessage().contains("GPA below requirement"));
    }

    @Test
    public void hasWorkloadWarningBeenSentShouldReturnTrueAfterWarningCreated() {
        notificationService.createWorkloadWarning(
                "TA010", "Test TA", 25, 20, "2026 Spring");

        assertTrue(notificationService.hasWorkloadWarningBeenSent("TA010", "2026 Spring"));
        assertEquals(1, notificationService.countWorkloadWarningsForTa("TA010", "2026 Spring"));
        assertFalse(notificationService.getLastWorkloadWarningTime("TA010", "2026 Spring").isEmpty());
    }

    @Test
    public void createWorkloadWarningShouldUseSemesterScopedRelatedId() {
        Notification notification = notificationService.createWorkloadWarning(
                "TA011", "Test TA", 22, 20, "2026 Spring");

        assertEquals(
                NotificationRepository.buildWorkloadRelatedId("TA011", "2026 Spring"),
                notification.getRelatedEntityId());
    }
}