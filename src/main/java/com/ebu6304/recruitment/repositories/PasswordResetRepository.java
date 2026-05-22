package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.PasswordResetToken;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PasswordResetRepository {
    private static final String RESET_FILE = "data/password_reset_tokens.json";

    public void save(PasswordResetToken token) {
        List<PasswordResetToken> tokens = findAll();
        boolean found = false;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getToken().equals(token.getToken())) {
                tokens.set(i, token);
                found = true;
                break;
            }
        }
        if (!found) {
            tokens.add(token);
        }
        JsonFileUtil.writeList(RESET_FILE, tokens);
    }

    public List<PasswordResetToken> findAll() {
        return JsonFileUtil.readList(RESET_FILE, PasswordResetToken.class);
    }

    public Optional<PasswordResetToken> findUsable(String tokenValue) {
        LocalDateTime now = LocalDateTime.now();
        return findAll().stream()
                .filter(token -> tokenValue != null && tokenValue.equals(token.getToken()))
                .filter(token -> !token.isUsed())
                .filter(token -> token.getExpiresAt() != null
                        && LocalDateTime.parse(token.getExpiresAt()).isAfter(now))
                .findFirst();
    }
}
