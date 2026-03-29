package com.ebu6304.recruitment;

import com.ebu6304.recruitment.controllers.AdminController;
import com.ebu6304.recruitment.controllers.ControllerResult;
import com.ebu6304.recruitment.controllers.MOApplicationController;
import com.ebu6304.recruitment.controllers.MOJobController;
import com.ebu6304.recruitment.controllers.TAController;
import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import com.ebu6304.recruitment.services.ApplicationService;
import com.ebu6304.recruitment.services.AuthService;
import com.ebu6304.recruitment.services.JobService;
import com.ebu6304.recruitment.services.WorkloadService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * TA招聘系统主入口（Recruitment Application Main Entry）
 * 初始化所有组件并演示核心功能。
 *
 * <p>系统架构（分层设计）：
 * <pre>
 *   Controllers（控制层）
 *       ↓
 *   Services（业务层）
 *       ↓
 *   Repositories（数据访问层）
 *       ↓
 *   JSON文件存储（data/目录）
 * </pre>
 * </p>
 *
 * <p>用户角色：
 * <ul>
 *   <li>TA（Teaching Assistant）- 助教，可浏览职位、提交申请、查看申请状态</li>
 *   <li>MO（Module Organiser）- 模块负责人，可发布职位、审核申请</li>
 *   <li>Admin - 系统管理员，可查看全局工作量报告</li>
 * </ul>
 * </p>
 *
 * @author Group39
 * @version 1.0
 */
public class RecruitmentApp {

    /**
     * 应用程序主方法。
     * 初始化系统组件，演示MO发布职位、TA申请、MO审核等核心流程。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println("=== BUPT International School TA Recruitment System ===");
        System.out.println("Initializing system components...\n");

        // ==================== 初始化数据访问层 ====================
        UserRepository userRepository = new UserRepository();
        JobRepository jobRepository = new JobRepository();
        ApplicationRepository applicationRepository = new ApplicationRepository();
        WorkloadRepository workloadRepository = new WorkloadRepository();

        // ==================== 初始化业务服务层 ====================
        AuthService authService = new AuthService(userRepository);
        JobService jobService = new JobService(jobRepository, userRepository);
        ApplicationService applicationService = new ApplicationService(
                applicationRepository, jobRepository, userRepository, workloadRepository);
        WorkloadService workloadService = new WorkloadService(workloadRepository, userRepository);

        // ==================== 初始化控制层 ====================
        MOJobController moJobController = new MOJobController(jobService);
        MOApplicationController moAppController = new MOApplicationController(
                applicationService, workloadService);
        TAController taController = new TAController(
                jobService, applicationService, workloadService);
        AdminController adminController = new AdminController(
                jobService, applicationService, workloadService);

        System.out.println("System initialized successfully!\n");

        // ==================== 演示核心流程 ====================
        demonstrateCoreWorkflow(authService, moJobController, moAppController,
                taController, adminController);
    }

    /**
     * 演示系统核心工作流程：
     * 1. MO注册并发布职位
     * 2. TA注册并申请职位
     * 3. MO审核申请（录用/拒绝）
     * 4. 管理员查看工作量报告
     */
    private static void demonstrateCoreWorkflow(
            AuthService authService,
            MOJobController moJobController,
            MOApplicationController moAppController,
            TAController taController,
            AdminController adminController) {

        // ==================== Step 1: 注册MO ====================
        System.out.println("--- Step 1: Register MO ---");
        String moId = null;
        try {
            ModuleOrganiser mo = authService.registerMO(
                    "dr.smith",          // username
                    "password123",       // password
                    "smith@bupt.edu.cn", // email
                    "Dr. Smith",         // fullName
                    "CS Department",     // department
                    "EBU6304",           // moduleCode
                    "Software Engineering" // moduleName
            );
            moId = mo.getUserId();
            System.out.println("[SUCCESS] MO registered: " + mo.getUsername()
                    + " (ID: " + moId + ")");
        } catch (Exception e) {
            System.out.println("[FAILED] MO registration: " + e.getMessage());
            // 尝试登录已存在的账户
            try {
                String token = authService.login("dr.smith", "password123");
                moId = authService.getUserByToken(token).get().getUserId();
                System.out.println("[INFO] MO already exists, logged in: " + moId);
            } catch (Exception ex) {
                System.out.println("[ERROR] Cannot proceed without MO: " + ex.getMessage());
                return;
            }
        }

        // ==================== Step 2: 注册TA ====================
        System.out.println("\n--- Step 2: Register TA ---");
        String taId = null;
        try {
            TA ta = authService.registerTA(
                    "alice.wang",                  // username
                    "password456",                 // password
                    "alice@student.bupt.edu.cn",   // email
                    "Alice Wang",                  // fullName
                    "2021001",                     // studentId
                    "CS Department",               // department
                    "Software Engineering"         // major
            );
            taId = ta.getUserId();
            System.out.println("[SUCCESS] TA registered: " + ta.getUsername()
                    + " (ID: " + taId + ")");
        } catch (Exception e) {
            System.out.println("[FAILED] TA registration: " + e.getMessage());
            try {
                String token = authService.login("alice.wang", "password456");
                taId = authService.getUserByToken(token).get().getUserId();
                System.out.println("[INFO] TA already exists, logged in: " + taId);
            } catch (Exception ex) {
                System.out.println("[ERROR] Cannot proceed without TA: " + ex.getMessage());
                return;
            }
        }

        // ==================== Step 3: MO发布职位 ====================
        System.out.println("\n--- Step 3: MO Posts a Job ---");
        List<String> skills = Arrays.asList("Java", "OOP", "Git");
        ControllerResult<JobPosting> jobResult = moJobController.postJob(
                moId, "EBU6304", "Software Engineering",
                "Lab TA for EBU6304", "Assist students in lab sessions",
                skills, 8, 2, "2026-04-30", "2026 Spring",
                "LAB_TA", 3.5, 15.0);
        printResult(jobResult);

        if (jobResult.isSuccess()) {
            String jobId = jobResult.getData().getJobId();

            // ==================== Step 4: TA浏览职位 ====================
            System.out.println("\n--- Step 4: TA Browses Open Jobs ---");
            ControllerResult<List<JobPosting>> browseResult = taController.browseOpenJobs();
            printResult(browseResult);

            // ==================== Step 5: TA申请职位 ====================
            System.out.println("\n--- Step 5: TA Applies for Job ---");
            ControllerResult<Application> applyResult = taController.applyForJob(
                    taId, jobId,
                    "I am passionate about software engineering and have strong Java skills.",
                    null);
            printResult(applyResult);

            if (applyResult.isSuccess()) {
                String appId = applyResult.getData().getApplicationId();

                // ==================== Step 6: MO查看申请 ====================
                System.out.println("\n--- Step 6: MO Reviews Applications ---");
                ControllerResult<List<Application>> appsResult =
                        moAppController.getApplicationsForJob(moId, jobId);
                printResult(appsResult);

                // ==================== Step 7: MO录用申请 ====================
                System.out.println("\n--- Step 7: MO Accepts Application ---");
                ControllerResult<Application> acceptResult = moAppController.acceptApplication(
                        moId, appId, "Great candidate!", "2026 Spring", 8);
                printResult(acceptResult);

                // ==================== Step 8: TA查看申请状态 ====================
                System.out.println("\n--- Step 8: TA Checks Application Status ---");
                ControllerResult<List<Application>> myAppsResult =
                        taController.getMyApplications(taId);
                printResult(myAppsResult);

                // ==================== Step 9: 管理员查看工作量报告 ====================
                System.out.println("\n--- Step 9: Admin Views Workload Report ---");
                ControllerResult<List<Map<String, Object>>> reportResult =
                        adminController.getWorkloadReport("2026 Spring");
                printResult(reportResult);

                // ==================== Step 10: 管理员查看系统统计 ====================
                System.out.println("\n--- Step 10: Admin Views System Stats ---");
                ControllerResult<Map<String, Object>> statsResult =
                        adminController.getSystemStats();
                printResult(statsResult);
            }
        }

        System.out.println("\n=== Demo Complete ===");
    }

    /**
     * 打印控制器操作结果。
     *
     * @param result 控制器结果
     */
    private static void printResult(ControllerResult<?> result) {
        if (result.isSuccess()) {
            System.out.println("[SUCCESS] " + result.getMessage());
            if (result.getData() != null) {
                Object data = result.getData();
                if (data instanceof List) {
                    List<?> list = (List<?>) data;
                    System.out.println("  Count: " + list.size());
                    // 只打印前3条，避免输出过多
                    int limit = Math.min(list.size(), 3);
                    for (int i = 0; i < limit; i++) {
                        System.out.println("  [" + i + "] " + list.get(i));
                    }
                    if (list.size() > 3) {
                        System.out.println("  ... and " + (list.size() - 3) + " more");
                    }
                } else {
                    System.out.println("  Data: " + data);
                }
            }
        } else {
            System.out.println("[FAILED] " + result.getMessage());
        }
    }
}
