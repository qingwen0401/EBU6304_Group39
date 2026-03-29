package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.utils.IdGenerator;

import java.util.List;
import java.util.Optional;

/**
 * 职位服务（Job Service）
 * 处理MO发布、更新、关闭职位等业务逻辑。
 *
 * <p>核心功能：
 * <ul>
 *   <li>MO发布新职位</li>
 *   <li>MO更新职位信息</li>
 *   <li>MO关闭/取消职位</li>
 *   <li>TA浏览开放职位</li>
 *   <li>按条件筛选职位</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class JobService {

    /** 职位数据访问层 */
    private final JobRepository jobRepository;

    /** 用户数据访问层（用于验证MO身份） */
    private final UserRepository userRepository;

    /**
     * 构造方法，注入依赖。
     *
     * @param jobRepository  职位数据访问层
     * @param userRepository 用户数据访问层
     */
    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // ==================== MO职位管理 ====================

    /**
     * MO发布新职位。
     *
     * @param moId         MO的用户ID
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
     * @return 创建成功的职位对象
     * @throws IllegalArgumentException MO不存在或参数无效时抛出
     */
    public JobPosting postJob(String moId, String moduleCode, String moduleName,
                              String title, String description,
                              List<String> requiredSkills,
                              int hoursPerWeek, int vacancies,
                              String deadline, String semester,
                              String jobType, double minGpa, double hourlyRate) {
        // 验证MO存在
        Optional<ModuleOrganiser> moOpt = userRepository.findMOById(moId);
        if (!moOpt.isPresent()) {
            throw new IllegalArgumentException("MO not found: " + moId);
        }

        // 参数验证
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job title cannot be empty");
        }
        if (hoursPerWeek <= 0 || hoursPerWeek > 40) {
            throw new IllegalArgumentException("Hours per week must be between 1 and 40");
        }
        if (vacancies <= 0) {
            throw new IllegalArgumentException("Vacancies must be at least 1");
        }

        ModuleOrganiser mo = moOpt.get();
        String jobId = IdGenerator.generateJobId();

        JobPosting job = new JobPosting(
                jobId, moId, mo.getFullName(),
                moduleCode, moduleName,
                title, description,
                hoursPerWeek, vacancies,
                deadline, semester
        );
        job.setRequiredSkills(requiredSkills);
        job.setJobType(jobType);
        job.setMinGpa(minGpa);
        job.setHourlyRate(hourlyRate);

        jobRepository.save(job);

        // 更新MO的已发布职位列表
        mo.addPostedJob(jobId);
        userRepository.saveMO(mo);

        return job;
    }

    /**
     * MO更新职位信息（仅允许更新OPEN状态的职位）。
     *
     * @param moId        MO的用户ID（用于权限验证）
     * @param jobId       职位ID
     * @param title       新标题（null表示不更新）
     * @param description 新描述（null表示不更新）
     * @param deadline    新截止时间（null表示不更新）
     * @param vacancies   新招聘名额（-1表示不更新）
     * @return 更新后的职位对象
     * @throws IllegalArgumentException 职位不存在、无权限或状态不允许时抛出
     */
    public JobPosting updateJob(String moId, String jobId,
                                String title, String description,
                                String deadline, int vacancies) {
        JobPosting job = getJobByIdOrThrow(jobId);

        // 权限验证：只有发布该职位的MO才能修改
        if (!moId.equals(job.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to update this job");
        }

        // 状态验证：只有OPEN状态可以修改
        if (!JobPosting.STATUS_OPEN.equals(job.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot update job in status: " + job.getStatus());
        }

        // 更新字段（非null才更新）
        if (title != null && !title.trim().isEmpty()) {
            job.setTitle(title.trim());
        }
        if (description != null) {
            job.setDescription(description);
        }
        if (deadline != null) {
            job.setDeadline(deadline);
        }
        if (vacancies > 0) {
            job.setVacancies(vacancies);
        }

        jobRepository.save(job);
        return job;
    }

    /**
     * MO关闭职位（停止接受申请）。
     *
     * @param moId  MO的用户ID
     * @param jobId 职位ID
     * @throws IllegalArgumentException 职位不存在或无权限时抛出
     */
    public void closeJob(String moId, String jobId) {
        JobPosting job = getJobByIdOrThrow(jobId);
        if (!moId.equals(job.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to close this job");
        }
        job.close();
        jobRepository.save(job);
    }

    /**
     * MO取消职位。
     *
     * @param moId  MO的用户ID
     * @param jobId 职位ID
     */
    public void cancelJob(String moId, String jobId) {
        JobPosting job = getJobByIdOrThrow(jobId);
        if (!moId.equals(job.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to cancel this job");
        }
        job.cancel();
        jobRepository.save(job);
    }

    /**
     * MO将职位状态改为审核中。
     *
     * @param moId  MO的用户ID
     * @param jobId 职位ID
     */
    public void startReviewing(String moId, String jobId) {
        JobPosting job = getJobByIdOrThrow(jobId);
        if (!moId.equals(job.getMoId())) {
            throw new IllegalArgumentException("Permission denied");
        }
        job.startReviewing();
        jobRepository.save(job);
    }

    // ==================== 职位查询 ====================

    /**
     * 获取所有开放职位（供TA浏览）。
     *
     * @return 开放职位列表
     */
    public List<JobPosting> getOpenJobs() {
        return jobRepository.findOpenJobs();
    }

    /**
     * 获取指定MO发布的所有职位。
     *
     * @param moId MO的用户ID
     * @return 该MO的职位列表
     */
    public List<JobPosting> getJobsByMo(String moId) {
        return jobRepository.findByMoId(moId);
    }

    /**
     * 根据职位ID获取职位详情。
     *
     * @param jobId 职位ID
     * @return 包含职位的Optional
     */
    public Optional<JobPosting> getJobById(String jobId) {
        return jobRepository.findById(jobId);
    }

    /**
     * 获取所有职位（管理员用）。
     *
     * @return 所有职位列表
     */
    public List<JobPosting> getAllJobs() {
        return jobRepository.findAll();
    }

    /**
     * 根据学期筛选职位。
     *
     * @param semester 学期
     * @return 该学期的职位列表
     */
    public List<JobPosting> getJobsBySemester(String semester) {
        return jobRepository.findBySemester(semester);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据职位ID获取职位，不存在则抛出异常。
     *
     * @param jobId 职位ID
     * @return 职位对象
     * @throws IllegalArgumentException 职位不存在时抛出
     */
    private JobPosting getJobByIdOrThrow(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
    }
}
