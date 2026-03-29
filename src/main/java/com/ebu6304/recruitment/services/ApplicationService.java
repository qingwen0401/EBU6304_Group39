package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.utils.IdGenerator;

import java.util.List;
import java.util.Optional;

/**
 * 申请服务（Application Service）
 * 处理TA申请职位、MO审核申请等核心业务逻辑。
 *
 * <p>核心功能：
 * <ul>
 *   <li>TA提交职位申请</li>
 *   <li>TA撤回申请</li>
 *   <li>MO查看申请列表</li>
 *   <li>MO录用/拒绝申请</li>
 *   <li>录用后自动创建工作量记录</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class ApplicationService {

    /** 申请数据访问层 */
    private final ApplicationRepository applicationRepository;

    /** 职位数据访问层 */
    private final JobRepository jobRepository;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 工作量记录数据访问层 */
    private final WorkloadRepository workloadRepository;

    /**
     * 构造方法，注入依赖。
     */
    public ApplicationService(ApplicationRepository applicationRepository,
                               JobRepository jobRepository,
                               UserRepository userRepository,
                               WorkloadRepository workloadRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.workloadRepository = workloadRepository;
    }

    // ==================== TA申请操作 ====================

    /**
     * TA提交职位申请。
     *
     * @param taId          TA的用户ID
     * @param jobId         职位ID
     * @param coverLetter   求职信
     * @param cvPath        简历文件路径（可选）
     * @return 创建成功的申请对象
     * @throws IllegalArgumentException 职位不存在、不开放、已申请或TA不存在时抛出
     */
    public Application applyForJob(String taId, String jobId,
                                   String coverLetter, String cvPath) {
        // 验证TA存在
        Optional<TA> taOpt = userRepository.findTAById(taId);
        if (!taOpt.isPresent()) {
            throw new IllegalArgumentException("TA not found: " + taId);
        }

        // 验证职位存在且开放
        Optional<JobPosting> jobOpt = jobRepository.findById(jobId);
        if (!jobOpt.isPresent()) {
            throw new IllegalArgumentException("Job not found: " + jobId);
        }
        JobPosting job = jobOpt.get();
        if (!job.isOpen()) {
            throw new IllegalArgumentException(
                    "Job is not open for applications. Status: " + job.getStatus());
        }

        // 防止重复申请
        if (applicationRepository.existsByTaIdAndJobId(taId, jobId)) {
            throw new IllegalArgumentException("You have already applied for this job");
        }

        TA ta = taOpt.get();
        String appId = IdGenerator.generateApplicationId();

        Application application = new Application(
                appId, taId, ta.getFullName(), jobId,
                job.getTitle(), job.getMoId(), coverLetter
        );
        if (cvPath != null && !cvPath.isEmpty()) {
            application.setCvPath(cvPath);
        }

        applicationRepository.save(application);

        // 更新职位的申请ID列表
        job.addApplication(appId);
        jobRepository.save(job);

        return application;
    }

    /**
     * TA撤回申请（只能撤回PENDING状态的申请）。
     *
     * @param taId          TA的用户ID
     * @param applicationId 申请ID
     * @throws IllegalArgumentException 申请不存在、无权限或状态不允许时抛出
     */
    public void withdrawApplication(String taId, String applicationId) {
        Application app = getApplicationOrThrow(applicationId);

        // 权限验证
        if (!taId.equals(app.getTaId())) {
            throw new IllegalArgumentException("You don't have permission to withdraw this application");
        }

        // 状态验证
        if (!Application.STATUS_PENDING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot withdraw application in status: " + app.getStatus());
        }

        app.withdraw();
        applicationRepository.save(app);
    }

    // ==================== MO审核操作 ====================

    /**
     * MO获取某职位的所有申请列表。
     *
     * @param moId  MO的用户ID（权限验证）
     * @param jobId 职位ID
     * @return 申请列表
     * @throws IllegalArgumentException 职位不存在或无权限时抛出
     */
    public List<Application> getApplicationsForJob(String moId, String jobId) {
        JobPosting job = getJobOrThrow(jobId);
        if (!moId.equals(job.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to view applications for this job");
        }
        return applicationRepository.findByJobId(jobId);
    }

    /**
     * MO录用申请人（接受申请）。
     * 录用后自动创建工作量记录，并更新职位剩余名额。
     *
     * @param moId          MO的用户ID
     * @param applicationId 申请ID
     * @param feedback      录用反馈信息（可选）
     * @return 更新后的申请对象
     * @throws IllegalArgumentException 申请不存在、无权限、状态不允许或名额已满时抛出
     */
    public Application acceptApplication(String moId, String applicationId, String feedback) {
        Application app = getApplicationOrThrow(applicationId);

        // 权限验证
        if (!moId.equals(app.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to accept this application");
        }

        // 状态验证
        if (!Application.STATUS_PENDING.equals(app.getStatus())
                && !Application.STATUS_REVIEWING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot accept application in status: " + app.getStatus());
        }

        // 检查职位名额
        JobPosting job = getJobOrThrow(app.getJobId());
        long acceptedCount = applicationRepository.findAcceptedByJobId(app.getJobId()).size();
        if (acceptedCount >= job.getVacancies()) {
            throw new IllegalArgumentException(
                    "No more vacancies available for this job");
        }

        // 更新申请状态
        app.accept(feedback);
        applicationRepository.save(app);

        // 自动创建工作量记录
        createWorkloadRecord(app, job);

        return app;
    }

    /**
     * MO拒绝申请。
     *
     * @param moId          MO的用户ID
     * @param applicationId 申请ID
     * @param feedback      拒绝原因（可选）
     * @return 更新后的申请对象
     */
    public Application rejectApplication(String moId, String applicationId, String feedback) {
        Application app = getApplicationOrThrow(applicationId);

        if (!moId.equals(app.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to reject this application");
        }

        if (!Application.STATUS_PENDING.equals(app.getStatus())
                && !Application.STATUS_REVIEWING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot reject application in status: " + app.getStatus());
        }

        app.reject(feedback);
        applicationRepository.save(app);
        return app;
    }

    /**
     * MO将申请标记为审核中。
     *
     * @param moId          MO的用户ID
     * @param applicationId 申请ID
     * @return 更新后的申请对象
     */
    public Application markAsReviewing(String moId, String applicationId) {
        Application app = getApplicationOrThrow(applicationId);

        if (!moId.equals(app.getMoId())) {
            throw new IllegalArgumentException("Permission denied");
        }

        if (!Application.STATUS_PENDING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Can only mark PENDING applications as reviewing");
        }

        app.setStatus(Application.STATUS_REVIEWING);
        applicationRepository.save(app);
        return app;
    }

    // ==================== 查询操作 ====================

    /**
     * TA查看自己的所有申请。
     *
     * @param taId TA的用户ID
     * @return 该TA的申请列表
     */
    public List<Application> getApplicationsByTA(String taId) {
        return applicationRepository.findByTaId(taId);
    }

    /**
     * 根据申请ID获取申请详情。
     *
     * @param applicationId 申请ID
     * @return 包含申请的Optional
     */
    public Optional<Application> getApplicationById(String applicationId) {
        return applicationRepository.findById(applicationId);
    }

    /**
     * 获取指定职位的所有申请（管理员用，无权限限制）。
     *
     * @param jobId 职位ID
     * @return 该职位的申请列表
     */
    public List<Application> getApplicationsByJob(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    /**
     * 获取所有申请（管理员用）。
     *
     * @return 所有申请列表
     */
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据申请ID获取申请，不存在则抛出异常。
     */
    private Application getApplicationOrThrow(String applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Application not found: " + applicationId));
    }

    /**
     * 根据职位ID获取职位，不存在则抛出异常。
     */
    private JobPosting getJobOrThrow(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
    }

    /**
     * 录用申请后自动创建工作量记录。
     *
     * @param app 已录用的申请
     * @param job 对应的职位
     */
    private void createWorkloadRecord(Application app, JobPosting job) {
        // 获取TA信息
        Optional<TA> taOpt = userRepository.findTAById(app.getTaId());
        if (!taOpt.isPresent()) return;

        TA ta = taOpt.get();
        String recordId = IdGenerator.generateWorkloadId();

        WorkloadRecord record = new WorkloadRecord(
                recordId,
                app.getTaId(),
                ta.getFullName(),
                app.getJobId(),
                job.getTitle(),
                job.getModuleCode(),
                app.getMoId(),
                job.getHoursPerWeek(),
                job.getSemester(),
                app.getApplicationId()
        );

        workloadRepository.save(record);
    }
}
