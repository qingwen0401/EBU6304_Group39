<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Profile - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9f9f9; margin: 0; padding: 20px; }
        .container { max-width: 900px; margin: 0 auto; background: white; border: 1px solid #e0e0e0; }
        .nav-sidebar { width: 120px; float: left; background: #f0f0f0; padding: 15px; min-height: 600px; }
        .nav-sidebar p { font-size: 12px; font-weight: bold; margin-bottom: 15px; }
        .nav-sidebar a { display: block; margin: 8px 0; text-decoration: none; color: #333; font-size: 12px; }
        .nav-sidebar a.active { font-weight: bold; color: #0066cc; }
        .nav-sidebar a:hover { color: #0066cc; }
        .logout-link { color: #dc2626 !important; margin-top: 20px; }
        .content { margin-left: 140px; padding: 20px; }
        .breadcrumb { font-size: 12px; color: #666; margin-bottom: 15px; }
        .section { margin-bottom: 25px; padding: 15px; border: 1px solid #e0e0e0; border-radius: 4px; }
        .section h4 { margin-top: 0; color: #444; font-size: 14px; margin-bottom: 12px; }
        .field { margin: 10px 0; }
        .field label { display: block; font-size: 12px; color: #666; margin-bottom: 3px; }
        .field .value { padding: 6px; background: #f8f9fa; border: 1px solid #eee; border-radius: 3px; font-size: 13px; }
        .btn-group { text-align: right; padding: 0 0 15px 0; }
        .btn { padding: 6px 12px; border: none; border-radius: 3px; cursor: pointer; font-size: 12px; text-decoration: none; display: inline-block; }
        .btn-edit { background: #007bff; color: white; }
        .btn-edit:hover { background: #0056b3; }
        .skills-list { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 5px; }
        .skill-tag { background: #e0f2fe; color: #0369a1; padding: 3px 8px; border-radius: 3px; font-size: 12px; }
        .no-data { color: #94a3b8; font-size: 13px; font-style: italic; }
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
        <div class="breadcrumb">Home &gt; My Profile</div>
        <h2 style="font-size:18px;margin-bottom:20px;">TA Profile</h2>

        <c:choose>
            <c:when test="${ta != null}">
                <div class="section">
                    <h4>Personal Details</h4>
                    <div class="field">
                        <label>Full Name</label>
                        <div class="value">${not empty ta.fullName ? ta.fullName : ta.username}</div>
                    </div>
                    <div class="field">
                        <label>Username</label>
                        <div class="value">${ta.username}</div>
                    </div>
                    <div class="field">
                        <label>Student ID</label>
                        <div class="value">${not empty ta.studentId ? ta.studentId : '-'}</div>
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <div class="value">${not empty ta.email ? ta.email : '-'}</div>
                    </div>
                    <div class="field">
                        <label>Department</label>
                        <div class="value">${not empty ta.department ? ta.department : '-'}</div>
                    </div>
                    <div class="field">
                        <label>Major / Degree Program</label>
                        <div class="value">${not empty ta.major ? ta.major : '-'}</div>
                    </div>
                    <div class="field">
                        <label>Year</label>
                        <div class="value">${not empty ta.year ? ta.year : '-'}</div>
                    </div>
                    <div class="field">
                        <label>GPA</label>
                        <div class="value">${ta.gpa > 0 ? ta.gpa : '-'}</div>
                    </div>
                </div>

                <div class="section">
                    <h4>Skills</h4>
                    <c:choose>
                        <c:when test="${not empty ta.skills}">
                            <div class="skills-list">
                                <c:forEach var="skill" items="${ta.skills}">
                                    <span class="skill-tag">${skill}</span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-data">No skills listed yet.</p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${not empty ta.bio}">
                    <div class="section">
                        <h4>Bio</h4>
                        <p style="font-size:13px;color:#374151;">${ta.bio}</p>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <div class="section">
                    <p class="no-data">Profile not found. Please update your profile.</p>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/ta/profile?action=edit" class="btn btn-edit">Edit Profile</a>
        </div>
    </div>
</div>
</body>
</html>
