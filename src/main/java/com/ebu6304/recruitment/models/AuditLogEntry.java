package com.ebu6304.recruitment.models;

public class AuditLogEntry {
    private String logId;
    private String timestamp;
    private String username;
    private String userId;
    private String role;
    private String action;
    private String outcome;
    private String ipAddress;
    private String details;

    public AuditLogEntry() {}

    public AuditLogEntry(String logId, String username, String userId, String role,
                         String action, String outcome, String ipAddress, String details) {
        this.logId = logId;
        this.timestamp = java.time.LocalDateTime.now().toString();
        this.username = username;
        this.userId = userId;
        this.role = role;
        this.action = action;
        this.outcome = outcome;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
