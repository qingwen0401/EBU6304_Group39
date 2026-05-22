package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.NotificationService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
//测试从 unread 变为 read的逻辑。

class NotificationServletTest {

    private NotificationServlet servlet;
    // 模拟 HTTP 请求和响应对象
    private HttpServletRequest request;
    private HttpServletResponse response;
    // 模拟 Service 层
    private NotificationService notificationService;
    // 用于捕获 Servlet 打印给前端的 JSON 输出
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        notificationService = mock(NotificationService.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        // 重写 Servlet 的 getServletContext 方法，注入我们的 Mock Service
        servlet = new NotificationServlet() {
            @Override
            public ServletContext getServletContext() {
                ServletContext context = mock(ServletContext.class);
                when(context.getAttribute("notificationService")).thenReturn(notificationService);
                return context;
            }
        };

        // 拦截 response.getWriter()，将原本要发给浏览器的内容写入到我们创建的 stringWriter 中，方便后续断言检查
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    /**
     * 测试场景：前端传来了一个有效的 notificationId。
     * 预期结果：成功调用 service 标记已读，并向前端返回表示成功的 JSON 字符串。
     */
    @Test
    void doPost_WithValidId_ShouldMarkAsReadAndReturnSuccessJson() throws Exception {
        // --- Arrange ---
        // 模拟前端发起了表单或 AJAX 请求，传参 notificationId = "NOT_12345"
        when(request.getParameter("notificationId")).thenReturn("NOT_12345");

        // --- Act ---
        // 模拟 Servlet 接收并处理 POST 请求
        servlet.doPost(request, response);

        // --- Assert ---
        // 1. 验证确实将前端传来的 ID 传递给了业务层进行标记
        verify(notificationService, times(1)).markAsRead("NOT_12345");
        // 2. 验证响应头设置正确 (指明返回的是 JSON)
        verify(response).setContentType("application/json;charset=UTF-8");

        // 3. 将拦截到的输出流转换为字符串，检查 JSON 内容是否包含成功的标志
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":true"));
        assertTrue(jsonResponse.contains("Notification marked as read"));
    }

    /**
     * 测试场景：前端传来的 ID 为空（或缺失）。
     * 预期结果：不调用 service 层，直接向前端返回包含失败原因的 JSON。
     */
    @Test
    void doPost_WithMissingId_ShouldReturnErrorJson() throws Exception {
        // --- Arrange ---
        // 模拟前端传了一个空白的参数
        when(request.getParameter("notificationId")).thenReturn("   ");

        // --- Act ---
        servlet.doPost(request, response);

        // --- Assert ---
        // 验证防呆逻辑：因为 ID 为空，绝对不能去调用底层的 markAsRead
        verify(notificationService, never()).markAsRead(anyString());

        // 验证返回的 JSON 表示失败，并指明缺失 ID
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":false"));
        assertTrue(jsonResponse.contains("Notification ID is missing"));
    }

    /**
     * 测试场景：执行标记时，底层数据库或 Service 突然报错。
     * 预期结果：Servlet 能够优雅地捕获异常，不会崩溃 (500错误)，而是给前端返回一个包含报错信息的失败 JSON。
     */
    @Test
    void doPost_WhenServiceThrowsException_ShouldReturnErrorJson() throws Exception {
        // --- Arrange ---
        when(request.getParameter("notificationId")).thenReturn("NOT_999");
        // 模拟调用 service 时，抛出一个运行时异常 (例如数据库断开连接)
        doThrow(new RuntimeException("Database down")).when(notificationService).markAsRead("NOT_999");

        // --- Act ---
        servlet.doPost(request, response);

        // --- Assert ---
        // 验证捕获到了异常并正常转化为失败的 JSON 响应
        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("\"success\":false"));
        assertTrue(jsonResponse.contains("Failed to mark notification"));
        assertTrue(jsonResponse.contains("Database down")); // 应当把底层异常信息包裹在消息中
    }
}