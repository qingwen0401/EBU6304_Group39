package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadConfigRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作量服务（Workload Service）
 * 处理TA工作量统计、超载检测等业务逻辑。
 * 管理员可通过此服务监控所有TA的工作负荷。
 *
 * <p>工作量规则：
 * <ul>
 *   <li>每个TA每学期最多工作 {@link #MAX_WEEKLY_HOURS} 小时/周</li>
 *   <li>超过阈值时系统发出警告</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class WorkloadService {

    /** 每周最大工作小时数限制（超过此值视为超载） */
    public static final int MAX_WEEKLY_HOURS = 20;

    /** 工作量警告阈值（超过此值发出警告） */
    public static final int WARNING_WEEKLY_HOURS = 15;

    /** 工作量记录数据访问层 */
    private final WorkloadRepository workloadRepository;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    private final WorkloadConfigRepository workloadConfigRepository;

    /**
     * 构造方法，注入依赖。
     *
     * @param workloadRepository 工作量记录数据访问层
     * @param userRepository     用户数据访问层
     */
    public WorkloadService(WorkloadRepository workloadRepository,
                           UserRepository userRepository) {
        this(workloadRepository, userRepository, new WorkloadConfigRepository());
    }

    public WorkloadService(WorkloadRepository workloadRepository,
                           UserRepository userRepository,
                           WorkloadConfigRepository workloadConfigRepository) {
        this.workloadRepository = workloadRepository;
        this.userRepository = userRepository;
        this.workloadConfigRepository = workloadConfigRepository;
    }

    // ==================== 工作量查询 ====================

    /**
     * 获取指定TA在指定学期的工作量记录。
     *
     * @param taId     TA用户ID
     * @param semester 学期（如 "2026 Spring"）
     * @return 工作量记录列表
     */
    public List<WorkloadRecord> getTAWorkload(String taId, String semester) {
        return workloadRepository.findByTaIdAndSemester(taId, semester);
    }

    /**
     * 计算指定TA在指定学期的总工作小时数（仅统计ACTIVE状态）。
     *
     * @param taId     TA用户ID
     * @param semester 学期
     * @return 总工作小时数/周
     */
    public int getTotalWeeklyHours(String taId, String semester) {
        return workloadRepository.calculateTotalHours(taId, semester);
    }

    /**
     * 检查TA是否超载。
     *
     * @param taId     TA用户ID
     * @param semester 学期
     * @return 超载返回true
     */
    public boolean isOverloaded(String taId, String semester) {
        return getTotalWeeklyHours(taId, semester) > getMaxWeeklyHours();
    }

    /**
     * 检查TA是否接近超载（超过警告阈值）。
     *
     * @param taId     TA用户ID
     * @param semester 学期
     * @return 接近超载返回true
     */
    public boolean isNearOverload(String taId, String semester) {
        int hours = getTotalWeeklyHours(taId, semester);
        return hours > getWarningWeeklyHours() && hours <= getMaxWeeklyHours();
    }

    /**
     * 检查TA接受新职位后是否会超载。
     *
     * @param taId            TA用户ID
     * @param semester        学期
     * @param additionalHours 新职位的每周工作小时数
     * @return 会超载返回true
     */
    public boolean wouldExceedLimit(String taId, String semester, int additionalHours) {
        int currentHours = getTotalWeeklyHours(taId, semester);
        return (currentHours + additionalHours) > getMaxWeeklyHours();
    }

    // ==================== 管理员统计 ====================

    /**
     * 获取所有TA在指定学期的工作量汇总。
     * 返回Map：taId → 总工作小时数
     *
     * @param semester 学期
     * @return 工作量汇总Map
     */
    public Map<String, Integer> getAllTAWorkloadSummary(String semester) {
        Map<String, Integer> summary = new HashMap<>();
        List<TA> allTAs = userRepository.findAllTAs();

        for (TA ta : allTAs) {
            int hours = getTotalWeeklyHours(ta.getUserId(), semester);
            summary.put(ta.getUserId(), hours);
        }

        return summary;
    }

    /**
     * 获取超载的TA列表（工作量超过最大限制）。
     *
     * @param semester 学期
     * @return 超载TA列表
     */
    public List<TA> getOverloadedTAs(String semester) {
        List<TA> overloaded = new ArrayList<>();
        List<TA> allTAs = userRepository.findAllTAs();

        for (TA ta : allTAs) {
            if (isOverloaded(ta.getUserId(), semester)) {
                overloaded.add(ta);
            }
        }

        return overloaded;
    }

    /**
     * 获取工作量详细报告（包含每个TA的工作量状态）。
     *
     * @param semester 学期
     * @return 工作量报告列表，每项包含TA信息和工作量状态
     */
    public List<Map<String, Object>> getWorkloadReport(String semester) {
        List<Map<String, Object>> report = new ArrayList<>();
        List<TA> allTAs = userRepository.findAllTAs();

        for (TA ta : allTAs) {
            int hours = getTotalWeeklyHours(ta.getUserId(), semester);
            List<WorkloadRecord> records = getTAWorkload(ta.getUserId(), semester);

            Map<String, Object> entry = new HashMap<>();
            entry.put("taId", ta.getUserId());
            entry.put("taName", ta.getFullName());
            entry.put("studentId", ta.getStudentId());
            entry.put("totalWeeklyHours", hours);
            entry.put("jobCount", records.size());
            entry.put("isOverloaded", hours > getMaxWeeklyHours());
            entry.put("isNearOverload", hours > getWarningWeeklyHours() && hours <= getMaxWeeklyHours());
            entry.put("workloadStatus", getWorkloadStatus(hours));
            entry.put("records", records);

            report.add(entry);
        }

        return report;
    }

    /**
     * 获取指定MO管理的TA工作量汇总。
     *
     * @param moId     MO用户ID
     * @param semester 学期
     * @return 该MO管理的工作量记录列表
     */
    public List<WorkloadRecord> getWorkloadByMO(String moId, String semester) {
        List<WorkloadRecord> allRecords = workloadRepository.findByMoId(moId);
        List<WorkloadRecord> result = new ArrayList<>();
        for (WorkloadRecord r : allRecords) {
            if (semester == null || semester.equals(r.getSemester())) {
                result.add(r);
            }
        }
        return result;
    }

    // ==================== 工作量记录管理 ====================

    /**
     * 取消工作量记录（当申请被撤回或职位取消时调用）。
     *
     * @param recordId 工作量记录ID
     */
    public void cancelWorkloadRecord(String recordId) {
        workloadRepository.findById(recordId).ifPresent(record -> {
            record.setStatus("CANCELLED");
            workloadRepository.save(record);
        });
    }

    /**
     * 完成工作量记录（学期结束时调用）。
     *
     * @param recordId 工作量记录ID
     */
    public void completeWorkloadRecord(String recordId) {
        workloadRepository.findById(recordId).ifPresent(record -> {
            record.setStatus("COMPLETED");
            workloadRepository.save(record);
        });
    }

    /**
     * 获取所有工作量记录（管理员用）。
     *
     * @return 所有工作量记录
     */
    public List<WorkloadRecord> getAllWorkloadRecords() {
        return workloadRepository.findAll();
    }

    public int getMaxWeeklyHours() {
        return workloadConfigRepository.getConfig().getMaxWeeklyHours();
    }

    public int getWarningWeeklyHours() {
        return Math.max(0, getMaxWeeklyHours() - 5);
    }

    public void setMaxWeeklyHours(int maxWeeklyHours) {
        if (maxWeeklyHours < 1 || maxWeeklyHours > 80) {
            throw new IllegalArgumentException("Threshold must be between 1 and 80 hours.");
        }
        workloadConfigRepository.save(new com.ebu6304.recruitment.models.WorkloadConfig(maxWeeklyHours));
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据工作小时数返回工作量状态描述。
     *
     * @param hours 每周工作小时数
     * @return 状态描述字符串
     */
    private String getWorkloadStatus(int hours) {
        if (hours == 0) return "IDLE";
        if (hours <= getWarningWeeklyHours()) return "NORMAL";
        if (hours <= getMaxWeeklyHours()) return "WARNING";
        return "OVERLOADED";
    }
}
