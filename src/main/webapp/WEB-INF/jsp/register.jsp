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
            padding: 40px; width: 500px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }
        h2 { font-size: 22px; color: #1e293b; margin-bottom: 8px; }
        .subtitle { font-size: 13px; color: #64748b; margin-bottom: 24px; }
        .form-group { margin-bottom: 14px; }
        .form-group label { display: block; font-size: 13px; color: #374151; margin-bottom: 5px; font-weight: bold; }
        .form-group input {
            width: 100%; padding: 9px 12px; border: 1px solid #d1d5db;
            border-radius: 4px; font-size: 14px; outline: none; transition: border-color 0.2s;
        }
        .form-group input:focus { border-color: #2563eb; }
        .form-group input.invalid { border-color: #dc2626; }
        .form-group input.valid   { border-color: #16a34a; }
        .field-hint { font-size: 11px; color: #94a3b8; margin-top: 3px; }
        .field-error { font-size: 11px; color: #dc2626; margin-top: 3px; display: none; }
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

    <form method="post" action="${pageContext.request.contextPath}/register" id="registerForm" novalidate>
        <div class="row">
            <div class="form-group">
                <label>Username <span class="required">*</span></label>
                <input type="text" name="username" id="username"
                       value="${username != null ? username : ''}" required>
                <div class="field-hint">3–20 characters, letters/numbers/underscore</div>
                <div class="field-error" id="usernameError">Invalid username format.</div>
            </div>
            <div class="form-group">
                <label>Full Name <span class="required">*</span></label>
                <input type="text" name="fullName" id="fullName"
                       value="${fullName != null ? fullName : ''}" required>
                <div class="field-error" id="fullNameError">Full name is required.</div>
            </div>
        </div>

        <div class="form-group">
            <label>University Email <span class="required">*</span></label>
            <input type="email" name="email" id="email"
                   value="${email != null ? email : ''}"
                   placeholder="e.g. 2312229xx@student.qmul.ac.uk" required>
            <div class="field-hint">Must be a university email (.ac.uk or .edu.cn)</div>
            <div class="field-error" id="emailError">Please enter a valid university email.</div>
        </div>

        <div class="row">
            <div class="form-group">
                <label>Password <span class="required">*</span></label>
                <input type="password" name="password" id="password" required>
                <div class="field-hint">At least 8 characters with letters and numbers</div>
                <div class="field-error" id="passwordError">Password too weak.</div>
            </div>
            <div class="form-group">
                <label>Confirm Password <span class="required">*</span></label>
                <input type="password" name="confirmPassword" id="confirmPassword" required>
                <div class="field-error" id="confirmError">Passwords do not match.</div>
            </div>
        </div>

        <div class="row">
            <div class="form-group">
                <label>Student ID <span class="required">*</span></label>
                <input type="text" name="studentId" id="studentId"
                       value="${studentId != null ? studentId : ''}" required>
                <div class="field-error" id="studentIdError">Student ID is required.</div>
            </div>
            <div class="form-group">
                <label>Department</label>
                <input type="text" name="department"
                       value="${department != null ? department : ''}"
                       placeholder="e.g. Computer Science">
            </div>
        </div>

        <div class="form-group">
            <label>Major / Degree Program</label>
            <input type="text" name="major"
                   value="${major != null ? major : ''}"
                   placeholder="e.g. BSc Computer Science">
        </div>

        <button type="submit" class="btn-register">Create Account</button>
    </form>

    <div class="login-link">
        Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
    </div>
</div>

<script>
    // 正则表达式（与后端保持一致）
    const usernameRegex = /^[A-Za-z0-9_]{3,20}$/;
    const emailRegex    = /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.(ac\.uk|edu\.cn|edu)$/i;
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;

    function showError(inputId, errorId, condition) {
        const input = document.getElementById(inputId);
        const error = document.getElementById(errorId);
        if (condition) {
            input.classList.add('invalid');
            input.classList.remove('valid');
            if (error) error.style.display = 'block';
            return false;
        } else {
            input.classList.remove('invalid');
            input.classList.add('valid');
            if (error) error.style.display = 'none';
            return true;
        }
    }

    // 各字段实时校验
    document.getElementById('username').addEventListener('blur', function() {
        showError('username', 'usernameError', !usernameRegex.test(this.value));
    });

    document.getElementById('email').addEventListener('blur', function() {
        showError('email', 'emailError', !emailRegex.test(this.value.trim()));
    });

    document.getElementById('password').addEventListener('blur', function() {
        showError('password', 'passwordError', !passwordRegex.test(this.value));
    });

    document.getElementById('confirmPassword').addEventListener('blur', function() {
        const pw = document.getElementById('password').value;
        showError('confirmPassword', 'confirmError', this.value !== pw);
    });

    document.getElementById('fullName').addEventListener('blur', function() {
        showError('fullName', 'fullNameError', this.value.trim() === '');
    });

    document.getElementById('studentId').addEventListener('blur', function() {
        showError('studentId', 'studentIdError', this.value.trim() === '');
    });

    // 提交前整体校验
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        const username  = document.getElementById('username').value;
        const email     = document.getElementById('email').value;
        const password  = document.getElementById('password').value;
        const confirm   = document.getElementById('confirmPassword').value;
        const fullName  = document.getElementById('fullName').value;
        const studentId = document.getElementById('studentId').value;

        let valid = true;
        valid = showError('username',        'usernameError',  !usernameRegex.test(username))        && valid;
        valid = showError('email',           'emailError',     !emailRegex.test(email.trim()))       && valid;
        valid = showError('password',        'passwordError',  !passwordRegex.test(password))        && valid;
        valid = showError('confirmPassword', 'confirmError',   password !== confirm)                 && valid;
        valid = showError('fullName',        'fullNameError',  fullName.trim() === '')               && valid;
        valid = showError('studentId',       'studentIdError', studentId.trim() === '')              && valid;

        if (!valid) e.preventDefault();
    });
</script>
</body>
</html>