package com.ebu6304.recruitment.models;

/**
 * 工作量记录实体类（Workload Record）
 * 记录TA在某个职位上的实际工作量，用于管理员监控TA总体工作负荷。
 *
 * <p>每当TA被录用到一个职位时，系统自动创建一条WorkloadRecord。
 * 管理员可通过汇总WorkloadRecord来查看每个TA的总工作量。</p>
 *
 * @author Group39
 * @version 1.0
 */
public class WorkloadRecord {

    /** 工作量记录唯一ID */
    private String recordId;

    /** 对应的TA用户ID */
    private String taId;

    /** TA姓名（冗余存储，便于显示） */
    private String taName;

    /** 对应的职位ID */
    private String jobId;

    /** 职位标题（冗余存储，便于显示） */
    private String jobTitle;

    /** 模块代码 */
    private String moduleCode;

    /** 发布该职位的MO的用户ID */
    private String moId;

    /** 每周工作小时数（来自职位信息） */
    private int weeklyHours;

    /** 学期（如 "2026 Spring"） */
    private String semester;

    /** 记录创建时间（ISO-8601字符串） */
    private String createdAt;

    /** 对应的申请ID */
    private String applicationId;

    /** 工作量记录状态：ACTIVE（进行中）/ COMPLETED（已完成）/ CANCELLED（已取消） */
    private String status;

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public WorkloadRecord() {}

    /**
     * 创建工作量记录的构造方法。
     * 通常在TA被录用时由系统自动调用。
     *
     * @param recordId      记录ID
     * @param taId          TA用户ID
     * @param taName        TA姓名
     * @param jobId         职位ID
     * @param jobTitle      职位标题
     * @param moduleCode    模块代码
     * @param moId          MO用户ID
     * @param weeklyHours   每周工作小时数
     * @param semester      学期
     * @param applicationId 对应申请ID
     */
    public WorkloadRecord(String recordId, String taId, String taName,
                          String jobId, String jobTitle, String moduleCode,
                          String moId, int weeklyHours, String semester,
                          String applicationId) {
        this.recordId = recordId;
        this.taId = taId;
        this.taName = taName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.moduleCode = moduleCode;
        this.moId = moId;
        this.weeklyHours = weeklyHours;
        this.semester = semester;
        this.applicationId = applicationId;
        this.status = "ACTIVE";
        this.createdAt = java.time.LocalDateTime.now().toString();
    }

    // ==================== Getter / Setter ====================

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getTaName() { return taName; }
    public void setTaName(String taName) { this.taName = taName; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public int getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(int weeklyHours) { this.weeklyHours = weeklyHours; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "WorkloadRecord{id='" + recordId + "', taId='" + taId +
               "', jobId='" + jobId + "', weeklyHours=" + weeklyHours +
               ", status='" + status + "'}";
    }
}
