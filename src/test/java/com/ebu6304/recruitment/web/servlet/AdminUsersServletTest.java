package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.AuditLogEntry;
import com.ebu6304.recruitment.models.ModuleOrganiser;
import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.AuditLogRepository;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.AuthService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminUsersServletTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AdminUsersServlet servlet;
    private User admin;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminUsersServlet();
        ServletContext context = AdminServletTestSupport.initServlet(servlet);
        lenient().when(context.getAttribute("userRepository")).thenReturn(userRepository);
        lenient().when(context.getAttribute("auditLogRepository")).thenReturn(auditLogRepository);
        lenient().when(context.getAttribute("authService")).thenReturn(authService);
        admin = AdminServletTestSupport.adminUser();
    }

    @Test
    void getUsersFiltersByRoleAndActiveStatus() throws Exception {
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        TA inactiveTa = new TA("TA001", "ta.one", "hash", "ta@example.com",
                "TA One", "S001", "EECS", "CS");
        inactiveTa.setActive(false);
        ModuleOrganiser activeMo = mo("MO001", "mo.one");
        when(request.getParameter("role")).thenReturn("MO");
        when(request.getParameter("status")).thenReturn("ACTIVE");
        when(request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp"))
                .thenReturn(dispatcher);
        when(userRepository.findAllTAs()).thenReturn(List.of(inactiveTa));
        when(userRepository.findAllMOs()).thenReturn(List.of(activeMo));
        when(userRepository.findAllAdmins()).thenReturn(List.of(admin));

        servlet.doGet(request, response);

        ArgumentCaptor<List<User>> usersCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("users"), usersCaptor.capture());
        assertEquals(1, usersCaptor.getValue().size());
        assertEquals("MO001", usersCaptor.getValue().get(0).getUserId());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void createMORegistersAccountAndWritesSuccessAudit() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        ModuleOrganiser created = mo("MO002", "new.mo");
        when(request.getParameter("action")).thenReturn("create-mo");
        when(request.getParameter("username")).thenReturn("new.mo");
        when(request.getParameter("password")).thenReturn("valid123");
        when(request.getParameter("email")).thenReturn("new.mo@example.com");
        when(request.getParameter("fullName")).thenReturn("New MO");
        when(request.getParameter("department")).thenReturn("EECS");
        when(request.getParameter("moduleCode")).thenReturn("EBU6304");
        when(request.getParameter("moduleName")).thenReturn("Software Engineering");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authService.registerMO("new.mo", "valid123", "new.mo@example.com",
                "New MO", "EECS", "EBU6304", "Software Engineering")).thenReturn(created);

        servlet.doPost(request, response);

        verify(authService).registerMO("new.mo", "valid123", "new.mo@example.com",
                "New MO", "EECS", "EBU6304", "Software Engineering");
        verify(response).sendRedirect(contains("/admin/users?message=Created+MO+account+new.mo"));
        AuditLogEntry audit = captureAudit();
        assertEquals("MO_CREATED", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
        assertTrue(audit.getDetails().contains("new.mo"));
    }

    @Test
    void createMORejectsInvalidEmailBeforeRegistering() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("create-mo");
        when(request.getParameter("username")).thenReturn("bad.mo");
        when(request.getParameter("password")).thenReturn("valid123");
        when(request.getParameter("email")).thenReturn("not-an-email");
        when(request.getParameter("fullName")).thenReturn("Bad MO");
        when(request.getParameter("department")).thenReturn("EECS");
        when(request.getParameter("moduleCode")).thenReturn("EBU6304");
        when(request.getParameter("moduleName")).thenReturn("Software Engineering");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        servlet.doPost(request, response);

        verify(authService, never()).registerMO(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(response).sendRedirect(contains("/admin/users?error=A+valid+email+address"));
        assertEquals("FAILED", captureAudit().getOutcome());
    }

    @Test
    void createMOReportsPasswordOrUniqueConstraintFailuresFromAuthService() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("create-mo");
        when(request.getParameter("username")).thenReturn("existing.mo");
        when(request.getParameter("password")).thenReturn("123");
        when(request.getParameter("email")).thenReturn("existing@example.com");
        when(request.getParameter("fullName")).thenReturn("Existing MO");
        when(request.getParameter("department")).thenReturn("EECS");
        when(request.getParameter("moduleCode")).thenReturn("EBU6304");
        when(request.getParameter("moduleName")).thenReturn("Software Engineering");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authService.registerMO(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("/admin/users?error=Username+already+exists"));
        AuditLogEntry audit = captureAudit();
        assertEquals("MO_CREATED", audit.getAction());
        assertEquals("FAILED", audit.getOutcome());
        assertEquals("Username already exists", audit.getDetails());
    }

    @Test
    void createTARegistersAccountAndStoresAssignedModule() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        TA created = new TA("TA999", "new.ta", "hash", "new.ta@example.com",
                "New TA", "S999", "EECS", "CS");
        when(request.getParameter("action")).thenReturn("create-ta");
        when(request.getParameter("taUsername")).thenReturn("new.ta");
        when(request.getParameter("taPassword")).thenReturn("valid123");
        when(request.getParameter("taEmail")).thenReturn("new.ta@example.com");
        when(request.getParameter("taFullName")).thenReturn("New TA");
        when(request.getParameter("studentId")).thenReturn("S999");
        when(request.getParameter("taDepartment")).thenReturn("EECS");
        when(request.getParameter("major")).thenReturn("CS");
        when(request.getParameter("assignedModule")).thenReturn("EBU6304");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authService.registerTA("new.ta", "valid123", "new.ta@example.com",
                "New TA", "S999", "EECS", "CS")).thenReturn(created);

        servlet.doPost(request, response);

        ArgumentCaptor<TA> taCaptor = ArgumentCaptor.forClass(TA.class);
        verify(userRepository).saveTA(taCaptor.capture());
        assertEquals("EBU6304", taCaptor.getValue().getAssignedModule());
        verify(response).sendRedirect(contains("/admin/users?message=Created+TA+account+new.ta"));
        AuditLogEntry audit = captureAudit();
        assertEquals("TA_CREATED", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
    }

    @Test
    void updateTAChangesAdminManagedProfileFields() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        TA ta = new TA("TA100", "ta.update", "hash", "old@example.com",
                "Old TA", "S100", "Old Dept", "Old Major");
        when(request.getParameter("action")).thenReturn("update-ta");
        when(request.getParameter("userId")).thenReturn("TA100");
        when(request.getParameter("fullName")).thenReturn("Updated TA");
        when(request.getParameter("email")).thenReturn("updated@example.com");
        when(request.getParameter("department")).thenReturn("EECS");
        when(request.getParameter("major")).thenReturn("Software");
        when(request.getParameter("assignedModule")).thenReturn("EBU6405");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.findTAById("TA100")).thenReturn(Optional.of(ta));
        when(userRepository.emailExists("updated@example.com")).thenReturn(false);

        servlet.doPost(request, response);

        verify(userRepository).saveTA(ta);
        assertEquals("Updated TA", ta.getFullName());
        assertEquals("updated@example.com", ta.getEmail());
        assertEquals("EBU6405", ta.getAssignedModule());
        assertEquals("TA_UPDATED", captureAudit().getAction());
    }

    @Test
    void deleteTADeletesExistingAccount() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        TA ta = new TA("TA101", "ta.delete", "hash", "delete@example.com",
                "Delete TA", "S101", "EECS", "CS");
        when(request.getParameter("action")).thenReturn("delete-ta");
        when(request.getParameter("userId")).thenReturn("TA101");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.findTAById("TA101")).thenReturn(Optional.of(ta));
        when(userRepository.deleteTA("TA101")).thenReturn(true);

        servlet.doPost(request, response);

        verify(userRepository).deleteTA("TA101");
        verify(response).sendRedirect(contains("/admin/users?message=Deleted+TA+account+ta.delete"));
        assertEquals("TA_DELETED", captureAudit().getAction());
    }

    @Test
    void deleteMODeletesExistingMOAndWritesAudit() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        ModuleOrganiser existingMo = mo("MO003", "old.mo");
        when(request.getParameter("action")).thenReturn("delete-mo");
        when(request.getParameter("userId")).thenReturn("MO003");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.findMOById("MO003")).thenReturn(Optional.of(existingMo));
        when(userRepository.deleteMO("MO003")).thenReturn(true);

        servlet.doPost(request, response);

        verify(userRepository).deleteMO("MO003");
        verify(response).sendRedirect(contains("/admin/users?message=Deleted+MO+account+old.mo"));
        AuditLogEntry audit = captureAudit();
        assertEquals("MO_DELETED", audit.getAction());
        assertEquals("SUCCESS", audit.getOutcome());
    }

    @Test
    void deleteMOReportsMissingAccount() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("delete-mo");
        when(request.getParameter("userId")).thenReturn("MO404");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.findMOById("MO404")).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        verify(userRepository, never()).deleteMO(anyString());
        verify(response).sendRedirect(contains("/admin/users?error=MO+account+not+found"));
        assertEquals("FAILED", captureAudit().getOutcome());
    }

    @Test
    void deactivateCurrentAdminIsBlocked() throws Exception {
        AdminServletTestSupport.withCurrentUser(request, admin);
        when(request.getParameter("action")).thenReturn("deactivate");
        when(request.getParameter("userId")).thenReturn("ADMIN001");
        when(request.getContextPath()).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.findAllTAs()).thenReturn(List.of());
        when(userRepository.findAllMOs()).thenReturn(List.of());
        when(userRepository.findAllAdmins()).thenReturn(List.of(admin));

        servlet.doPost(request, response);

        verify(userRepository, never()).saveUser(any());
        verify(response).sendRedirect(contains("Admins+cannot+deactivate+their+own"));
        assertEquals("FAILED", captureAudit().getOutcome());
    }

    private AuditLogEntry captureAudit() {
        ArgumentCaptor<AuditLogEntry> captor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepository).save(captor.capture());
        return captor.getValue();
    }

    private static ModuleOrganiser mo(String userId, String username) {
        return new ModuleOrganiser(userId, username, "hash", username + "@example.com",
                username, "EECS", "EBU6304", "Software Engineering");
    }
}
