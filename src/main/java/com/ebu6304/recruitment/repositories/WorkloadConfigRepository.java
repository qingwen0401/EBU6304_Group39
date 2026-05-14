package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.WorkloadConfig;
import com.ebu6304.recruitment.utils.JsonFileUtil;

public class WorkloadConfigRepository {
    private static final String CONFIG_FILE = "data/admin_workload_config.json";
    private static final int DEFAULT_MAX_WEEKLY_HOURS = 20;

    public WorkloadConfig getConfig() {
        WorkloadConfig config = JsonFileUtil.readObject(CONFIG_FILE, WorkloadConfig.class);
        if (config == null || config.getMaxWeeklyHours() <= 0) {
            return new WorkloadConfig(DEFAULT_MAX_WEEKLY_HOURS);
        }
        return config;
    }

    public void save(WorkloadConfig config) {
        JsonFileUtil.writeObject(CONFIG_FILE, config);
    }
}
