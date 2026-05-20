package com.ebu6304.recruitment.web;

import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.NotificationRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadConfigRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.AuthService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.NotificationService;
import com.ebu6304.recruitment.services.WorkloadService;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.File;

public class AppInitializer implements ServletContextListener {

    private static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        servletContext = ctx;

        String webInfPath = ctx.getRealPath("/WEB-INF");
        if (webInfPath != null) {
            File dataDir = new File(webInfPath, "data");
            if (dataDir.exists()) {
                JsonFileUtil.setBaseDir(webInfPath);
                ctx.log("[AppInitializer] Using data directory: " + dataDir.getAbsolutePath());
            } else {
                ctx.log("[AppInitializer] WEB-INF/data not found, using working directory: "
                        + new File("data").getAbsolutePath());
            }
        }

        UserRepository userRepository = new UserRepository();
        JobRepository jobRepository = new JobRepository();
        ApplicationRepository applicationRepository = new ApplicationRepository();
        WorkloadRepository workloadRepository = new WorkloadRepository();
        WorkloadConfigRepository workloadConfigRepository = new WorkloadConfigRepository();
        AuditLogRepository auditLogRepository = new AuditLogRepository();
        NotificationRepository notificationRepository = new NotificationRepository();


        AuthService authService = new AuthService(userRepository);
        JobService jobService = new JobService(jobRepository, userRepository);

        NotificationService notificationService = new NotificationService(notificationRepository);
        ApplicationService applicationService = new ApplicationService(
                applicationRepository, jobRepository, userRepository, workloadRepository);

        applicationService.setNotificationService(notificationService);

        WorkloadService workloadService = new WorkloadService(
                workloadRepository, userRepository, workloadConfigRepository);

        ctx.setAttribute("authService", authService);
        ctx.setAttribute("jobService", jobService);
        ctx.setAttribute("applicationService", applicationService);
        ctx.setAttribute("workloadService", workloadService);
        ctx.setAttribute("notificationService", notificationService);
        ctx.setAttribute("userRepository", userRepository);
        ctx.setAttribute("jobRepository", jobRepository);
        ctx.setAttribute("applicationRepository", applicationRepository);
        ctx.setAttribute("auditLogRepository", auditLogRepository);
        ctx.setAttribute("workloadConfigRepository", workloadConfigRepository);
        ctx.setAttribute("notificationRepository", notificationRepository);

        ctx.log("[AppInitializer] TA Recruitment System initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[AppInitializer] TA Recruitment System shutting down.");
        servletContext = null;
    }

    public static JobService getJobService() {
        return (JobService) servletContext.getAttribute("jobService");
    }

    public static ApplicationService getApplicationService() {
        return (ApplicationService) servletContext.getAttribute("applicationService");
    }

    public static AuthService getAuthService() {
        return (AuthService) servletContext.getAttribute("authService");
    }

    public static WorkloadService getWorkloadService() {
        return (WorkloadService) servletContext.getAttribute("workloadService");
    }

    public static NotificationService getNotificationService() {
        return (NotificationService) servletContext.getAttribute("notificationService");
    }

    public static AuditLogRepository getAuditLogRepository() {
        return (AuditLogRepository) servletContext.getAttribute("auditLogRepository");
    }
}