package com.ebu6304.recruitment.models;

/**
 * 用户基类
 * 系统中所有用户类型（TA、MO、Admin）的父类。
 * 包含通用的用户属性：ID、用户名、密码哈希、邮箱、角色等。
 *
 * <p>角色枚举：
 * <ul>
 *   <li>TA - 助教（Teaching Assistant）</li>
 *   <li>MO - 模块负责人（Module Organiser）</li>
 *   <li>ADMIN - 系统管理员</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class User {

    /** 用户唯一标识符 */
    private String userId;

    /** 登录用户名（唯一） */
    private String username;

    /** 密码哈希（SHA-256加盐，格式：{salt}:{hash}） */
    private String passwordHash;

    /** 用户邮箱 */
    private String email;

    /** 用户角色：TA / MO / ADMIN */
    private String role;

    /** 账户创建时间（ISO-8601格式字符串，便于JSON序列化） */
    private String createdAt;

    /** 账户是否激活 */
    private boolean active;

    /** 真实姓名 */
    private String fullName;

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public User() {}

    /**
     * 全参构造方法。
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param passwordHash 密码哈希
     * @param email        邮箱
     * @param role         角色
     * @param fullName     真实姓名
     */
    public User(String userId, String username, String passwordHash,
                String email, String role, String fullName) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.active = true;
        this.createdAt = java.time.LocalDateTime.now().toString();
    }

    // ==================== Getter / Setter ====================

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @Override
    public String toString() {
        return "User{userId='" + userId + "', username='" + username +
               "', role='" + role + "', email='" + email + "'}";
    }
}
