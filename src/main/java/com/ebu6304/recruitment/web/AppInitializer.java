package com.ebu6304.recruitment.web;

import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.AuthService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.File;

/**
 * Web应用初始化监听器
 * 在应用启动时初始化所有服务和仓库，并存储到 ServletContext 中供 Servlet 使用。
 * 同时负责配置数据文件目录。
 *
 * @author Group39
 * @version 1.0
 */
public class AppInitializer implements ServletContextListener {

    private static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        servletContext = ctx;

        // ==================== 配置数据目录 ====================
        // 优先使用 WEB-INF/data（适用于部署到独立Tomcat的WAR包）
        String webInfPath = ctx.getRealPath("/WEB-INF");
        if (webInfPath != null) {
            File dataDir = new File(webInfPath, "data");
            if (dataDir.exists()) {
                // 设置 baseDir 为 WEB-INF，这样 "data/xxx.json" 会解析到 WEB-INF/data/xxx.json
                JsonFileUtil.setBaseDir(webInfPath);
                ctx.log("[AppInitializer] Using data directory: " + dataDir.getAbsolutePath());
            } else {
                // WEB-INF/data 不存在，使用当前工作目录下的 data/（适用于 mvn cargo:run）
                ctx.log("[AppInitializer] WEB-INF/data not found, using working directory: "
                        + new File("data").getAbsolutePath());
            }
        }

        // ==================== 初始化仓库 ====================
        UserRepository userRepository = new UserRepository();
        JobRepository jobRepository = new JobRepository();
        ApplicationRepository applicationRepository = new ApplicationRepository();
        WorkloadRepository workloadRepository = new WorkloadRepository();

        // ==================== 初始化服务 ====================
        AuthService authService = new AuthService(userRepository);
        JobService jobService = new JobService(jobRepository, userRepository);
        ApplicationService applicationService = new ApplicationService(
                applicationRepository, jobRepository, userRepository, workloadRepository);
        WorkloadService workloadService = new WorkloadService(workloadRepository, userRepository);

        // ==================== 存储到 ServletContext ====================
        ctx.setAttribute("authService", authService);
        ctx.setAttribute("jobService", jobService);
        ctx.setAttribute("applicationService", applicationService);
        ctx.setAttribute("workloadService", workloadService);
        ctx.setAttribute("userRepository", userRepository);
        ctx.setAttribute("jobRepository", jobRepository);
        ctx.setAttribute("applicationRepository", applicationRepository);

        ctx.log("[AppInitializer] TA Recruitment System initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[AppInitializer] TA Recruitment System shutting down.");
        servletContext = null;
    }

    // ==================== 静态辅助方法 ====================

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
}

