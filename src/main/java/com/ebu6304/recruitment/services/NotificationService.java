package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.repositories.NotificationRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String recipientUserId, String recipientRole,
                                           String type, String title, String message,
                                           String relatedEntityId) {
        String notificationId = "NOT" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 6);

        Notification notification = new Notification(
                notificationId,
                recipientUserId,
                recipientRole,
                type,
                title,
                message,
                relatedEntityId
        );

        notificationRepository.save(notification);
        return notification;
    }

    public Notification createApplicationWithdrawnNotification(Application app) {
        String title = "Application Withdrawn";
        String message = app.getTaName() + " has withdrawn the application for "
                + app.getJobTitle() + ".";

        return createNotification(
                app.getMoId(),
                "MO",
                Notification.TYPE_APPLICATION_WITHDRAWN,
                title,
                message,
                app.getApplicationId()
        );
    }

    public Notification createWorkloadWarning(String taId, String taName,
                                              int totalWeeklyHours, int maxWeeklyHours,
                                              String semester) {
        String title = "Workload Overload Warning";
        String message = "Your current workload in " + semester + " is "
                + totalWeeklyHours + " hours per week, which exceeds the limit of "
                + maxWeeklyHours + " hours. Please consider reducing your workload.";

        return createNotification(
                taId,
                "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                title,
                message,
                taId
        );
    }



    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByRecipientUserId(userId);
    }

    public List<Notification> getRecentNotificationsForUser(String userId, int limit) {
        return getNotificationsForUser(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public long countUnreadNotifications(String userId) {
        return notificationRepository.findUnreadByRecipientUserId(userId).size();
    }

    public void markAsRead(String notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}