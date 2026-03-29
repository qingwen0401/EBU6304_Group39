package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.services.JobService;

import java.util.List;
import java.util.Optional;

/**
 * MO职位控制器（Module Organiser Job Controller）
 * 处理MO发布、管理职位的所有操作请求。
 *
 * <p>职责：
 * <ul>
 *   <li>接收并验证MO的职位操作请求</li>
 *   <li>调用JobService执行业务逻辑</li>
 *   <li>返回统一格式的响应结果</li>
 * </ul>
 * </p>
 *
 * <p>MO可执行的操作：
 * <ul>
 *   <li>发布新职位</li>
 *   <li>更新职位信息</li>
 *   <li>关闭/取消职位</li>
 *   <li>查看自己发布的职位</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class MOJobController {

    /** 职位业务服务 */
    private final JobService jobService;

    /**
     * 构造方法，注入依赖。
     *
     * @param jobService 职位业务服务
     */
    public MOJobController(JobService jobService) {
        this.jobService = jobService;
    }

    // ==================== 职位发布 ====================

    /**
     * MO发布新职位。
     *
     * @param moId         MO用户ID（当前登录用户）
     * @param moduleCode   模块代码
     * @param moduleName   模块名称
     * @param title        职位标题
     * @param description  职位描述
     * @param requiredSkills 所需技能列表
     * @param hoursPerWeek 每周工作小时数
     * @param vacancies    招聘名额
     * @param deadline     申请截止时间
     * @param semester     学期
     * @param jobType      职位类型
     * @param minGpa       最低GPA要求
     * @param hourlyRate   每小时薪酬
     * @return 操作结果（包含新创建的职位信息）
     */
    public ControllerResult<JobPosting> postJob(
            String moId, String moduleCode, String moduleName,
            String title, String description,
            List<String> requiredSkills,
            int hoursPerWeek, int vacancies,
            String deadline, String semester,
            String jobType, double minGpa, double hourlyRate) {
        try {
            JobPosting job = jobService.postJob(
                    moId, moduleCode, moduleName, title, description,
                    requiredSkills, hoursPerWeek, vacancies,
                    deadline, semester, jobType, minGpa, hourlyRate
            );
            return ControllerResult.success("Job posted successfully", job);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to post job: " + e.getMessage());
        }
    }

    // ==================== 职位更新 ====================

    /**
     * MO更新职位信息。
     *
     * @param moId        MO用户ID
     * @param jobId       职位ID
     * @param title       新标题（null表示不更新）
     * @param description 新描述（null表示不更新）
     * @param deadline    新截止时间（null表示不更新）
     * @param vacancies   新招聘名额（-1表示不更新）
     * @return 操作结果（包含更新后的职位信息）
     */
    public ControllerResult<JobPosting> updateJob(
            String moId, String jobId,
            String title, String description,
            String deadline, int vacancies) {
        try {
            JobPosting job = jobService.updateJob(moId, jobId, title, description, deadline, vacancies);
            return ControllerResult.success("Job updated successfully", job);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to update job: " + e.getMessage());
        }
    }

    // ==================== 职位状态管理 ====================

    /**
     * MO关闭职位（停止接受申请）。
     *
     * @param moId  MO用户ID
     * @param jobId 职位ID
     * @return 操作结果
     */
    public ControllerResult<Void> closeJob(String moId, String jobId) {
        try {
            jobService.closeJob(moId, jobId);
            return ControllerResult.success("Job closed successfully", null);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to close job: " + e.getMessage());
        }
    }

    /**
     * MO取消职位。
     *
     * @param moId  MO用户ID
     * @param jobId 职位ID
     * @return 操作结果
     */
    public ControllerResult<Void> cancelJob(String moId, String jobId) {
        try {
            jobService.cancelJob(moId, jobId);
            return ControllerResult.success("Job cancelled successfully", null);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to cancel job: " + e.getMessage());
        }
    }

    /**
     * MO将职位状态改为审核中。
     *
     * @param moId  MO用户ID
     * @param jobId 职位ID
     * @return 操作结果
     */
    public ControllerResult<Void> startReviewing(String moId, String jobId) {
        try {
            jobService.startReviewing(moId, jobId);
            return ControllerResult.success("Job status changed to REVIEWING", null);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to update job status: " + e.getMessage());
        }
    }

    // ==================== 职位查询 ====================

    /**
     * MO查看自己发布的所有职位。
     *
     * @param moId MO用户ID
     * @return 操作结果（包含职位列表）
     */
    public ControllerResult<List<JobPosting>> getMyJobs(String moId) {
        try {
            List<JobPosting> jobs = jobService.getJobsByMo(moId);
            return ControllerResult.success("Jobs retrieved successfully", jobs);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve jobs: " + e.getMessage());
        }
    }

    /**
     * 获取职位详情。
     *
     * @param jobId 职位ID
     * @return 操作结果（包含职位详情）
     */
    public ControllerResult<JobPosting> getJobDetail(String jobId) {
        try {
            Optional<JobPosting> jobOpt = jobService.getJobById(jobId);
            if (!jobOpt.isPresent()) {
                return ControllerResult.failure("Job not found: " + jobId);
            }
            return ControllerResult.success("Job retrieved successfully", jobOpt.get());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve job: " + e.getMessage());
        }
    }

    /**
     * 获取所有开放职位（供TA浏览）。
     *
     * @return 操作结果（包含开放职位列表）
     */
    public ControllerResult<List<JobPosting>> getOpenJobs() {
        try {
            List<JobPosting> jobs = jobService.getOpenJobs();
            return ControllerResult.success("Open jobs retrieved successfully", jobs);
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve open jobs: " + e.getMessage());
        }
    }
}
