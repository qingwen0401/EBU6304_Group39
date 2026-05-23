package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.repositories.NotificationRepository;
import com.ebu6304.recruitment.utils.JsonFileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MO相关通知服务测试")
public class MONotificationServiceTest {

    @TempDir
    Path tempDir;

    private NotificationService notificationService;
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        JsonFileUtil.setBaseDir(tempDir.toString());
        notificationRepository = new NotificationRepository();
        notificationService = new NotificationService(notificationRepository);
    }

    @Nested
    @DisplayName("申请录用通知")
    class AcceptedNotification {

        @Test
        @DisplayName("录用通知发送给TA，包含职位信息")
        void acceptedNotificationSentToTA() {
            Application app = createApplication();
            app.accept("Welcome aboard!");

            Notification notification = notificationService.createApplicationAcceptedNotification(app);

            assertEquals("TA001", notification.getRecipientUserId());
            assertEquals("TA", notification.getRecipientRole());
            assertEquals(Notification.TYPE_APPLICATION_ACCEPTED, notification.getType());
            assertEquals("Application Accepted", notification.getTitle());
            assertTrue(notification.getMessage().contains("SE TA"));
            assertTrue(notification.getMessage().contains("Welcome aboard!"));
        }

        @Test
        @DisplayName("录用通知无备注时不包含Note前缀")
        void acceptedNotificationWithoutNote() {
            Application app = createApplication();
            app.accept(null);

            Notification notification = notificationService.createApplicationAcceptedNotification(app);

            assertFalse(notification.getMessage().contains("Note:"));
        }
    }

    @Nested
    @DisplayName("申请拒绝通知")
    class RejectedNotification {

        @Test
        @DisplayName("拒绝通知发送给TA，包含拒绝原因")
        void rejectedNotificationSentToTA() {
            Application app = createApplication();
            app.reject("Not enough experience");

            Notification notification = notificationService.createApplicationRejectedNotification(app);

            assertEquals("TA001", notification.getRecipientUserId());
            assertEquals("TA", notification.getRecipientRole());
            assertEquals(Notification.TYPE_APPLICATION_REJECTED, notification.getType());
            assertEquals("Application Rejected", notification.getTitle());
            assertTrue(notification.getMessage().contains("SE TA"));
            assertTrue(notification.getMessage().contains("Not enough experience"));
        }

        @Test
        @DisplayName("拒绝通知无备注时不包含Note前缀")
        void rejectedNotificationWithoutNote() {
            Application app = createApplication();
            app.reject(null);

            Notification notification = notificationService.createApplicationRejectedNotification(app);

            assertFalse(notification.getMessage().contains("Note:"));
        }
    }

    @Nested
    @DisplayName("工作量取消通知")
    class WorkloadCancelledNotification {

        @Test
        @DisplayName("取消通知发送给TA")
        void cancelledNotificationSentToTA() {
            Notification notification = notificationService.createWorkloadCancelledNotificationForTA(
                    "TA001", "Alice", "EBU6304", "2026 Spring", "Schedule conflict");

            assertEquals("TA001", notification.getRecipientUserId());
            assertEquals("TA", notification.getRecipientRole());
            assertEquals(Notification.TYPE_WORKLOAD_CANCELLED, notification.getType());
            assertTrue(notification.getMessage().contains("EBU6304"));
            assertTrue(notification.getMessage().contains("Schedule conflict"));
        }

        @Test
        @DisplayName("取消通知发送给MO")
        void cancelledNotificationSentToMO() {
            Notification notification = notificationService.createWorkloadCancelledNotificationForMO(
                    "MO001", "Alice", "EBU6304", "2026 Spring", "Student request");

            assertEquals("MO001", notification.getRecipientUserId());
            assertEquals("MO", notification.getRecipientRole());
            assertEquals(Notification.TYPE_WORKLOAD_CANCELLED, notification.getType());
            assertTrue(notification.getMessage().contains("Alice"));
            assertTrue(notification.getMessage().contains("EBU6304"));
            assertTrue(notification.getMessage().contains("Student request"));
        }
    }

    @Nested
    @DisplayName("通知查询和状态管理")
    class NotificationQueryAndStatus {

        @Test
        @DisplayName("获取用户所有通知")
        void getNotificationsForUser() {
            notificationService.createNotification("MO001", "MO",
                    "APPLICATION_RECEIVED", "New App", "msg1", "APP001");
            notificationService.createNotification("MO001", "MO",
                    "APPLICATION_WITHDRAWN", "Withdrawn", "msg2", "APP002");
            notificationService.createNotification("TA001", "TA",
                    "APPLICATION_ACCEPTED", "Accepted", "msg3", "APP003");

            List<Notification> moNotifs = notificationService.getNotificationsForUser("MO001");
            assertEquals(2, moNotifs.size());
        }

        @Test
        @DisplayName("标记通知为已读")
        void markNotificationAsRead() {
            Notification notification = notificationService.createNotification(
                    "MO001", "MO", "TEST", "Title", "Message", "REL001");

            assertFalse(notification.isRead());

            notificationService.markAsRead(notification.getNotificationId());

            long unread = notificationService.countUnreadNotifications("MO001");
            assertEquals(0, unread);
        }

        @Test
        @DisplayName("工作量警告查重")
        void workloadWarningDeduplication() {
            assertFalse(notificationService.hasWorkloadWarningBeenSent("TA001", "2026 Spring"));

            notificationService.createWorkloadWarning("TA001", "Alice", 25, 20, "2026 Spring");

            assertTrue(notificationService.hasWorkloadWarningBeenSent("TA001", "2026 Spring"));
            assertEquals(1, notificationService.countWorkloadWarningsForTa("TA001", "2026 Spring"));
        }
    }

    private Application createApplication() {
        return new Application("APP001", "TA001", "Alice Chen", "JOB001",
                "SE TA", "MO001", "I want this position");
    }
}
