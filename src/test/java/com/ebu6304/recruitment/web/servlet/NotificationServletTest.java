package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.services.NotificationService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationServletTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private NotificationServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new NotificationServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        when(context.getAttribute("notificationService")).thenReturn(notificationService);
    }

    @Test
    void postMarkAsReadReturnsSuccessJson() throws Exception {
        StringWriter writer = new StringWriter();
        when(request.getParameter("notificationId")).thenReturn("NOT001");
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(request, response);

        verify(notificationService).markAsRead("NOT001");
        verify(response).setContentType("application/json;charset=UTF-8");
        assertTrue(writer.toString().contains("\"success\":true"));
    }

    @Test
    void postMissingNotificationIdReturnsFailureJson() throws Exception {
        StringWriter writer = new StringWriter();
        when(request.getParameter("notificationId")).thenReturn("  ");
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(request, response);

        verify(notificationService, never()).markAsRead(anyString());
        assertTrue(writer.toString().contains("\"success\":false"));
        assertTrue(writer.toString().contains("missing"));
    }

    @Test
    void postMarkAsReadHandlesServiceException() throws Exception {
        StringWriter writer = new StringWriter();
        when(request.getParameter("notificationId")).thenReturn("NOT404");
        doThrow(new RuntimeException("not found")).when(notificationService).markAsRead("NOT404");
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(request, response);

        assertTrue(writer.toString().contains("\"success\":false"));
        assertTrue(writer.toString().contains("not found"));
    }
}
