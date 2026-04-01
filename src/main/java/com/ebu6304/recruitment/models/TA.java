package com.ebu6304.recruitment.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 助教（Teaching Assistant，TA）实体类
 * 继承自User，增加TA特有属性：学号、GPA、技能列表、简历路径、申请记录等。
 *
 * <p>TA的核心功能：
 * <ul>
 *   <li>创建并维护个人档案</li>
 *   <li>上传简历</li>
 *   <li>浏览并申请职位</li>
 *   <li>查看申请状态</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class TA extends User {

    /** 学号 */
    private String studentId;

    /** 所在院系 */
    private String department;

    /** 专业 */
    private String major;

    /** 年级（如 "Year 3", "Postgraduate"） */
    private String year;

    /** GPA（0.0 - 4.0） */
    private double gpa;

    /** 技能列表（如 ["Java", "Python", "UML"]） */
    private List<String> skills;

    /** 简历文件路径（相对于data目录） */
    private String cvPath;

    /** 个人简介 */
    private String bio;

    /** 每周可用时间（JSON字符串，格式：{"morning":{"mon":true,...},...}） */
    private String availability;

    /** 该TA提交的所有申请ID列表 */
    private List<String> applicationIds;

    /** 当前每周工作小时数（用于工作量控制） */
    private int currentWeeklyHours;

    /** 最大允许每周工作小时数（默认20小时） */
    private int maxWeeklyHours;

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public TA() {
        super();
        this.skills = new ArrayList<>();
        this.applicationIds = new ArrayList<>();
        this.maxWeeklyHours = 20;
        this.currentWeeklyHours = 0;
    }

    /**
     * 创建TA实例的构造方法。
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param passwordHash 密码哈希
     * @param email        邮箱
     * @param fullName     真实姓名
     * @param studentId    学号
     * @param department   院系
     * @param major        专业
     */
    public TA(String userId, String username, String passwordHash,
              String email, String fullName,
              String studentId, String department, String major) {
        super(userId, username, passwordHash, email, "TA", fullName);
        this.studentId = studentId;
        this.department = department;
        this.major = major;
        this.skills = new ArrayList<>();
        this.applicationIds = new ArrayList<>();
        this.maxWeeklyHours = 20;
        this.currentWeeklyHours = 0;
    }

    // ==================== 业务方法 ====================

    /**
     * 添加技能到技能列表（去重）。
     *
     * @param skill 技能名称
     */
    public void addSkill(String skill) {
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        }
        if (skill != null && !skill.trim().isEmpty() && !this.skills.contains(skill)) {
            this.skills.add(skill.trim());
        }
    }

    /**
     * 移除技能。
     *
     * @param skill 技能名称
     */
    public void removeSkill(String skill) {
        if (this.skills != null) {
            this.skills.remove(skill);
        }
    }

    /**
     * 检查TA是否具备指定技能（不区分大小写）。
     *
     * @param skill 技能名称
     * @return 具备返回true，否则返回false
     */
    public boolean hasSkill(String skill) {
        if (this.skills == null || skill == null) return false;
        return this.skills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(skill.trim()));
    }

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
     * 检查TA是否还有可用工作时间。
     *
     * @param additionalHours 需要增加的小时数
     * @return 有可用时间返回true，否则返回false
     */
    public boolean hasAvailableHours(int additionalHours) {
        return (this.currentWeeklyHours + additionalHours) <= this.maxWeeklyHours;
    }

    /**
     * 增加当前工作小时数。
     *
     * @param hours 增加的小时数
     */
    public void addWorkHours(int hours) {
        this.currentWeeklyHours += hours;
    }

    // ==================== Getter / Setter ====================

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public List<String> getApplicationIds() { return applicationIds; }
    public void setApplicationIds(List<String> applicationIds) { this.applicationIds = applicationIds; }

    public int getCurrentWeeklyHours() { return currentWeeklyHours; }
    public void setCurrentWeeklyHours(int currentWeeklyHours) { this.currentWeeklyHours = currentWeeklyHours; }

    public int getMaxWeeklyHours() { return maxWeeklyHours; }
    public void setMaxWeeklyHours(int maxWeeklyHours) { this.maxWeeklyHours = maxWeeklyHours; }

    @Override
    public String toString() {
        return "TA{userId='" + getUserId() + "', name='" + getFullName() +
               "', studentId='" + studentId + "', gpa=" + gpa +
               ", skills=" + skills + "}";
    }
}
