package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.CvFileData;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.utils.IdGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private static final String DEFAULT_CV_UPLOAD_DIR = "data/uploads/cv";

    /** 申请数据访问层 */
    private final ApplicationRepository applicationRepository;

    /** 职位数据访问层 */
    private final JobRepository jobRepository;

    /** 用户数据访问层 */
    private final UserRepository userRepository;

    /** 工作量记录数据访问层 */
    private final WorkloadRepository workloadRepository;
    private final String cvUploadDir;

    /**
     * 构造方法，注入依赖。
     */
    public ApplicationService(ApplicationRepository applicationRepository,
                               JobRepository jobRepository,
                               UserRepository userRepository,
                               WorkloadRepository workloadRepository) {
        this(applicationRepository, jobRepository, userRepository, workloadRepository, DEFAULT_CV_UPLOAD_DIR);
    }

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository,
                              WorkloadRepository workloadRepository,
                              String cvUploadDir) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.workloadRepository = workloadRepository;
        this.cvUploadDir = cvUploadDir;
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
            validateCvFileExtension(cvPath);
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
        if (!Application.STATUS_PENDING.equals(app.getStatus())) {
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

        // 更新职位的已录用人数
        job.incrementFilledCount();
        jobRepository.save(job);

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

        if (!Application.STATUS_PENDING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot reject application in status: " + app.getStatus());
        }

        app.reject(feedback);
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
     * MO查看自己职位的所有申请。
     *
     * @param moId MO的用户ID
     * @return 该MO职位的申请列表
     */
    public List<Application> getApplicationsByMo(String moId) {
        return applicationRepository.findAll().stream()
                .filter(app -> moId.equals(app.getMoId()))
                .collect(java.util.stream.Collectors.toList());
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
     * TA 上传 CV 文件并落盘，返回保存路径（相对路径）。
     */
    public String uploadCv(String taId, String originalFileName, byte[] content) {
        if (userRepository.findTAById(taId).isEmpty()) {
            throw new IllegalArgumentException("TA not found: " + taId);
        }
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("CV file is empty");
        }
        validateCvFileExtension(originalFileName);

        String extension = getFileExtension(originalFileName);
        String safeFileName = taId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + "." + extension;
        Path dirPath = Paths.get(cvUploadDir);
        Path target = dirPath.resolve(safeFileName).normalize();
        try {
            Files.createDirectories(dirPath);
            Files.write(target, content);
            return normalizeRelativePath(target);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save CV file: " + e.getMessage(), e);
        }
    }

    /**
     * 绑定申请与CV路径，只允许申请人本人操作。
     */
    public Application attachCvToApplication(String taId, String applicationId, String cvPath) {
        validateCvFileExtension(cvPath);
        Application app = getApplicationOrThrow(applicationId);
        if (!taId.equals(app.getTaId())) {
            throw new IllegalArgumentException("You don't have permission to update this application");
        }
        app.setCvPath(cvPath);
        applicationRepository.save(app);
        return app;
    }

    /**
     * 仅岗位发布者MO可读取申请关联CV。
     */
    public CvFileData getCvForApplicationAsMo(String moId, String applicationId) {
        Application app = getApplicationOrThrow(applicationId);
        if (!moId.equals(app.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to view this CV");
        }
        if (app.getCvPath() == null || app.getCvPath().isBlank()) {
            throw new IllegalArgumentException("No CV uploaded for this application");
        }

        Path path = Paths.get(app.getCvPath()).normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("CV file not found: " + app.getCvPath());
        }
        String fileName = path.getFileName().toString();
        validateCvFileExtension(fileName);
        try {
            byte[] content = Files.readAllBytes(path);
            return new CvFileData(fileName, inferContentType(fileName), content);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read CV file: " + e.getMessage(), e);
        }
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

    private void validateCvFileExtension(String fileNameOrPath) {
        String extension = getFileExtension(fileNameOrPath);
        if (!"pdf".equals(extension) && !"doc".equals(extension)) {
            throw new IllegalArgumentException("Invalid CV file type. Only .pdf or .doc is allowed");
        }
    }

    private String getFileExtension(String fileNameOrPath) {
        if (fileNameOrPath == null || fileNameOrPath.isBlank()) {
            throw new IllegalArgumentException("CV file name is required");
        }
        String name = Paths.get(fileNameOrPath).getFileName().toString().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            throw new IllegalArgumentException("Invalid CV file type. Only .pdf or .doc is allowed");
        }
        return name.substring(dot + 1);
    }

    private String normalizeRelativePath(Path target) {
        return target.toString().replace('\\', '/');
    }

    private String inferContentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/msword";
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
