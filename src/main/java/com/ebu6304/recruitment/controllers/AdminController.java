package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器（Admin Controller）
 * 处理系统管理员的所有操作请求，包括：
 * <ul>
 *   <li>查看全局工作量报告</li>
 *   <li>监控超载TA</li>
 *   <li>查看所有职位和申请</li>
 *   <li>管理工作量记录状态</li>
 * </ul>
 *
 * <p>管理员拥有系统最高权限，可查看所有数据但不直接参与招聘流程。</p>
 *
 * @author Group39
 * @version 1.0
 */
public class AdminController {

    /** 职位业务服务 */
    private final JobService jobService;

    /** 申请业务服务 */
    private final ApplicationService applicationService;

    /** 工作量业务服务 */
    private final WorkloadService workloadService;

    /**
     * 构造方法，注入依赖。
     *
     * @param jobService         职位业务服务
     * @param applicationService 申请业务服务
     * @param workloadService    工作量业务服务
     */
    public AdminController(JobService jobService,
                           ApplicationService applicationService,
                           WorkloadService workloadService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.workloadService = workloadService;
    }

    // ==================== 工作量管理 ====================

    /**
     * 获取指定学期所有TA的工作量详细报告。
     * 报告包含每个TA的工作小时数、职位数量、超载状态等信息。
     *
     * @param semester 学期（如 "2026 Spring"），null表示所有学期
     * @return 操作结果（包含工作量报告列表）
     */
    public ControllerResult<List<Map<String, Object>>> getWorkloadReport(String semester) {
        try {
            List<Map<String, Object>> report = workloadService.getWorkloadReport(semester);
            return ControllerResult.success(
                    "Workload report retrieved: " + report.size() + " TA(s)", report);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get workload report: " + e.getMessage());
        }
    }

    /**
     * 获取超载TA列表（工作量超过每周最大限制）。
     *
     * @param semester 学期
     * @return 操作结果（包含超载TA列表）
     */
    public ControllerResult<List<TA>> getOverloadedTAs(String semester) {
        try {
            List<TA> overloaded = workloadService.getOverloadedTAs(semester);
            String msg = overloaded.isEmpty()
                    ? "No overloaded TAs found"
                    : overloaded.size() + " overloaded TA(s) found";
            return ControllerResult.success(msg, overloaded);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get overloaded TAs: " + e.getMessage());
        }
    }

    /**
     * 获取所有工作量记录（管理员全局视图）。
     *
     * @return 操作结果（包含所有工作量记录）
     */
    public ControllerResult<List<WorkloadRecord>> getAllWorkloadRecords() {
        try {
            List<WorkloadRecord> records = workloadService.getAllWorkloadRecords();
            return ControllerResult.success(
                    "Retrieved " + records.size() + " workload record(s)", records);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get workload records: " + e.getMessage());
        }
    }

    /**
     * 将工作量记录标记为已完成（学期结束时使用）。
     *
     * @param recordId 工作量记录ID
     * @return 操作结果
     */
    public ControllerResult<Void> completeWorkloadRecord(String recordId) {
        try {
            workloadService.completeWorkloadRecord(recordId);
            return ControllerResult.success("Workload record marked as completed", null);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to complete workload record: " + e.getMessage());
        }
    }

    /**
     * 取消工作量记录。
     *
     * @param recordId 工作量记录ID
     * @return 操作结果
     */
    public ControllerResult<Void> cancelWorkloadRecord(String recordId) {
        try {
            workloadService.cancelWorkloadRecord(recordId);
            return ControllerResult.success("Workload record cancelled", null);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to cancel workload record: " + e.getMessage());
        }
    }

    // ==================== 职位管理（全局视图） ====================

    /**
     * 获取系统中所有职位（管理员全局视图）。
     *
     * @return 操作结果（包含所有职位列表）
     */
    public ControllerResult<List<JobPosting>> getAllJobs() {
        try {
            List<JobPosting> jobs = jobService.getAllJobs();
            return ControllerResult.success(
                    "Retrieved " + jobs.size() + " job(s)", jobs);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get all jobs: " + e.getMessage());
        }
    }

    /**
     * 获取所有开放状态的职位。
     *
     * @return 操作结果（包含开放职位列表）
     */
    public ControllerResult<List<JobPosting>> getAllOpenJobs() {
        try {
            List<JobPosting> jobs = jobService.getOpenJobs();
            return ControllerResult.success(
                    "Retrieved " + jobs.size() + " open job(s)", jobs);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get open jobs: " + e.getMessage());
        }
    }

    // ==================== 申请管理（全局视图） ====================

    /**
     * 获取系统中所有申请（管理员全局视图）。
     *
     * @return 操作结果（包含所有申请列表）
     */
    public ControllerResult<List<Application>> getAllApplications() {
        try {
            List<Application> applications = applicationService.getAllApplications();
            return ControllerResult.success(
                    "Retrieved " + applications.size() + " application(s)", applications);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get all applications: " + e.getMessage());
        }
    }

    /**
     * 获取指定TA的所有申请记录。
     *
     * @param taId TA用户ID
     * @return 操作结果（包含该TA的申请列表）
     */
    public ControllerResult<List<Application>> getApplicationsByTA(String taId) {
        try {
            List<Application> applications = applicationService.getApplicationsByTA(taId);
            return ControllerResult.success(
                    "Retrieved " + applications.size() + " application(s) for TA: " + taId,
                    applications);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get applications: " + e.getMessage());
        }
    }

    /**
     * 获取指定职位的所有申请记录。
     *
     * @param jobId 职位ID
     * @return 操作结果（包含该职位的申请列表）
     */
    public ControllerResult<List<Application>> getApplicationsByJob(String jobId) {
        try {
            List<Application> applications = applicationService.getApplicationsByJob(jobId);
            return ControllerResult.success(
                    "Retrieved " + applications.size() + " application(s) for job: " + jobId,
                    applications);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get applications: " + e.getMessage());
        }
    }

    // ==================== 系统统计 ====================

    /**
     * 获取系统概览统计信息。
     * 包含：总职位数、开放职位数、总申请数、待处理申请数等。
     *
     * @return 操作结果（包含统计信息Map）
     */
    public ControllerResult<Map<String, Object>> getSystemStats() {
        try {
            List<JobPosting> allJobs = jobService.getAllJobs();
            List<JobPosting> openJobs = jobService.getOpenJobs();
            List<Application> allApps = applicationService.getAllApplications();

            long pendingApps = allApps.stream()
                    .filter(a -> "PENDING".equals(a.getStatus()))
                    .count();
            long acceptedApps = allApps.stream()
                    .filter(a -> "ACCEPTED".equals(a.getStatus()))
                    .count();

            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalJobs", allJobs.size());
            stats.put("openJobs", openJobs.size());
            stats.put("totalApplications", allApps.size());
            stats.put("pendingApplications", pendingApps);
            stats.put("acceptedApplications", acceptedApps);

            return ControllerResult.success("System stats retrieved", stats);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to get system stats: " + e.getMessage());
        }
    }
}
