package com.ebu6304.recruitment.services;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.repositories.ApplicationRepository;
import com.ebu6304.recruitment.repositories.JobRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.repositories.WorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

//测试TA 主动撤回申请时触发 MO 通知的交互测试。

class ApplicationServiceWithdrawalTest {

    // 声明需要 Mock (模拟) 的依赖项
    private ApplicationRepository applicationRepository;
    private NotificationService notificationService;
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        // 使用 Mockito 创建这些接口或类的虚拟对象，以便我们在测试中控制它们的行为
        applicationRepository = mock(ApplicationRepository.class);
        notificationService = mock(NotificationService.class);
        JobRepository jobRepository = mock(JobRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        WorkloadRepository workloadRepository = mock(WorkloadRepository.class);

        // 实例化我们要测试的目标服务 (ApplicationService)
        applicationService = new ApplicationService(
                applicationRepository, jobRepository, userRepository, workloadRepository);
        // 手动注入模拟的 NotificationService，以便追踪是否发送了通知
        applicationService.setNotificationService(notificationService);
    }

    /**
     * 测试场景：TA成功撤回一条处于 PENDING (待处理) 状态的申请。
     * 预期结果：申请状态变更为 WITHDRAWN，保存到数据库，并调用 notificationService 发送通知给 MO。
     */
    @Test
    void withdrawApplication_ShouldChangeStatusAndTriggerMONotification() {
        // --- Arrange (准备阶段) ---
        String taId = "TA_001";
        // 创建一个模拟的申请记录，默认状态应该是 PENDING
        Application app = new Application("APP_123", taId, "Alice", "JOB_1", "Lab TA", "MO_001", "Cover");
        // 配置模拟数据库：当按照 "APP_123" 查询时，返回我们刚刚创建的 app 对象
        when(applicationRepository.findById("APP_123")).thenReturn(Optional.of(app));

        // --- Act (执行阶段) ---
        // 调用我们想要测试的方法
        applicationService.withdrawApplication(taId, "APP_123");

        // --- Assert (断言阶段) ---
        // 1. 验证申请对象本身的状态是否被正确修改
        assertEquals(Application.STATUS_WITHDRAWN, app.getStatus());
        // 2. 验证是否调用了 applicationRepository 的 save 方法将修改保存到数据库 (执行了1次)
        verify(applicationRepository, times(1)).save(app);
        // 3. 核心验证：验证确实调用了 notificationService 的对应方法，为该申请生成了给 MO 的通知
        verify(notificationService, times(1)).createApplicationWithdrawnNotification(app);
    }

    /**
     * 测试场景：尝试撤回别人的申请。
     * 预期结果：抛出 IllegalArgumentException 异常，并且不会触发任何通知。
     */
    @Test
    void withdrawApplication_ShouldThrowException_WhenNotOwner() {
        // --- Arrange ---
        Application app = new Application("APP_123", "TA_001", "Alice", "JOB_1", "Lab TA", "MO_001", "Cover");
        when(applicationRepository.findById("APP_123")).thenReturn(Optional.of(app));

        // --- Act & Assert ---
        // 使用一个不同的 taId ("TA_OTHER") 尝试撤回 "TA_001" 的申请
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            applicationService.withdrawApplication("TA_OTHER", "APP_123");
        });

        // 验证报错信息是否符合预期
        assertEquals("You don't have permission to withdraw this application", exception.getMessage());
        // 验证安全防御：确保没有错误地发出通知 (never 表示 0 次调用)
        verify(notificationService, never()).createApplicationWithdrawnNotification(any());
    }

    /**
     * 测试场景：尝试撤回一个已经被接受 (ACCEPTED) 的申请。
     * 预期结果：抛出 IllegalArgumentException 异常，提示不能撤回，且不触发通知。
     */
    @Test
    void withdrawApplication_ShouldThrowException_WhenNotPending() {
        // --- Arrange ---
        Application app = new Application("APP_123", "TA_001", "Alice", "JOB_1", "Lab TA", "MO_001", "Cover");
        // 模拟该申请已经被接受，状态不再是 PENDING
        app.setStatus(Application.STATUS_ACCEPTED);
        when(applicationRepository.findById("APP_123")).thenReturn(Optional.of(app));

        // --- Act & Assert ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            applicationService.withdrawApplication("TA_001", "APP_123");
        });

        // 验证是否拦截了非法状态的撤回请求
        assertEquals("Cannot withdraw application in status: ACCEPTED", exception.getMessage());
        // 验证没有发送通知
        verify(notificationService, never()).createApplicationWithdrawnNotification(any());
    }
}