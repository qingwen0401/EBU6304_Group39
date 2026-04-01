package com.ebu6304.recruitment.controllers;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.CvFileData;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.WorkloadService;

import java.util.List;

/**
 * MO申请审核控制器（Module Organiser Application Controller）
 * 处理MO查看、审核TA申请的所有操作请求。
 *
 * <p>MO可执行的操作：
 * <ul>
 *   <li>查看某职位的所有申请</li>
 *   <li>将申请标记为审核中</li>
 *   <li>录用申请人</li>
 *   <li>拒绝申请人</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class MOApplicationController {

    /** 申请业务服务 */
    private final ApplicationService applicationService;

    /** 工作量业务服务（录用前检查TA工作量） */
    private final WorkloadService workloadService;

    /**
     * 构造方法，注入依赖。
     *
     * @param applicationService 申请业务服务
     * @param workloadService    工作量业务服务
     */
    public MOApplicationController(ApplicationService applicationService,
                                    WorkloadService workloadService) {
        this.applicationService = applicationService;
        this.workloadService = workloadService;
    }

    // ==================== 申请查询 ====================

    /**
     * MO查看某职位的所有申请列表。
     *
     * @param moId  MO用户ID（当前登录用户）
     * @param jobId 职位ID
     * @return 操作结果（包含申请列表）
     */
    public ControllerResult<List<Application>> getApplicationsForJob(String moId, String jobId) {
        try {
            List<Application> applications = applicationService.getApplicationsForJob(moId, jobId);
            return ControllerResult.success(
                    "Retrieved " + applications.size() + " applications", applications);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve applications: " + e.getMessage());
        }
    }

    /**
     * 获取申请详情。
     *
     * @param applicationId 申请ID
     * @return 操作结果（包含申请详情）
     */
    public ControllerResult<Application> getApplicationDetail(String applicationId) {
        try {
            return applicationService.getApplicationById(applicationId)
                    .map(app -> ControllerResult.success("Application retrieved", app))
                    .orElse(ControllerResult.failure("Application not found: " + applicationId));
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve application: " + e.getMessage());
        }
    }

    /**
     * MO查看申请关联CV（仅该职位所属MO可见）。
     */
    public ControllerResult<CvFileData> getApplicationCv(String moId, String applicationId) {
        try {
            CvFileData cv = applicationService.getCvForApplicationAsMo(moId, applicationId);
            return ControllerResult.success("CV retrieved", cv);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to retrieve CV: " + e.getMessage());
        }
    }

    // ==================== 申请审核 ====================

    /**
     * MO将申请标记为审核中。
     *
     * @param moId          MO用户ID
     * @param applicationId 申请ID
     * @return 操作结果（包含更新后的申请）
     */
    public ControllerResult<Application> markAsReviewing(String moId, String applicationId) {
        try {
            Application app = applicationService.markAsReviewing(moId, applicationId);
            return ControllerResult.success("Application marked as reviewing", app);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to update application: " + e.getMessage());
        }
    }

    /**
     * MO录用申请人。
     * 录用前会检查TA的工作量，若超载则给出警告（但仍允许录用）。
     *
     * @param moId          MO用户ID
     * @param applicationId 申请ID
     * @param feedback      录用反馈（可选）
     * @param semester      学期（用于工作量检查）
     * @param jobHoursPerWeek 职位每周工作小时数（用于工作量检查）
     * @return 操作结果（包含更新后的申请）
     */
    public ControllerResult<Application> acceptApplication(
            String moId, String applicationId,
            String feedback, String semester, int jobHoursPerWeek) {
        try {
            // 先获取申请信息
            Application app = applicationService.getApplicationById(applicationId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Application not found: " + applicationId));

            // 工作量预检查（警告但不阻止）
            String workloadWarning = null;
            if (semester != null && jobHoursPerWeek > 0) {
                if (workloadService.wouldExceedLimit(app.getTaId(), semester, jobHoursPerWeek)) {
                    workloadWarning = "WARNING: This TA will exceed the maximum workload limit after acceptance.";
                }
            }

            // 执行录用
            Application accepted = applicationService.acceptApplication(moId, applicationId, feedback);

            String message = "Application accepted successfully";
            if (workloadWarning != null) {
                message = message + " | " + workloadWarning;
            }

            return ControllerResult.success(message, accepted);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to accept application: " + e.getMessage());
        }
    }

    /**
     * MO拒绝申请人。
     *
     * @param moId          MO用户ID
     * @param applicationId 申请ID
     * @param feedback      拒绝原因（可选）
     * @return 操作结果（包含更新后的申请）
     */
    public ControllerResult<Application> rejectApplication(
            String moId, String applicationId, String feedback) {
        try {
            Application app = applicationService.rejectApplication(moId, applicationId, feedback);
            return ControllerResult.success("Application rejected", app);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to reject application: " + e.getMessage());
        }
    }

    // ==================== 批量操作 ====================

    /**
     * MO批量拒绝某职位的所有待审核申请（通常在录用完成后调用）。
     *
     * @param moId  MO用户ID
     * @param jobId 职位ID
     * @return 操作结果（包含被拒绝的申请数量）
     */
    public ControllerResult<Integer> rejectAllPendingApplications(String moId, String jobId) {
        try {
            List<Application> applications = applicationService.getApplicationsForJob(moId, jobId);
            int rejectedCount = 0;

            for (Application app : applications) {
                if (Application.STATUS_PENDING.equals(app.getStatus())
                        || Application.STATUS_REVIEWING.equals(app.getStatus())) {
                    applicationService.rejectApplication(moId, app.getApplicationId(),
                            "Position has been filled");
                    rejectedCount++;
                }
            }

            return ControllerResult.success(
                    "Rejected " + rejectedCount + " pending applications", rejectedCount);
        } catch (IllegalArgumentException e) {
            return ControllerResult.failure(e.getMessage());
        } catch (Exception e) {
            return ControllerResult.failure("Failed to reject applications: " + e.getMessage());
        }
    }
}
