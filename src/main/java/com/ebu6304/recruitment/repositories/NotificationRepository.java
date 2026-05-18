package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.Notification;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NotificationRepository {
    private static final String NOTIFICATION_FILE = "data/notifications.json";

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

        JsonFileUtil.writeList(NOTIFICATION_FILE, list);
    }

    public List<Notification> findAll() {
        return JsonFileUtil.readList(NOTIFICATION_FILE, Notification.class);
    }

    public Optional<Notification> findById(String notificationId) {
        return findAll().stream()
                .filter(n -> notificationId.equals(n.getNotificationId()))
                .findFirst();
    }

    public List<Notification> findByRecipientUserId(String recipientUserId) {
        return findAll().stream()
                .filter(n -> recipientUserId.equals(n.getRecipientUserId()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<Notification> findUnreadByRecipientUserId(String recipientUserId) {
        return findByRecipientUserId(recipientUserId).stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
    }

    public void markAsRead(String notificationId) {
        Optional<Notification> notificationOpt = findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            save(notification);
        }
    }

    public List<Notification> findWorkloadWarningsForTa(String taId, String semester) {
        String relatedKey = buildWorkloadRelatedId(taId, semester);
        return findAll().stream()
                .filter(n -> Notification.TYPE_WORKLOAD_WARNING.equals(n.getType()))
                .filter(n -> taId.equals(n.getRecipientUserId()))
                .filter(n -> relatedKey.equals(n.getRelatedEntityId())
                        || (taId.equals(n.getRelatedEntityId())
                        && n.getMessage() != null
                        && n.getMessage().contains("in " + semester)))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public static String buildWorkloadRelatedId(String taId, String semester) {
        return taId + "::" + semester;
    }
}