<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Reset Admin Password - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; font-family: Arial, sans-serif; }
        body { margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f4f7fb; color: #0f172a; }
        .box { width: 420px; background: white; border: 1px solid #e2e8f0; border-radius: 14px; padding: 28px; box-shadow: 0 12px 28px rgba(15,23,42,.08); }
        h2 { margin: 0 0 8px; }
        p { color: #64748b; font-size: 14px; }
        label { display: block; margin-top: 16px; margin-bottom: 6px; font-size: 12px; font-weight: 700; color: #64748b; text-transform: uppercase; }
        input { width: 100%; padding: 11px 12px; border: 1px solid #cbd5e1; border-radius: 10px; }
        button, .link-btn { display: inline-block; width: 100%; margin-top: 18px; border: none; border-radius: 10px; padding: 11px 14px; background: #2563eb; color: white; text-align: center; font-weight: 700; text-decoration: none; cursor: pointer; }
        .ghost { background: #e2e8f0; color: #0f172a; }
        .alert { padding: 12px 14px; border-radius: 10px; margin: 16px 0; font-weight: 700; font-size: 14px; }
        .success { background: #dcfce7; color: #166534; }
        .error { background: #fee2e2; color: #991b1b; }
        .dev-link { word-break: break-all; font-size: 12px; color: #2563eb; }
    </style>
</head>
<body>
<div class="box">
    <h2>Reset Admin Password</h2>
    <p>Use the admin email address to receive a reset link.</p>

    <% if (request.getAttribute("message") != null) { %>
        <div class="alert success"><%= request.getAttribute("message") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% String token = request.getAttribute("token") == null ? "" : String.valueOf(request.getAttribute("token")); %>
    <% if (token.isEmpty()) { %>
        <form method="post" action="${pageContext.request.contextPath}/password-reset">
            <input type="hidden" name="action" value="request">
            <label>Admin Email</label>
            <input type="email" name="email" required>
            <button type="submit">Send Reset Email</button>
        </form>
        <% if (request.getAttribute("devResetLink") != null) { %>
            <p class="dev-link">Development reset link: <a href="<%= request.getAttribute("devResetLink") %>"><%= request.getAttribute("devResetLink") %></a></p>
        <% } %>
    <% } else { %>
        <form method="post" action="${pageContext.request.contextPath}/password-reset">
            <input type="hidden" name="action" value="complete">
            <input type="hidden" name="token" value="<%= token %>">
            <label>New Password</label>
            <input type="password" name="password" required minlength="6">
            <label>Confirm Password</label>
            <input type="password" name="confirmPassword" required minlength="6">
            <button type="submit">Set New Password</button>
        </form>
    <% } %>
    <a class="link-btn ghost" href="${pageContext.request.contextPath}/login">Back to Login</a>
</div>
</body>
</html>
