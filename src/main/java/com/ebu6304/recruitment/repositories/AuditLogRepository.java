package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLogRepository {
    private static final String AUDIT_FILE = "data/audit_logs.json";

    public void save(AuditLogEntry entry) {
        List<AuditLogEntry> entries = findAll();
        entries.add(entry);
        JsonFileUtil.writeList(AUDIT_FILE, entries);
    }

    public List<AuditLogEntry> findAll() {
        List<AuditLogEntry> entries = JsonFileUtil.readList(AUDIT_FILE, AuditLogEntry.class);
        entries.sort(Comparator.comparing(AuditLogEntry::getTimestamp,
                Comparator.nullsLast(String::compareTo)).reversed());
        return entries;
    }

    public List<AuditLogEntry> findByFilters(String action, String role, String outcome) {
        return findAll().stream()
                .filter(e -> isBlank(action) || action.equalsIgnoreCase(e.getAction()))
                .filter(e -> isBlank(role) || role.equalsIgnoreCase(e.getRole()))
                .filter(e -> isBlank(outcome) || outcome.equalsIgnoreCase(e.getOutcome()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
