package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.User;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

final class AdminServletTestSupport {

    private AdminServletTestSupport() {
    }

    static ServletContext initServlet(HttpServlet servlet) throws Exception {
        ServletContext context = mock(ServletContext.class);
        ServletConfig config = mock(ServletConfig.class);
        when(config.getServletContext()).thenReturn(context);
        lenient().when(config.getServletName()).thenReturn(servlet.getClass().getSimpleName());
        servlet.init(config);
        return context;
    }

    static User adminUser() {
        return new User("ADMIN001", "admin", "hash", "admin@example.com", "ADMIN", "Admin User");
    }

    static void withCurrentUser(HttpServletRequest request, User admin) {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(admin);
    }

    static TestServletOutputStream outputStream(ByteArrayOutputStream target) {
        return new TestServletOutputStream(target);
    }

    static final class TestServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream target;

        private TestServletOutputStream(ByteArrayOutputStream target) {
            this.target = target;
        }

        @Override
        public void write(int b) throws IOException {
            target.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }

    static final class EmptyReadListener implements ReadListener {
        @Override
        public void onDataAvailable() {
        }

        @Override
        public void onAllDataRead() {
        }

        @Override
        public void onError(Throwable throwable) {
        }
    }
}
