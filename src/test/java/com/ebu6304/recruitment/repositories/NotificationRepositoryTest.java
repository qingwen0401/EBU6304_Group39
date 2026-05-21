package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.utils.JsonFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationRepositoryTest {

    @TempDir
    Path tempDir;

    private NotificationRepository notificationRepository;

    @BeforeEach
    public void setUp() {
        JsonFileUtil.setBaseDir(tempDir.toString());
        notificationRepository = new NotificationRepository();
    }

    @Test
    public void saveShouldPersistNewNotification() {
        Notification notification = new Notification(
                "NOT001",
                "TA001",
                "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Workload Overload Warning",
                "Your workload is too high.",
                "TA001"
        );

        notificationRepository.save(notification);

        List<Notification> notifications = notificationRepository.findAll();

        assertEquals(1, notifications.size());
        assertEquals("NOT001", notifications.get(0).getNotificationId());
        assertEquals("TA001", notifications.get(0).getRecipientUserId());
    }

    @Test
    public void saveShouldUpdateExistingNotification() {
        Notification notification = new Notification(
                "NOT001",
                "TA001",
                "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Old Title",
                "Old message.",
                "TA001"
        );

        notificationRepository.save(notification);

        notification.setTitle("Updated Title");
        notification.setMessage("Updated message.");
        notification.setRead(true);
        notificationRepository.save(notification);

        List<Notification> notifications = notificationRepository.findAll();

        assertEquals(1, notifications.size());
        assertEquals("Updated Title", notifications.get(0).getTitle());
        assertEquals("Updated message.", notifications.get(0).getMessage());
        assertTrue(notifications.get(0).isRead());
    }

    @Test
    public void findByIdShouldReturnMatchingNotification() {
        Notification notification = new Notification(
                "NOT002",
                "MO001",
                "MO",
                Notification.TYPE_APPLICATION_WITHDRAWN,
                "Application Withdrawn",
                "A TA withdrew an application.",
                "APP001"
        );

        notificationRepository.save(notification);

        Optional<Notification> result = notificationRepository.findById("NOT002");

        assertTrue(result.isPresent());
        assertEquals("MO001", result.get().getRecipientUserId());
        assertEquals(Notification.TYPE_APPLICATION_WITHDRAWN, result.get().getType());
    }

    @Test
    public void findByRecipientUserIdShouldOnlyReturnThatUsersNotifications() {
        Notification taNotification = new Notification(
                "NOT003",
                "TA001",
                "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning",
                "Workload warning.",
                "TA001"
        );

        Notification moNotification = new Notification(
                "NOT004",
                "MO001",
                "MO",
                Notification.TYPE_APPLICATION_WITHDRAWN,
                "Withdrawn",
                "Application withdrawn.",
                "APP001"
        );

        notificationRepository.save(taNotification);
        notificationRepository.save(moNotification);

        List<Notification> taResults = notificationRepository.findByRecipientUserId("TA001");

        assertEquals(1, taResults.size());
        assertEquals("NOT003", taResults.get(0).getNotificationId());
    }

    @Test
    public void markAsReadShouldUpdateReadStatus() {
        Notification notification = new Notification(
                "NOT005",
                "TA001",
                "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Warning",
                "Workload warning.",
                "TA001"
        );

        notificationRepository.save(notification);
        notificationRepository.markAsRead("NOT005");

        Optional<Notification> result = notificationRepository.findById("NOT005");

        assertTrue(result.isPresent());
        assertTrue(result.get().isRead());
    }
}