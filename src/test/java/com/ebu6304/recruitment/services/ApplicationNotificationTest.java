package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.NotificationRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationNotificationTest {

    @TempDir
    Path tempDir;

    private ApplicationService applicationService;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        String dataDir = tempDir.toString().replace('\\', '/');
        NotificationRepository notificationRepository = new NotificationRepository() {
            @Override
            public List<Notification> findAll() {
                return com.ebu6304.recruitment.utils.JsonFileUtil.readList(
                        dataDir + "/notifications.json", Notification.class);
            }

            @Override
            public void save(Notification notification) {
                List<Notification> list = findAll();
                boolean found = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getNotificationId().equals(notification.getNotificationId())) {
                        list.set(i, notification);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    list.add(notification);
                }
                com.ebu6304.recruitment.utils.JsonFileUtil.writeList(
                        dataDir + "/notifications.json", list);
            }
        };

        notificationService = new NotificationService(notificationRepository);
        applicationService = new ApplicationService(
                new ApplicationRepository(),
                new JobRepository(),
                new UserRepository(),
                new WorkloadRepository()
        );
        applicationService.setNotificationService(notificationService);
    }

    @Test
    void rejectApplicationCreatesUnreadNotificationForTa() {
        Application app = new Application(
                "APP_TEST_1", "TA_TEST", "Test TA",
                "JOB1", "Tutorial TA", "MO_TEST", "cover");
        app.reject("Not selected");

        notificationService.createApplicationRejectedNotification(app);

        List<Notification> notifications =
                notificationService.getNotificationsForUser("TA_TEST");

        assertEquals(1, notifications.size());
        assertEquals(Notification.TYPE_APPLICATION_REJECTED, notifications.get(0).getType());
        assertFalse(notifications.get(0).isRead());
        assertTrue(notifications.get(0).getMessage().contains("Tutorial TA"));
    }

    @Test
    void acceptApplicationCreatesUnreadNotificationForTa() {
        Application app = new Application(
                "APP_TEST_2", "TA_TEST", "Test TA",
                "JOB1", "Lab TA", "MO_TEST", "cover");
        app.accept("Welcome aboard");

        notificationService.createApplicationAcceptedNotification(app);

        List<Notification> notifications =
                notificationService.getNotificationsForUser("TA_TEST");

        assertEquals(1, notifications.size());
        assertEquals(Notification.TYPE_APPLICATION_ACCEPTED, notifications.get(0).getType());
        assertFalse(notifications.get(0).isRead());
        assertTrue(notifications.get(0).getMessage().contains("Lab TA"));
        assertTrue(notifications.get(0).getMessage().contains("Welcome aboard"));
    }
}
