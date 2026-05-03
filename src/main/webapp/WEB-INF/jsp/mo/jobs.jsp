<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Job Postings - MO Dashboard</title>
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
            font-weight: 700;
            color: #93c5fd;
            margin: 24px 0 10px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .sidebar a {
            display: flex;
            align-items: center;
            padding: 10px 14px;
            color: #e0e7ff;
            text-decoration: none;
            border-radius: 8px;
            margin-bottom: 4px;
            transition: all 0.2s;
            font-size: 14px;
        }

        .sidebar a:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }

        .sidebar a.active {
            background: rgba(255, 255, 255, 0.15);
            color: white;
            font-weight: 600;
        }

        .sidebar .icon {
            margin-right: 10px;
            font-size: 16px;
        }

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

        .alert {
            padding: 14px 18px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-success {
            background: #d1fae5;
            color: #065f46;
            border: 1px solid #a7f3d0;
        }

        .alert-error {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }

        .jobs-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 1px 3px rgba(15, 23, 42, 0.08);
            overflow: hidden;
        }

        .job-item {
            padding: 20px;
            border-bottom: 1px solid #f1f5f9;
            display: flex;
            justify-content: space-between;
            align-items: start;
            transition: all 0.2s;
        }

        .job-item.cancelled {
            background: #f8f9fa;
            opacity: 0.7;
        }

        .job-item.cancelled .job-title {
            text-decoration: line-through;
            color: #94a3b8;
        }

        .job-item.cancelled .job-module,
        .job-item.cancelled .job-stats {
            color: #94a3b8;
        }

        .job-item:last-child {
            border-bottom: none;
        }

        .job-info {
            flex: 1;
        }

        .job-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;
        }

        .job-title {
            font-size: 16px;
            font-weight: 700;
            color: #0f172a;
        }

        .job-badge {
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
        }

        .badge-open {
            background: #d1fae5;
            color: #065f46;
        }

        .badge-closed {
            background: #e2e8f0;
            color: #475569;
        }

        .badge-cancelled {
            background: #fee2e2;
            color: #991b1b;
        }

        .job-module {
            font-size: 13px;
            color: #64748b;
            margin-bottom: 10px;
        }

        .job-stats {
            display: flex;
            gap: 20px;
            font-size: 13px;
            color: #64748b;
        }

        .job-stat {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .job-actions {
            display: flex;
            gap: 8px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            text-decoration: none;
            display: inline-block;
        }

        .btn-primary {
            background: #2563eb;
            color: white;
        }

        .btn-primary:hover {
            background: #1d4ed8;
        }

        .btn-danger {
            background: #dc2626;
            color: white;
        }

        .btn-danger:hover {
            background: #b91c1c;
        }

        .btn-secondary {
            background: #e2e8f0;
            color: #475569;
        }

        .btn-secondary:hover {
            background: #cbd5e1;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #64748b;
        }

        .empty-state-icon {
            font-size: 48px;
            margin-bottom: 16px;
        }

        .empty-state p {
            font-size: 16px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<div class="sidebar">
    <div class="brand">TA Recruitment</div>
    <div class="role">Module Organiser Portal</div>

    <div class="nav-title">Main</div>
    <a href="${pageContext.request.contextPath}/mo/dashboard">
        <span class="icon">📊</span> Dashboard
    </a>
    <a href="${pageContext.request.contextPath}/mo/create-job">
        <span class="icon">➕</span> Create Job
    </a>

    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/mo/jobs" class="active">
        <span class="icon">📄</span> My Jobs
    </a>
    <a href="${pageContext.request.contextPath}/mo/applications">
        <span class="icon">📋</span> Applications
    </a>
    <a href="${pageContext.request.contextPath}/mo/analytics">
        <span class="icon">📈</span> Analytics
    </a>

    <div class="nav-title">Tools</div>
    <a href="${pageContext.request.contextPath}/mo/templates">
        <span class="icon">📝</span> Templates
    </a>
    <a href="${pageContext.request.contextPath}/logout">
        <span class="icon">🚪</span> Logout
    </a>
</div>

<div class="main">
    <div class="topbar">
        <h1 class="page-title">My Job Postings</h1>
        <div class="user-info">
            <div class="user-avatar">${sessionScope.user.name.substring(0,1).toUpperCase()}</div>
            <div class="user-name">${sessionScope.user.name}</div>
        </div>
    </div>

    <div style="margin-bottom: 24px; position: relative;">
        <span style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); color: #94a3b8; font-size: 16px;">🔍</span>
        <input type="text" id="searchInput" placeholder="Search jobs by title, module code, or module name..."
               style="width: 100%; padding: 12px 16px 12px 44px; border: 1px solid #e2e8f0; border-radius: 10px; font-size: 14px; transition: all 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.04);"
               onfocus="this.style.borderColor='#3b82f6'; this.style.boxShadow='0 4px 12px rgba(59,130,246,0.15)';"
               onblur="this.style.borderColor='#e2e8f0'; this.style.boxShadow='0 2px 4px rgba(0,0,0,0.04)';">
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <div class="jobs-container">
        <c:choose>
            <c:when test="${not empty jobs}">
                <c:forEach var="job" items="${jobs}">
                    <div class="job-item ${job.status == 'CANCELLED' ? 'cancelled' : ''}">
                        <div class="job-info">
                            <div class="job-header">
                                <div class="job-title">${job.title}</div>
                                <c:choose>
                                    <c:when test="${job.status == 'OPEN'}">
                                        <span class="job-badge badge-open">Open</span>
                                    </c:when>
                                    <c:when test="${job.status == 'CLOSED'}">
                                        <span class="job-badge badge-closed">Closed</span>
                                    </c:when>
                                    <c:when test="${job.status == 'CANCELLED'}">
                                        <span class="job-badge badge-cancelled">Cancelled</span>
                                    </c:when>
                                </c:choose>
                            </div>
                            <div class="job-module">${job.moduleCode} - ${job.moduleName}</div>
                            <div class="job-stats">
                                <div class="job-stat">
                                    <span>👥</span>
                                    ${job.filledCount}/${job.vacancies} filled
                                </div>
                                <div class="job-stat">
                                    <span>📋</span>
                                    ${job.applicationIds.size()} applications
                                </div>
                                <div class="job-stat">
                                    <span>📅</span>
                                    Deadline: ${job.deadline}
                                </div>
                                <div class="job-stat">
                                    <span>💰</span>
                                    £${job.hourlyRate}/hr
                                </div>
                            </div>
                        </div>
                        <div class="job-actions">
                            <c:if test="${job.status != 'CANCELLED'}">
                                <a href="${pageContext.request.contextPath}/mo/applications?jobId=${job.jobId}" class="btn btn-primary">
                                    View Applications
                                </a>
                            </c:if>
                            <c:if test="${job.status == 'OPEN'}">
                                <form method="post" action="${pageContext.request.contextPath}/mo/jobs" style="display: inline;">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="jobId" value="${job.jobId}">
                                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this job posting? This action cannot be undone.')">
                                        Cancel Job
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📄</div>
                    <p>You haven't posted any jobs yet</p>
                    <a href="${pageContext.request.contextPath}/mo/create-job" class="btn btn-primary">
                        Create Your First Job
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    // Search functionality
    const searchInput = document.getElementById('searchInput');
    const jobItems = document.querySelectorAll('.job-item');

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();

        jobItems.forEach(item => {
            const title = item.querySelector('.job-title').textContent.toLowerCase();
            const module = item.querySelector('.job-module').textContent.toLowerCase();

            if (title.includes(searchTerm) || module.includes(searchTerm)) {
                item.style.display = '';
            } else {
                item.style.display = 'none';
            }
        });
    });
</script>

</body>
</html>
