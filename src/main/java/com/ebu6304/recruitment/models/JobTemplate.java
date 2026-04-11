package com.ebu6304.recruitment.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 职位模板实体类（Job Template）
 * 用于存储标准化的职位描述，支持快速重新发布职位。
 *
 * @author Group39
 * @version 1.0
 */
public class JobTemplate {

    /** 模板唯一ID */
    private String templateId;

    /** 创建该模板的MO的用户ID */
    private String moId;

    /** 模板名称 */
    private String templateName;

    /** 模块代码（如 "EBU6304"） */
    private String moduleCode;

    /** 模块名称（如 "Software Engineering"） */
    private String moduleName;

    /** 职位标题（如 "Software Engineering TA"） */
    private String title;

    /** 职位详细描述 */
    private String description;

    /** 职位要求的技能列表 */
    private List<String> requiredSkills;

    /** 每周工作小时数 */
    private int hoursPerWeek;

    /** 每小时薪酬（元） */
    private double hourlyRate;

    /** 职位类型（如 "Module TA", "Invigilation", "Lab Assistant"） */
    private String jobType;

    /** 最低GPA要求（0.0表示无要求） */
    private double minGpa;

    /** 创建时间（ISO-8601字符串） */
    private String createdAt;

    /** 最后使用时间（ISO-8601字符串） */
    private String lastUsedAt;

    /** 使用次数 */
    private int usageCount;

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public JobTemplate() {
        this.requiredSkills = new ArrayList<>();
        this.usageCount = 0;
    }

    /**
     * 从JobPosting创建模板的构造方法。
     *
     * @param templateId   模板ID
     * @param templateName 模板名称
     * @param job          职位发布对象
     */
    public JobTemplate(String templateId, String templateName, JobPosting job) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.moId = job.getMoId();
        this.moduleCode = job.getModuleCode();
        this.moduleName = job.getModuleName();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.requiredSkills = new ArrayList<>(job.getRequiredSkills());
        this.hoursPerWeek = job.getHoursPerWeek();
        this.hourlyRate = job.getHourlyRate();
        this.jobType = job.getJobType();
        this.minGpa = job.getMinGpa();
        this.createdAt = java.time.LocalDateTime.now().toString();
        this.usageCount = 0;
    }

    // ==================== 业务方法 ====================

    /**
     * 记录模板被使用。
     */
    public void recordUsage() {
        this.usageCount++;
        this.lastUsedAt = java.time.LocalDateTime.now().toString();
    }

    // ==================== Getter / Setter ====================

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public double getMinGpa() { return minGpa; }
    public void setMinGpa(double minGpa) { this.minGpa = minGpa; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(String lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    @Override
    public String toString() {
        return "JobTemplate{id='" + templateId + "', name='" + templateName +
               "', module='" + moduleCode + "', usageCount=" + usageCount + "}";
    }
}
