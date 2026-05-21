package com.ebu6304.recruitment.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    @Test
    public void constructorShouldCreateUnreadNotification() {
        Notification notification = new Notification(
                "NOT001",
                "TA001",
                "TA",
                Notification.TYPE_WORKLOAD_WARNING,
                "Workload Overload Warning",
                "Your workload is too high.",
                "TA001"
        );

        assertEquals("NOT001", notification.getNotificationId());
        assertEquals("TA001", notification.getRecipientUserId());
        assertEquals("TA", notification.getRecipientRole());
        assertEquals(Notification.TYPE_WORKLOAD_WARNING, notification.getType());
        assertEquals("Workload Overload Warning", notification.getTitle());
        assertEquals("Your workload is too high.", notification.getMessage());
        assertEquals("TA001", notification.getRelatedEntityId());
        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    public void settersShouldUpdateNotificationFields() {
        Notification notification = new Notification();

        notification.setNotificationId("NOT002");
        notification.setRecipientUserId("MO001");
        notification.setRecipientRole("MO");
        notification.setType(Notification.TYPE_APPLICATION_WITHDRAWN);
        notification.setTitle("Application Withdrawn");
        notification.setMessage("A TA withdrew an application.");
        notification.setRelatedEntityId("APP001");
        notification.setRead(true);
        notification.setCreatedAt("2026-05-16T10:00:00");

        assertEquals("NOT002", notification.getNotificationId());
        assertEquals("MO001", notification.getRecipientUserId());
        assertEquals("MO", notification.getRecipientRole());
        assertEquals(Notification.TYPE_APPLICATION_WITHDRAWN, notification.getType());
        assertEquals("Application Withdrawn", notification.getTitle());
        assertEquals("A TA withdrew an application.", notification.getMessage());
        assertEquals("APP001", notification.getRelatedEntityId());
        assertTrue(notification.isRead());
        assertEquals("2026-05-16T10:00:00", notification.getCreatedAt());
    }
}