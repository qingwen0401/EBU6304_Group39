package com.ebu6304.recruitment.models;

/**
 * 申请实体类（Application）
 * 记录TA对某个职位的申请信息，包括申请状态、求职信、MO审核意见等。
 *
 * <p>申请状态流转：
 * <pre>
 *   PENDING（待审核）→ ACCEPTED（已录用）
 *                   → REJECTED（已拒绝）
 *   PENDING → WITHDRAWN（已撤回，由TA主动撤回）
 * </pre>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class Application {

    // ==================== 申请状态常量 ====================

    /** 申请状态：待审核 */
    public static final String STATUS_PENDING = "PENDING";

    /** 申请状态：已录用 */
    public static final String STATUS_ACCEPTED = "ACCEPTED";

    /** 申请状态：已拒绝 */
    public static final String STATUS_REJECTED = "REJECTED";

    /** 申请状态：已撤回（TA主动撤回） */
    public static final String STATUS_WITHDRAWN = "WITHDRAWN";

    // ==================== 字段定义 ====================

    /** 申请唯一ID */
    private String applicationId;

    /** 申请人（TA）的用户ID */
    private String taId;

    /** 申请人姓名（冗余存储，便于显示） */
    private String taName;

    /** 申请对应的职位ID */
    private String jobId;

    /** 职位标题（冗余存储，便于显示） */
    private String jobTitle;

    /** 发布该职位的MO的用户ID */
    private String moId;

    /** 求职信内容 */
    private String coverLetter;

    /** 简历文件路径（可选） */
    private String cvPath;

    /** 申请状态：PENDING / ACCEPTED / REJECTED / WITHDRAWN */
    private String status;

    /** 申请提交时间（ISO-8601字符串） */
    private String appliedAt;

    /** MO审核时间（ISO-8601字符串） */
    private String reviewedAt;

    /** MO审核备注（录用/拒绝原因） */
    private String reviewNote;

    /** TA的GPA（申请时快照，便于筛选） */
    private double taGpa;

    /** TA的技能列表快照（逗号分隔，申请时记录） */
    private String taSkillsSnapshot;

    private String moduleCode;
    private String moduleName;

    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public Application() {}

    /**
     * 创建申请的构造方法。
     *
     * @param applicationId 申请ID
     * @param taId          TA用户ID
     * @param taName        TA姓名
     * @param jobId         职位ID
     * @param jobTitle      职位标题
     * @param moId          发布职位的MO用户ID
     * @param coverLetter   求职信
     */
    public Application(String applicationId, String taId, String taName,
                       String jobId, String jobTitle, String moId,
                       String coverLetter) {
        this.applicationId = applicationId;
        this.taId = taId;
        this.taName = taName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.moId = moId;
        this.coverLetter = coverLetter;
        this.status = STATUS_PENDING;
        this.appliedAt = java.time.LocalDateTime.now().toString();
    }

    // ==================== 业务方法 ====================

    /**
     * MO录用该申请。
     *
     * @param note 录用备注（可为null）
     */
    public void accept(String note) {
        this.status = STATUS_ACCEPTED;
        this.reviewNote = note;
        this.reviewedAt = java.time.LocalDateTime.now().toString();
    }

    /**
     * MO拒绝该申请。
     *
     * @param note 拒绝原因（可为null）
     */
    public void reject(String note) {
        this.status = STATUS_REJECTED;
        this.reviewNote = note;
        this.reviewedAt = java.time.LocalDateTime.now().toString();
    }

    /**
     * TA撤回申请（仅PENDING状态可撤回）。
     */
    public void withdraw() {
        this.status = STATUS_WITHDRAWN;
    }

    /**
     * 检查申请是否处于待审核状态。
     *
     * @return 待审核返回true
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(this.status);
    }

    /**
     * 检查申请是否已被录用。
     *
     * @return 已录用返回true
     */
    public boolean isAccepted() {
        return STATUS_ACCEPTED.equals(this.status);
    }

    // ==================== Getter / Setter ====================

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public String getTaName() { return taName; }
    public void setTaName(String taName) { this.taName = taName; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }

    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }

    public double getTaGpa() { return taGpa; }
    public void setTaGpa(double taGpa) { this.taGpa = taGpa; }

    public String getTaSkillsSnapshot() { return taSkillsSnapshot; }
    public void setTaSkillsSnapshot(String taSkillsSnapshot) { this.taSkillsSnapshot = taSkillsSnapshot; }

    @Override
    public String toString() {
        return "Application{id='" + applicationId + "', jobId='" + jobId +
               "', taId='" + taId + "', status='" + status + "'}";
    }
}
