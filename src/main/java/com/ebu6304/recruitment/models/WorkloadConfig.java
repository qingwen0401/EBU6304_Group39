package com.ebu6304.recruitment.models;

public class WorkloadConfig {
    private int maxWeeklyHours;

    public WorkloadConfig() {
        this.maxWeeklyHours = 20;
    }

    public WorkloadConfig(int maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
    }

    public int getMaxWeeklyHours() { return maxWeeklyHours; }
    public void setMaxWeeklyHours(int maxWeeklyHours) { this.maxWeeklyHours = maxWeeklyHours; }
}
