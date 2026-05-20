package com.ebu6304.recruitment.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块负责人（Module Organiser，MO）实体类
 * 继承自User，增加MO特有属性：所属院系、负责模块、已发布职位列表等。
 *
 * <p>MO的核心职责：
 * <ul>
 *   <li>发布TA招聘职位</li>
 *   <li>审核TA申请</li>
 *   <li>选定合适的TA</li>
 *   <li>管理职位生命周期</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class ModuleOrganiser extends User {

    /** 所属院系（如 "Computer Science", "Electronic Engineering"） */
    private String department;

    /** 负责的模块代码（如 "EBU6304"） */
    private String moduleCode;

    /** 负责的模块名称（如 "Software Engineering"） */
    private String moduleName;

    /** 该MO发布的所有职位ID列表 */
    private List<String> postedJobIds;

    /** 联系电话 */
    private String phone;

    /** 办公室地址 */
    private String officeLocation;

    /** DeepSeek API密钥（用于AI技能匹配） */
    private String deepseekApiKey;

    // ==================== 构造方法 ====================

    /** 无参构造（Gson反序列化需要） */
    public ModuleOrganiser() {
        super();
        this.postedJobIds = new ArrayList<>();
    }

    /**
     * 创建MO实例的构造方法。
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param passwordHash 密码哈希
     * @param email        邮箱
     * @param fullName     真实姓名
     * @param department   所属院系
     * @param moduleCode   模块代码
     * @param moduleName   模块名称
     */
    public ModuleOrganiser(String userId, String username, String passwordHash,
                           String email, String fullName,
                           String department, String moduleCode, String moduleName) {
        super(userId, username, passwordHash, email, "MO", fullName);
        this.department = department;
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.postedJobIds = new ArrayList<>();
    }

    // ==================== 业务方法 ====================

    /**
     * 添加已发布的职位ID到列表。
     *
     * @param jobId 职位ID
     */
    public void addPostedJob(String jobId) {
        if (this.postedJobIds == null) {
            this.postedJobIds = new ArrayList<>();
        }
        if (!this.postedJobIds.contains(jobId)) {
            this.postedJobIds.add(jobId);
        }
    }

    /**
     * 移除已发布的职位ID。
     *
     * @param jobId 职位ID
     */
    public void removePostedJob(String jobId) {
        if (this.postedJobIds != null) {
            this.postedJobIds.remove(jobId);
        }
    }

    /**
     * 检查该MO是否拥有指定职位。
     *
     * @param jobId 职位ID
     * @return 拥有返回true，否则返回false
     */
    public boolean ownsJob(String jobId) {
        return this.postedJobIds != null && this.postedJobIds.contains(jobId);
    }

    // ==================== Getter / Setter ====================

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public List<String> getPostedJobIds() { return postedJobIds; }
    public void setPostedJobIds(List<String> postedJobIds) { this.postedJobIds = postedJobIds; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public String getDeepseekApiKey() { return deepseekApiKey; }
    public void setDeepseekApiKey(String deepseekApiKey) { this.deepseekApiKey = deepseekApiKey; }

    @Override
    public String toString() {
        return "ModuleOrganiser{userId='" + getUserId() + "', name='" + getFullName() +
               "', module='" + moduleCode + " " + moduleName + "', dept='" + department + "'}";
    }
}
