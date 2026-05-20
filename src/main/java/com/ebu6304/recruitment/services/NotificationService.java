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

    public Notification createApplicationAcceptedNotification(Application app) {
        String title = "Application Accepted";
        String message = "Your application for " + app.getJobTitle() + " has been accepted.";
        if (app.getReviewNote() != null && !app.getReviewNote().isBlank()) {
            message += " Note: " + app.getReviewNote();
        }

        return createNotification(
                app.getTaId(),
                "TA",
                Notification.TYPE_APPLICATION_ACCEPTED,
                title,
                message,
                app.getApplicationId()
        );
    }

    public Notification createApplicationRejectedNotification(Application app) {
        String title = "Application Rejected";
        String message = "Your application for " + app.getJobTitle() + " has been rejected.";
        if (app.getReviewNote() != null && !app.getReviewNote().isBlank()) {
            message += " Note: " + app.getReviewNote();
        }

        return createNotification(
                app.getTaId(),
                "TA",
                Notification.TYPE_APPLICATION_REJECTED,
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
                NotificationRepository.buildWorkloadRelatedId(taId, semester)
        );
    }

    public List<Notification> getWorkloadWarningsForTa(String taId, String semester) {
        return notificationRepository.findWorkloadWarningsForTa(taId, semester);
    }

    public boolean hasWorkloadWarningBeenSent(String taId, String semester) {
        return !getWorkloadWarningsForTa(taId, semester).isEmpty();
    }

    public String getLastWorkloadWarningTime(String taId, String semester) {
        List<Notification> warnings = getWorkloadWarningsForTa(taId, semester);
        return warnings.isEmpty() ? "" : warnings.get(0).getCreatedAt();
    }

    public int countWorkloadWarningsForTa(String taId, String semester) {
        return getWorkloadWarningsForTa(taId, semester).size();
    }

    public Notification createWorkloadCancelledNotificationForTA(String taId, String taName,
                                                                  String moduleCode, String semester,
                                                                  String reason) {
        String title = "Workload Assignment Cancelled";
        String message = "Your workload assignment for " + moduleCode + " in " + semester
                + " has been cancelled by the administrator. Reason: " + reason;

        return createNotification(
                taId,
                "TA",
                Notification.TYPE_WORKLOAD_CANCELLED,
                title,
                message,
                moduleCode
        );
    }

    public Notification createWorkloadCancelledNotificationForMO(String moId, String taName,
                                                                  String moduleCode, String semester,
                                                                  String reason) {
        String title = "TA Workload Cancelled";
        String message = "The administrator has cancelled " + taName + "'s workload assignment for "
                + moduleCode + " in " + semester + ". Reason: " + reason;

        return createNotification(
                moId,
                "MO",
                Notification.TYPE_WORKLOAD_CANCELLED,
                title,
                message,
                moduleCode
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