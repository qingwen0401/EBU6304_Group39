package com.ebu6304.recruitment.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 职位发布实体类（Job Posting）
 * 由MO创建，描述TA招聘职位的详细信息。
 *
 * <p>职位状态流转：
 * <pre>
 *   OPEN（开放申请）→ REVIEWING（审核中）→ CLOSED（已关闭）
 *                                        ↑
 *                                   CANCELLED（已取消）
 * </pre>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class JobPosting {

    // ==================== 职位状态常量 ====================

    /** 职位状态：开放申请 */
    public static final String STATUS_OPEN = "OPEN";

    /** 职位状态：已关闭（招聘完成） */
    public static final String STATUS_CLOSED = "CLOSED";

    /** 职位状态：已取消 */
    public static final String STATUS_CANCELLED = "CANCELLED";

    // ==================== 字段定义 ====================

    /** 职位唯一ID */
    private String jobId;

    /** 发布该职位的MO的用户ID */
    private String moId;

    /** MO的姓名（冗余存储，便于显示） */
    private String moName;

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

    /** 招聘名额 */
    private int vacancies;

    /** 已录用人数 */
    private int filledCount;

    /** 职位状态：OPEN / REVIEWING / CLOSED / CANCELLED */
    private String status;

    /** 发布时间（ISO-8601字符串） */
    private String postedAt;

    /** 申请截止时间（ISO-8601字符串） */
    private String deadline;

    /** 学期（如 "2026 Spring"） */
    private String semester;

    /** 职位类型（如 "Module TA", "Invigilation", "Lab Assistant"） */
    private String jobType;

    /** 最低GPA要求（0.0表示无要求） */
    private double minGpa;

    /** 申请该职位的申请ID列表 */
    private List<String> applicationIds;

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public JobPosting() {
        this.requiredSkills = new ArrayList<>();
        this.applicationIds = new ArrayList<>();
        this.status = STATUS_OPEN;
        this.filledCount = 0;
    }

    /**
     * 创建职位的构造方法。
     *
     * @param jobId        职位ID
     * @param moId         MO的用户ID
     * @param moName       MO姓名
     * @param moduleCode   模块代码
     * @param moduleName   模块名称
     * @param title        职位标题
     * @param description  职位描述
     * @param hoursPerWeek 每周工作小时数
     * @param vacancies    招聘名额
     * @param deadline     申请截止时间
     * @param semester     学期
     */
    public JobPosting(String jobId, String moId, String moName,
                      String moduleCode, String moduleName,
                      String title, String description,
                      int hoursPerWeek, int vacancies,
                      String deadline, String semester) {
        this.jobId = jobId;
        this.moId = moId;
        this.moName = moName;
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.title = title;
        this.description = description;
        this.hoursPerWeek = hoursPerWeek;
        this.vacancies = vacancies;
        this.deadline = deadline;
        this.semester = semester;
        this.requiredSkills = new ArrayList<>();
        this.applicationIds = new ArrayList<>();
        this.status = STATUS_OPEN;
        this.filledCount = 0;
        this.postedAt = java.time.LocalDateTime.now().toString();
    }

    // ==================== 业务方法 ====================

    /**
     * 添加申请ID到申请列表。
     *
     * @param applicationId 申请ID
     */
    public void addApplication(String applicationId) {
        if (this.applicationIds == null) {
            this.applicationIds = new ArrayList<>();
        }
        if (!this.applicationIds.contains(applicationId)) {
            this.applicationIds.add(applicationId);
        }
    }

    /**
     * 检查职位是否仍在接受申请。
     *
     * @return 开放状态返回true，否则返回false
     */
    public boolean isOpen() {
        return STATUS_OPEN.equals(this.status);
    }

    /**
     * 检查职位是否已满员。
     *
     * @return 已满员返回true，否则返回false
     */
    public boolean isFull() {
        return this.filledCount >= this.vacancies;
    }

    /**
     * 剩余招聘名额。
     *
     * @return 剩余名额数
     */
    public int remainingVacancies() {
        return Math.max(0, this.vacancies - this.filledCount);
    }

    /**
     * 增加已录用人数（录用一名TA时调用）。
     */
    public void incrementFilledCount() {
        this.filledCount++;
        // 如果已满员，自动关闭职位
        if (isFull()) {
            this.status = STATUS_CLOSED;
        }
    }

    /**
     * 关闭职位。
     */
    public void close() {
        this.status = STATUS_CLOSED;
    }

    /**
     * 取消职位。
     */
    public void cancel() {
        this.status = STATUS_CANCELLED;
    }

    // ==================== Getter / Setter ====================

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public String getMoName() { return moName; }
    public void setMoName(String moName) { this.moName = moName; }

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

    public int getVacancies() { return vacancies; }
    public void setVacancies(int vacancies) { this.vacancies = vacancies; }

    public int getFilledCount() { return filledCount; }
    public void setFilledCount(int filledCount) { this.filledCount = filledCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPostedAt() { return postedAt; }
    public void setPostedAt(String postedAt) { this.postedAt = postedAt; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public double getMinGpa() { return minGpa; }
    public void setMinGpa(double minGpa) { this.minGpa = minGpa; }

    public List<String> getApplicationIds() { return applicationIds; }
    public void setApplicationIds(List<String> applicationIds) { this.applicationIds = applicationIds; }

    @Override
    public String toString() {
        return "JobPosting{jobId='" + jobId + "', title='" + title +
               "', module='" + moduleCode + "', status='" + status +
               "', vacancies=" + vacancies + ", filled=" + filledCount + "}";
    }
}
