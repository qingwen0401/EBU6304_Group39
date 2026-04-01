<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9fafb; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; }
        .register-box {
            background: white; border: 1px solid #e2e8f0; border-radius: 8px;
            padding: 40px; width: 480px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        h2 { font-size: 22px; color: #1e293b; margin-bottom: 8px; }
        .subtitle { font-size: 13px; color: #64748b; margin-bottom: 24px; }
        .form-group { margin-bottom: 14px; }
        .form-group label { display: block; font-size: 13px; color: #374151; margin-bottom: 5px; font-weight: bold; }
        .form-group input {
            width: 100%; padding: 9px 12px; border: 1px solid #d1d5db;
            border-radius: 4px; font-size: 14px; outline: none;
        }
        .form-group input:focus { border-color: #2563eb; }
        .required { color: #dc2626; }
        .btn-register {
            width: 100%; padding: 10px; background: #2563eb; color: white;
            border: none; border-radius: 4px; font-size: 14px; cursor: pointer; margin-top: 8px;
        }
        .btn-register:hover { background: #1d4ed8; }
        .error-msg { background: #fee2e2; color: #dc2626; padding: 10px; border-radius: 4px; font-size: 13px; margin-bottom: 16px; }
        .login-link { text-align: center; margin-top: 16px; font-size: 13px; color: #64748b; }
        .login-link a { color: #2563eb; text-decoration: none; }
        .login-link a:hover { text-decoration: underline; }
        .row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    </style>
</head>
<body>
<div class="register-box">
    <h2>Create TA Account</h2>
    <p class="subtitle">Register to apply for TA positions</p>

    <% if (request.getAttribute("error") != null) { %>
    <div class="error-msg"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <div class="row">
            <div class="form-group">
                <label>Username <span class="required">*</span></label>
                <input type="text" name="username" value="${username != null ? username : ''}" required>
            </div>
            <div class="form-group">
                <label>Password <span class="required">*</span></label>
                <input type="password" name="password" required>
            </div>
        </div>
        <div class="form-group">
            <label>Full Name <span class="required">*</span></label>
            <input type="text" name="fullName" value="${fullName != null ? fullName : ''}" required>
        </div>
        <div class="form-group">
            <label>University Email <span class="required">*</span></label>
            <input type="email" name="email" value="${email != null ? email : ''}" placeholder="user@university.ac.uk" required>
        </div>
        <div class="row">
            <div class="form-group">
                <label>Student ID <span class="required">*</span></label>
                <input type="text" name="studentId" value="${studentId != null ? studentId : ''}" required>
            </div>
            <div class="form-group">
                <label>Department</label>
                <input type="text" name="department" value="${department != null ? department : ''}" placeholder="e.g., Computer Science">
            </div>
        </div>
        <div class="form-group">
            <label>Major / Degree Program</label>
            <input type="text" name="major" value="${major != null ? major : ''}" placeholder="e.g., BSc Computer Science">
        </div>
        <button type="submit" class="btn-register">Create Account</button>
    </form>

    <div class="login-link">
        Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
    </div>
</div>
</body>
</html>
