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
import java.util.stream.Collectors;

public class ApplicationService {
    private static final String DEFAULT_CV_UPLOAD_DIR = "data/uploads/cv";

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final WorkloadRepository workloadRepository;
    private final String cvUploadDir;
    private NotificationService notificationService;

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

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public Application applyForJob(String taId, String jobId,
                                   String coverLetter, String cvPath) {
        Optional<TA> taOpt = userRepository.findTAById(taId);
        if (!taOpt.isPresent()) {
            throw new IllegalArgumentException("TA not found: " + taId);
        }

        Optional<JobPosting> jobOpt = jobRepository.findById(jobId);
        if (!jobOpt.isPresent()) {
            throw new IllegalArgumentException("Job not found: " + jobId);
        }

        JobPosting job = jobOpt.get();
        if (!job.isOpen()) {
            throw new IllegalArgumentException(
                    "Job is not open for applications. Status: " + job.getStatus());
        }

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

        job.addApplication(appId);
        jobRepository.save(job);

        return application;
    }

    public void withdrawApplication(String taId, String applicationId) {
        Application app = getApplicationOrThrow(applicationId);

        if (!taId.equals(app.getTaId())) {
            throw new IllegalArgumentException("You don't have permission to withdraw this application");
        }

        if (!Application.STATUS_PENDING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot withdraw application in status: " + app.getStatus());
        }

        app.withdraw();
        applicationRepository.save(app);

        if (notificationService != null) {
            notificationService.createApplicationWithdrawnNotification(app);
        }
    }

    public List<Application> getApplicationsForJob(String moId, String jobId) {
        JobPosting job = getJobOrThrow(jobId);
        if (!moId.equals(job.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to view applications for this job");
        }
        return applicationRepository.findByJobId(jobId);
    }

    public Application acceptApplication(String moId, String applicationId, String feedback) {
        Application app = getApplicationOrThrow(applicationId);

        if (!moId.equals(app.getMoId())) {
            throw new IllegalArgumentException("You don't have permission to accept this application");
        }

        if (!Application.STATUS_PENDING.equals(app.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot accept application in status: " + app.getStatus());
        }

        JobPosting job = getJobOrThrow(app.getJobId());
        long acceptedCount = applicationRepository.findAcceptedByJobId(app.getJobId()).size();
        if (acceptedCount >= job.getVacancies()) {
            throw new IllegalArgumentException("No more vacancies available for this job");
        }

        app.accept(feedback);
        applicationRepository.save(app);

        job.incrementFilledCount();
        jobRepository.save(job);

        createWorkloadRecord(app, job);

        return app;
    }

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

    public List<Application> getApplicationsByTA(String taId) {
        return applicationRepository.findByTaId(taId);
    }

    public List<Application> getApplicationsByMo(String moId) {
        return applicationRepository.findAll().stream()
                .filter(app -> moId.equals(app.getMoId()))
                .collect(Collectors.toList());
    }

    public Optional<Application> getApplicationById(String applicationId) {
        return applicationRepository.findById(applicationId);
    }

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

    public List<Application> getApplicationsByJob(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    private Application getApplicationOrThrow(String applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Application not found: " + applicationId));
    }

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

    private void createWorkloadRecord(Application app, JobPosting job) {
        Optional<TA> taOpt = userRepository.findTAById(app.getTaId());
        if (!taOpt.isPresent()) {
            return;
        }

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