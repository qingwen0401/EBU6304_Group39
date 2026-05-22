package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.PasswordResetEmail;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.List;

public class PasswordResetEmailRepository {
    private static final String EMAIL_FILE = "data/password_reset_emails.json";

    public void save(PasswordResetEmail email) {
        List<PasswordResetEmail> emails = findAll();
        emails.add(0, email);
        JsonFileUtil.writeList(EMAIL_FILE, emails);
    }

    public List<PasswordResetEmail> findAll() {
        return JsonFileUtil.readList(EMAIL_FILE, PasswordResetEmail.class);
    }
}
