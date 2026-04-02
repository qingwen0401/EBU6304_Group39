<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9fafb; display: flex; align-items: center; justify-content: center; min-height: 100vh; }
        .login-box {
            background: white; border: 1px solid #e2e8f0; border-radius: 8px;
            padding: 40px; width: 380px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        h2 { font-size: 22px; color: #1e293b; margin-bottom: 8px; }
        .subtitle { font-size: 13px; color: #64748b; margin-bottom: 24px; }
        .form-group { margin-bottom: 16px; }
        .form-group label { display: block; font-size: 13px; color: #374151; margin-bottom: 5px; font-weight: bold; }
        .form-group input {
            width: 100%; padding: 9px 12px; border: 1px solid #d1d5db;
            border-radius: 4px; font-size: 14px; outline: none;
        }
        .form-group input:focus { border-color: #2563eb; }
        .btn-login {
            width: 100%; padding: 10px; background: #2563eb; color: white;
            border: none; border-radius: 4px; font-size: 14px; cursor: pointer; margin-top: 8px;
        }
        .btn-login:hover { background: #1d4ed8; }
        .error-msg { background: #fee2e2; color: #dc2626; padding: 10px; border-radius: 4px; font-size: 13px; margin-bottom: 16px; }
        .success-msg { background: #dcfce7; color: #16a34a; padding: 10px; border-radius: 4px; font-size: 13px; margin-bottom: 16px; }
        .register-link { text-align: center; margin-top: 16px; font-size: 13px; color: #64748b; }
        .register-link a { color: #2563eb; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }
        .hint { font-size: 11px; color: #94a3b8; text-align: center; margin-top: 12px; }
    </style>
</head>
<body>
<div class="login-box">
    <h2>TA Recruitment System</h2>
    <p class="subtitle">Sign in to your account</p>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error-msg"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if ("true".equals(request.getParameter("registered"))) { %>
    <div class="success-msg">✓ Registration successful! Please sign in.</div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username"
                   placeholder="Enter your username" required autofocus>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                   placeholder="Enter your password" required>
        </div>
        <button type="submit" class="btn-login">Sign In</button>
    </form>

    <div class="register-link">
        Don't have an account? <a href="${pageContext.request.contextPath}/register">Register as TA</a>
    </div>
    <p class="hint">MO and Admin accounts are created by the system administrator.</p>
</div>
</body>
</html>