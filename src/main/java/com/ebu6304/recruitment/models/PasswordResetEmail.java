package com.ebu6304.recruitment.models;

public class PasswordResetEmail {
    private String emailId;
    private String recipientEmail;
    private String subject;
    private String resetLink;
    private String createdAt;

    public PasswordResetEmail() {
    }

    public PasswordResetEmail(String emailId, String recipientEmail,
                              String subject, String resetLink, String createdAt) {
        this.emailId = emailId;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.resetLink = resetLink;
        this.createdAt = createdAt;
    }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getResetLink() { return resetLink; }
    public void setResetLink(String resetLink) { this.resetLink = resetLink; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
