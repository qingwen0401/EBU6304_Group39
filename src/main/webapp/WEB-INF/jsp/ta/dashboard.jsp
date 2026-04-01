<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ebu6304.recruitment.models.Application" %>
<%@ page import="com.ebu6304.recruitment.models.JobPosting" %>
<%@ page import="com.ebu6304.recruitment.models.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TA Dashboard - Overview</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #fff; }
        .sidebar {
            width: 120px; height: 100vh; background: #f0f0f0;
            padding: 20px 10px; position: fixed; left: 0; top: 0;
        }
        .sidebar p { font-size: 12px; font-weight: bold; margin-bottom: 15px; }
        .sidebar a {
            display: block; color: #333; text-decoration: none;
            font-size: 12px; margin: 8px 0;
        }
        .sidebar a.active { font-weight: bold; color: #0066cc; }
        .sidebar a:hover { color: #0066cc; }
        .main { margin-left: 120px; padding: 20px; max-width: 1000px; }
        .header {
            display: flex; justify-content: space-between; align-items: center;
            margin-bottom: 15px; font-size: 12px; color: #666;
        }
        .header-right { display: flex; align-items: center; gap: 10px; }
        .user-avatar {
            width: 28px; height: 28px; background: #2563eb; border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 12px; color: white; font-weight: bold;
        }
        .alert-bar {
            background: #fef9c3; color: #856404; padding: 8px 15px;
            border-radius: 3px; margin-bottom: 20px; font-size: 13px;
            display: flex; justify-content: space-between;
        }
        .alert-bar a { color: #856404; font-weight: bold; }
        .alert-bar .close { cursor: pointer; }
        .welcome { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .welcome h1 { font-size: 22px; color: #333; }
        .btn-browse {
            background: #2563eb; color: white; border: none;
            padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 13px;
            text-decoration: none; display: inline-block;
        }
        .stats-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; margin-bottom: 20px; }
        .stat-card {
            background: white; border: 1px solid #e2e8f0; border-radius: 6px;
            padding: 15px; font-size: 13px; color: #64748b;
        }
        .stat-card .number { font-size: 20px; font-weight: bold; color: #1e293b; margin-top: 5px; }
        .content-row { display: grid; grid-template-columns: 2fr 1fr; gap: 20px; }
        .section { background: white; border: 1px solid #e2e8f0; border-radius: 6px; padding: 15px; }
        .section h3 { font-size: 14px; color: #333; margin-bottom: 15px; }
        .applications-table { width: 100%; border-collapse: collapse; font-size: 12px; }
        .applications-table th, .applications-table td {
            padding: 8px; text-align: left; border-bottom: 1px solid #e2e8f0;
        }
        .applications-table th { color: #64748b; }
        .status { padding: 3px 8px; border-radius: 3px; color: white; font-size: 11px; }
        .status-PENDING { background: #eab308; }
        .status-REVIEWING { background: #2563eb; }
        .status-ACCEPTED { background: #22c55e; }
        .status-REJECTED { background: #ef4444; }
        .status-WITHDRAWN { background: #94a3b8; }
        .job-card { border-bottom: 1px solid #e2e8f0; padding: 10px 0; font-size: 12px; }
        .job-card:last-child { border-bottom: none; }
        .job-card h4 { font-size: 13px; color: #1e293b; margin-bottom: 5px; }
        .job-card p { color: #64748b; font-size: 11px; }
        .job-card .deadline { color: #dc2626; font-size: 11px; }
        .logout-link { color: #dc2626; text-decoration: none; font-size: 12px; }
        .logout-link:hover { text-decoration: underline; }
        .empty-msg { color: #94a3b8; font-size: 12px; padding: 10px 0; }
    </style>
</head>
<body>
<div class="sidebar">
    <p>Navigation</p>
    <a href="${pageContext.request.contextPath}/ta/dashboard" class="active">Dashboard</a>
    <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
    <a href="${pageContext.request.contextPath}/ta/profile">My Profile</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link" style="margin-top:20px;">Logout</a>
</div>

<div class="main">
    <div class="header">
        <span>Home &gt; Dashboard</span>
        <div class="header-right">
            <div class="user-avatar">${currentUser.username.substring(0,1).toUpperCase()}</div>
            <span style="font-size:12px;">${currentUser.username}</span>
        </div>
    </div>

    <div class="alert-bar" id="alertBar">
        <span>Complete your profile to increase your chances of selection.
            <a href="${pageContext.request.contextPath}/ta/profile?action=edit">Update Profile</a></span>
        <span class="close" onclick="document.getElementById('alertBar').style.display='none'">x</span>
    </div>

    <div class="welcome">
        <h1>Welcome back, ${currentUser.username}!</h1>
        <a href="${pageContext.request.contextPath}/ta/jobs" class="btn-browse">Browse All Jobs</a>
    </div>

    <div class="stats-row">
        <div class="stat-card">
            <div>Active Applications</div>
            <div class="number">${activeCount}</div>
        </div>
        <div class="stat-card">
            <div>Total Applications</div>
            <div class="number">${totalCount}</div>
        </div>
        <div class="stat-card">
            <div>Open Positions</div>
            <div class="number">${openJobCount}</div>
        </div>
    </div>

    <div class="content-row">
        <div class="section">
            <h3>Recent Applications</h3>
            <c:choose>
                <c:when test="${empty recentApps}">
                    <p class="empty-msg">No applications yet. <a href="${pageContext.request.contextPath}/ta/jobs">Browse jobs</a> to get started.</p>
                </c:when>
                <c:otherwise>
                    <table class="applications-table">
                        <thead>
                        <tr>
                            <th>Job Title</th>
                            <th>Date Applied</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="app" items="${recentApps}">
                            <tr style="cursor:pointer;" onclick="location.href='${pageContext.request.contextPath}/ta/applications'">
                                <td>${app.jobId}</td>
                                <td>${app.appliedAt != null ? app.appliedAt.toString().substring(0,10) : '-'}</td>
                                <td><span class="status status-${app.status}">${app.status}</span></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="section">
            <h3>New Postings for You</h3>
            <c:choose>
                <c:when test="${empty recommendedJobs}">
                    <p class="empty-msg">No open positions at the moment.</p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="job" items="${recommendedJobs}">
                        <div class="job-card" onclick="location.href='${pageContext.request.contextPath}/ta/jobs'" style="cursor:pointer;">
                            <h4>${job.title}</h4>
                            <p>${job.moduleCode} | ${job.hoursPerWeek != null ? job.hoursPerWeek : '?'} hrs/week</p>
                            <p class="deadline">Deadline: ${job.deadline != null ? job.deadline.toString().substring(0,10) : 'Open'}</p>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
