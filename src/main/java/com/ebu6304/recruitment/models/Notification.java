package com.ebu6304.recruitment.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    public static final String TYPE_WORKLOAD_WARNING = "WORKLOAD_WARNING";
    public static final String TYPE_WORKLOAD_CANCELLED = "WORKLOAD_CANCELLED";
    public static final String TYPE_APPLICATION_WITHDRAWN = "APPLICATION_WITHDRAWN";
    public static final String TYPE_APPLICATION_ACCEPTED = "APPLICATION_ACCEPTED";
    public static final String TYPE_APPLICATION_REJECTED = "APPLICATION_REJECTED";

    private String notificationId;
    private String recipientUserId;
    private String recipientRole;
    private String type;
    private String title;
    private String message;
    private String relatedEntityId;

    @SerializedName("read")
    private boolean readStatus;

    private String createdAt;

    public Notification() {
    }

    public Notification(String notificationId, String recipientUserId, String recipientRole,
                        String type, String title, String message, String relatedEntityId) {
        this.notificationId = notificationId;
        this.recipientUserId = recipientUserId;
        this.recipientRole = recipientRole;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedEntityId = relatedEntityId;
        this.readStatus = false;
        this.createdAt = java.time.LocalDateTime.now().toString();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(String relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public boolean isRead() {
        return readStatus;
    }

    public void setRead(boolean read) {
        this.readStatus = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{id='" + notificationId
                + "', recipientUserId='" + recipientUserId
                + "', type='" + type
                + "', read=" + readStatus
                + "}";
    }
}