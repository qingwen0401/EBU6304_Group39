<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Profile - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9f9f9; margin: 0; padding: 20px; }
        .container { max-width: 900px; margin: 0 auto; background: white; border: 1px solid #e0e0e0; }
        .nav-sidebar { width: 120px; float: left; background: #f0f0f0; padding: 15px; min-height: 700px; }
        .nav-sidebar p { font-size: 12px; font-weight: bold; margin-bottom: 15px; }
        .nav-sidebar a { display: block; margin: 8px 0; text-decoration: none; color: #333; font-size: 12px; }
        .nav-sidebar a.active { font-weight: bold; color: #0066cc; }
        .nav-sidebar a:hover { color: #0066cc; }
        .logout-link { color: #dc2626 !important; margin-top: 20px; }
        .content { margin-left: 140px; padding: 20px; }
        .breadcrumb { font-size: 12px; color: #666; margin-bottom: 15px; }
        .section { margin-bottom: 25px; padding: 15px; border: 1px solid #e0e0e0; border-radius: 4px; }
        .section h4 { margin-top: 0; color: #444; font-size: 14px; margin-bottom: 12px; }
        .form-group { margin: 10px 0; }
        .form-group label { display: block; font-size: 12px; color: #666; margin-bottom: 3px; }
        .form-group input, .form-group textarea, .form-group select {
            width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 3px;
            box-sizing: border-box; font-size: 13px;
        }
        .form-group textarea { resize: vertical; }
        .required { color: red; }
        .btn-group { text-align: right; margin-top: 20px; padding-bottom: 20px; }
        .btn { padding: 6px 12px; border: none; border-radius: 3px; cursor: pointer; font-size: 12px; margin-left: 8px; text-decoration: none; display: inline-block; }
        .btn-cancel { background: #6c757d; color: white; }
        .btn-save { background: #007bff; color: white; }
        .btn-save:hover { background: #0056b3; }
        .row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    </style>
</head>
<body>
<div class="container">
    <div class="nav-sidebar">
        <p>Navigation</p>
        <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
        <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
        <a href="${pageContext.request.contextPath}/ta/profile" class="active">My Profile</a>
        <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
    </div>

    <div class="content">
        <div class="breadcrumb">Home &gt; My Profile &gt; Edit</div>
        <h2 style="font-size:18px;margin-bottom:20px;">Edit TA Profile</h2>

        <form method="post" action="${pageContext.request.contextPath}/ta/profile">
            <div class="section">
                <h4>Personal Details</h4>
                <div class="form-group">
                    <label>Full Name <span class="required">*</span></label>
                    <input type="text" name="fullName" value="${ta != null && not empty ta.fullName ? ta.fullName : ''}" required>
                </div>
                <div class="row">
                    <div class="form-group">
                        <label>Student ID <span class="required">*</span></label>
                        <input type="text" name="studentId" value="${ta != null && not empty ta.studentId ? ta.studentId : ''}" required>
                    </div>
                    <div class="form-group">
                        <label>University Email <span class="required">*</span></label>
                        <input type="email" name="email" value="${ta != null && not empty ta.email ? ta.email : ''}" required>
                    </div>
                </div>
                <div class="row">
                    <div class="form-group">
                        <label>Department</label>
                        <input type="text" name="department" value="${ta != null && not empty ta.department ? ta.department : ''}">
                    </div>
                    <div class="form-group">
                        <label>Major / Degree Program</label>
                        <input type="text" name="major" value="${ta != null && not empty ta.major ? ta.major : ''}">
                    </div>
                </div>
                <div class="row">
                    <div class="form-group">
                        <label>Year (e.g., Year 2, MSc Year 1)</label>
                        <input type="text" name="year" value="${ta != null && not empty ta.year ? ta.year : ''}">
                    </div>
                    <div class="form-group">
                        <label>GPA (0.0 - 4.0)</label>
                        <input type="number" name="gpa" step="0.01" min="0" max="4" value="${ta != null && ta.gpa > 0 ? ta.gpa : ''}">
                    </div>
                </div>
            </div>

            <div class="section">
                <h4>Skills &amp; Bio</h4>
                <div class="form-group">
                    <label>Skills (comma-separated)</label>
                    <textarea name="skills" rows="3" placeholder="e.g., Python, Java, Machine Learning, Data Analysis"><c:if test="${ta != null && not empty ta.skills}"><c:forEach var="skill" items="${ta.skills}" varStatus="s">${skill}<c:if test="${!s.last}">, </c:if></c:forEach></c:if></textarea>
                </div>
                <div class="form-group">
                    <label>Bio / Personal Statement</label>
                    <textarea name="bio" rows="4" placeholder="Briefly describe your background and why you want to be a TA...">${ta != null && not empty ta.bio ? ta.bio : ''}</textarea>
                </div>
            </div>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/ta/profile" class="btn btn-cancel">Cancel</a>
                <button type="submit" class="btn btn-save">Save Changes</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
