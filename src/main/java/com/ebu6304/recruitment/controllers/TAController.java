package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;

import java.util.List;

/**
 * TA控制器（Teaching Assistant Controller）
 * 处理TA浏览职位、提交申请、查看申请状态等操作请求。
 *
 * <p>TA可执行的操作：
 * <ul>
 *   <li>浏览所有开放职位</li>
 *   <li>提交职位申请</li>
 *   <li>撤回申请</li>
 *   <li>查看自己的申请状态</li>
 *   <li>查看自己的工作量</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class TAController {

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
    public TAController(JobService jobService,
                        ApplicationService applicationService,
                        WorkloadService workloadService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.workloadService = workloadService;
    }

    // ==================== 职位浏览 ====================

    /**
     * TA浏览所有开放职位。
     *
     * @return 操作结果（包含开放职位列表）
     */
    public ControllerResult<List<JobPosting>> browseOpenJobs() {
        try {
            List<JobPosting> jobs = jobService.getOpenJobs();
            return ControllerResult.success(
                    "Found " + jobs.size() + " open jobs", jobs);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve jobs: " + e.getMessage());
        }
    }

    /**
     * TA按学期筛选职位。
     *
     * @param semester 学期
     * @return 操作结果（包含该学期的职位列表）
     */
    public ControllerResult<List<JobPosting>> browseJobsBySemester(String semester) {
        try {
            List<JobPosting> jobs = jobService.getJobsBySemester(semester);
            return ControllerResult.success(
                    "Found " + jobs.size() + " jobs for semester: " + semester, jobs);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve jobs: " + e.getMessage());
        }
    }

    /**
     * TA查看职位详情。
     *
     * @param jobId 职位ID
     * @return 操作结果（包含职位详情）
     */
    public ControllerResult<JobPosting> viewJobDetail(String jobId) {
        try {
            return jobService.getJobById(jobId)
                    .map(job -> ControllerResult.success("Job retrieved", job))
                    .orElse(ControllerResult.failure("Job not found: " + jobId));
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve job: " + e.getMessage());
        }
    }

    // ==================== 申请操作 ====================

    /**
     * TA提交职位申请。
     *
     * @param taId        TA用户ID（当前登录用户）
     * @param jobId       职位ID
     * @param coverLetter 求职信
     * @param cvPath      简历文件路径（可选）
     * @return 操作结果（包含创建的申请）
     */
    public ControllerResult<Application> applyForJob(
            String taId, String jobId, String coverLetter, String cvPath) {
        try {
            Application application = applicationService.applyForJob(taId, jobId, coverLetter, cvPath);
            return ControllerResult.success("Application submitted successfully", application);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to submit application: " + e.getMessage());
        }
    }

    /**
     * TA撤回申请。
     *
     * @param taId          TA用户ID
     * @param applicationId 申请ID
     * @return 操作结果
     */
    public ControllerResult<Void> withdrawApplication(String taId, String applicationId) {
        try {
            applicationService.withdrawApplication(taId, applicationId);
            return ControllerResult.success("Application withdrawn successfully", null);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to withdraw application: " + e.getMessage());
        }
    }

    // ==================== 申请状态查询 ====================

    /**
     * TA查看自己的所有申请。
     *
     * @param taId TA用户ID
     * @return 操作结果（包含申请列表）
     */
    public ControllerResult<List<Application>> getMyApplications(String taId) {
        try {
            List<Application> applications = applicationService.getApplicationsByTA(taId);
            return ControllerResult.success(
                    "Found " + applications.size() + " applications", applications);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve applications: " + e.getMessage());
        }
    }

    /**
     * TA查看某个申请的详情。
     *
     * @param taId          TA用户ID（权限验证）
     * @param applicationId 申请ID
     * @return 操作结果（包含申请详情）
     */
    public ControllerResult<Application> getApplicationDetail(String taId, String applicationId) {
        try {
            return applicationService.getApplicationById(applicationId)
                    .filter(app -> taId.equals(app.getTaId()))
                    .map(app -> ControllerResult.success("Application retrieved", app))
                    .orElse(ControllerResult.failure(
                            "Application not found or you don't have permission"));
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve application: " + e.getMessage());
        }
    }

    // ==================== 工作量查询 ====================

    /**
     * TA查看自己在指定学期的工作量。
     *
     * @param taId     TA用户ID
     * @param semester 学期
     * @return 操作结果（包含工作量记录列表）
     */
    public ControllerResult<List<WorkloadRecord>> getMyWorkload(String taId, String semester) {
        try {
            List<WorkloadRecord> records = workloadService.getTAWorkload(taId, semester);
            int totalHours = workloadService.getTotalWeeklyHours(taId, semester);
            String status = totalHours > WorkloadService.MAX_WEEKLY_HOURS ? " [OVERLOADED]"
                    : totalHours > WorkloadService.WARNING_WEEKLY_HOURS ? " [WARNING]" : "";
            return ControllerResult.success(
                    "Total weekly hours: " + totalHours + status, records);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve workload: " + e.getMessage());
        }
    }
}
