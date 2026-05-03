<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>MO Dashboard - TA Recruitment System</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }

        body {
            background: #f4f7fb;
            color: #1e293b;
        }

        /* 左侧导航栏 */
        .sidebar {
            width: 250px;
            min-height: 100vh;
            background: linear-gradient(180deg, #1e3a8a, #1d4ed8);
            padding: 28px 18px;
            position: fixed;
            left: 0;
            top: 0;
            color: white;
            box-shadow: 4px 0 18px rgba(15, 23, 42, 0.08);
            z-index: 1000;
        }

        .sidebar .brand {
            font-size: 18px;
            font-weight: 700;
            line-height: 1.3;
            margin-bottom: 8px;
        }

        .sidebar .role {
            font-size: 12px;
            color: #bfdbfe;
            margin-bottom: 28px;
            padding-bottom: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .sidebar .nav-title {
            font-size: 11px;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: #bfdbfe;
            margin: 20px 0 12px;
            font-weight: 700;
        }

        .sidebar a {
            display: flex;
            align-items: center;
            color: #dbeafe;
            text-decoration: none;
            font-size: 14px;
            margin: 6px 0;
            padding: 11px 14px;
            border-radius: 10px;
            transition: 0.2s ease;
        }

        .sidebar a:hover {
            background: rgba(255, 255, 255, 0.12);
            color: white;
        }

        .sidebar a.active {
            background: white;
            color: #1d4ed8;
            font-weight: 700;
        }

        .sidebar a .icon {
            margin-right: 10px;
            font-size: 16px;
        }

        .sidebar .logout-link {
            margin-top: 30px;
            color: #fecaca;
            border-top: 1px solid rgba(255, 255, 255, 0.1);
            padding-top: 20px;
        }

        .sidebar .logout-link:hover {
            background: rgba(239, 68, 68, 0.18);
            color: white;
        }

        /* 主内容区 */
        .main {
            margin-left: 250px;
            padding: 32px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .page-title {
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 10px 16px;
            background: white;
            border-radius: 10px;
            border: 1px solid #e2e8f0;
        }

        .user-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 700;
            font-size: 14px;
        }

        .user-name {
            font-size: 14px;
            font-weight: 600;
            color: #0f172a;
        }

        /* 统计卡片网格 */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 32px;
        }

        .stat-card {
            background: white;
            padding: 24px;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
            transition: all 0.2s;
        }

        .stat-card:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            transform: translateY(-2px);
        }

        .stat-label {
            font-size: 13px;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .stat-value {
            font-size: 36px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .stat-change {
            font-size: 12px;
            color: #64748b;
        }

        .stat-change.positive {
            color: #16a34a;
        }

        .stat-change.warning {
            color: #ea580c;
        }

        /* 内容网格 */
        .content-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 24px;
            margin-bottom: 24px;
        }

        .panel {
            background: white;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
            padding: 24px;
        }

        .panel-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .panel-title {
            font-size: 18px;
            font-weight: 700;
            color: #0f172a;
        }

        .panel-action {
            font-size: 13px;
            color: #2563eb;
            text-decoration: none;
            font-weight: 600;
        }

        .panel-action:hover {
            color: #1d4ed8;
        }

        /* 职位卡片 */
        .job-card {
            padding: 16px;
            border: 1px solid #f1f5f9;
            border-radius: 10px;
            margin-bottom: 12px;
            transition: all 0.2s;
        }

        .job-card:hover {
            background: #f8fafc;
            border-color: #e2e8f0;
        }

        .job-card-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 10px;
        }

        .job-title {
            font-size: 15px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .job-module {
            font-size: 12px;
            color: #64748b;
        }

        .job-badge {
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
        }

        .badge-pending {
            background: #fef3c7;
            color: #92400e;
        }

        .badge-open {
            background: #d1fae5;
            color: #065f46;
        }

        .job-stats {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: #64748b;
            margin-top: 10px;
        }

        .job-stat {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        /* 申请列表 */
        .application-item {
            padding: 14px;
            border-bottom: 1px solid #f1f5f9;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .application-item:last-child {
            border-bottom: none;
        }

        .application-info {
            flex: 1;
        }

        .applicant-name {
            font-size: 14px;
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .application-meta {
            font-size: 12px;
            color: #64748b;
        }

        .status-badge {
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
        }

        .status-pending {
            background: #fef3c7;
            color: #92400e;
        }

        .status-reviewing {
            background: #dbeafe;
            color: #1e40af;
        }

        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #94a3b8;
        }

        .empty-state-icon {
            font-size: 48px;
            margin-bottom: 12px;
            opacity: 0.5;
        }

        /* 快速操作按钮 */
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
            margin-bottom: 32px;
        }

        .quick-action-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            padding: 16px;
            background: white;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            text-decoration: none;
            color: #0f172a;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.2s;
        }

        .quick-action-btn:hover {
            border-color: #2563eb;
            background: #eff6ff;
            transform: translateY(-2px);
        }

        .quick-action-btn.primary {
            background: #2563eb;
            color: white;
            border-color: #2563eb;
        }

        .quick-action-btn.primary:hover {
            background: #1d4ed8;
        }

        @media (max-width: 1200px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .content-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .sidebar {
                width: 200px;
            }

            .main {
                margin-left: 200px;
                padding: 20px;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

<!-- 左侧导航栏 -->
<div class="sidebar">
    <div class="brand">TA Recruitment</div>
    <div class="role">Module Organiser Portal</div>

    <div class="nav-title">Main</div>
    <a href="${pageContext.request.contextPath}/mo/dashboard" class="active">
        <span class="icon">📊</span> Dashboard
    </a>
    <a href="${pageContext.request.contextPath}/mo/create-job">
        <span class="icon">➕</span> Create Job
    </a>

    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/mo/jobs">
        <span class="icon">📄</span> My Jobs
    </a>
    <a href="${pageContext.request.contextPath}/mo/applications">
        <span class="icon">📋</span> Applications
        <c:if test="${stats.pendingApplications > 0}">
            <span style="margin-left: auto; background: #fef3c7; color: #92400e; padding: 2px 8px; border-radius: 999px; font-size: 11px; font-weight: 700;">${stats.pendingApplications}</span>
        </c:if>
    </a>
    <a href="${pageContext.request.contextPath}/mo/analytics">
        <span class="icon">📈</span> Analytics
    </a>

    <div class="nav-title">Tools</div>
    <a href="${pageContext.request.contextPath}/mo/templates">
        <span class="icon">📝</span> Templates
    </a>

    <a href="${pageContext.request.contextPath}/logout" class="logout-link">
        <span class="icon">🚪</span> Sign Out
    </a>
</div>

<!-- 主内容区 -->
<div class="main">
    <div class="topbar">
        <div class="page-title">Dashboard</div>
        <div class="user-info">
            <div class="user-avatar">${currentUser.fullName.substring(0, 1).toUpperCase()}</div>
            <div class="user-name">${currentUser.fullName}</div>
        </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Total Applications</div>
            <div class="stat-value">${stats.totalApplications}</div>
            <div class="stat-change">
                <c:if test="${stats.pendingApplications > 0}">
                    <span class="warning">${stats.pendingApplications} pending review</span>
                </c:if>
                <c:if test="${stats.pendingApplications == 0}">
                    All caught up!
                </c:if>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Active Jobs</div>
            <div class="stat-value">${stats.openJobs}</div>
            <div class="stat-change">
                ${stats.totalJobs} total jobs posted
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Fill Rate</div>
            <div class="stat-value">${stats.fillRate}%</div>
            <div class="stat-change">
                ${stats.totalFilled} / ${stats.totalVacancies} positions filled
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Accepted</div>
            <div class="stat-value">${stats.acceptedApplications}</div>
            <div class="stat-change positive">
                ${stats.rejectedApplications} rejected
            </div>
        </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
        <a href="${pageContext.request.contextPath}/mo/create-job" class="quick-action-btn primary">
            <span>➕</span> Create New Job Posting
        </a>
        <a href="${pageContext.request.contextPath}/mo/applications" class="quick-action-btn">
            <span>📋</span> Review Applications
            <c:if test="${stats.pendingApplications > 0}">
                (${stats.pendingApplications})
            </c:if>
        </a>
        <a href="${pageContext.request.contextPath}/mo/analytics" class="quick-action-btn">
            <span>📈</span> View Analytics
        </a>
        <a href="${pageContext.request.contextPath}/mo/templates" class="quick-action-btn">
            <span>📝</span> Use Template
        </a>
    </div>

    <!-- 内容网格 -->
    <div class="content-grid">
        <!-- 需要关注的职位 -->
        <div class="panel">
            <div class="panel-header">
                <div class="panel-title">Jobs Needing Attention</div>
                <a href="${pageContext.request.contextPath}/mo/applications" class="panel-action">View All →</a>
            </div>

            <c:choose>
                <c:when test="${not empty jobsNeedingAttention}">
                    <c:forEach var="job" items="${jobsNeedingAttention}">
                        <div class="job-card">
                            <div class="job-card-header">
                                <div>
                                    <div class="job-title">${job.title}</div>
                                    <div class="job-module">${job.moduleCode} - ${job.moduleName}</div>
                                </div>
                                <span class="job-badge badge-pending">Pending</span>
                            </div>
                            <div class="job-stats">
                                <div class="job-stat">
                                    <span>📝</span>
                                    <c:set var="pendingCount" value="0"/>
                                    <c:forEach var="app" items="${recentApplications}">
                                        <c:if test="${app.jobId == job.jobId && app.status == 'PENDING'}">
                                            <c:set var="pendingCount" value="${pendingCount + 1}"/>
                                        </c:if>
                                    </c:forEach>
                                    ${pendingCount} new applications
                                </div>
                                <div class="job-stat">
                                    <span>👥</span>
                                    ${job.filledCount}/${job.vacancies} filled
                                </div>
                                <div class="job-stat">
                                    <span>📅</span>
                                    Deadline: ${job.deadline}
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-icon">✅</div>
                        <p>All jobs are up to date!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- 最近的申请 -->
        <div class="panel">
            <div class="panel-header">
                <div class="panel-title">Recent Applications</div>
                <a href="${pageContext.request.contextPath}/mo/applications" class="panel-action">View All →</a>
            </div>

            <c:choose>
                <c:when test="${not empty recentApplications}">
                    <c:forEach var="app" items="${recentApplications}">
                        <div class="application-item">
                            <div class="application-info">
                                <div class="applicant-name">${app.taName}</div>
                                <div class="application-meta">
                                    ${app.jobTitle} • Applied ${app.appliedAt.substring(0, 10)}
                                </div>
                            </div>
                            <c:choose>
                                <c:when test="${app.status == 'PENDING'}">
                                    <span class="status-badge status-pending">Pending</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge">${app.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-icon">📭</div>
                        <p>No applications yet</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

</body>
</html>
