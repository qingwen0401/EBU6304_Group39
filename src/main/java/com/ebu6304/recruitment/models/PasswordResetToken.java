package com.ebu6304.recruitment.models;

public class PasswordResetToken {
    private String token;
    private String userId;
    private String email;
    private String createdAt;
    private String expiresAt;
    private boolean used;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, String userId, String email,
                              String createdAt, String expiresAt) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
