<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f4f7fb; color: #1e293b; }
        .sidebar { width: 250px; min-height: 100vh; background: linear-gradient(180deg, #1e3a8a, #1d4ed8); padding: 28px 18px; position: fixed; left: 0; top: 0; color: white; box-shadow: 4px 0 18px rgba(15,23,42,.08); }
        .brand { font-size: 18px; font-weight: 700; margin-bottom: 8px; }
        .role { font-size: 12px; color: #bfdbfe; margin-bottom: 28px; padding-bottom: 20px; border-bottom: 1px solid rgba(255,255,255,.1); }
        .nav-title { font-size: 11px; letter-spacing: .08em; text-transform: uppercase; color: #bfdbfe; margin: 20px 0 12px; font-weight: 700; }
        .sidebar a { display: flex; color: #dbeafe; text-decoration: none; font-size: 14px; margin: 6px 0; padding: 11px 14px; border-radius: 10px; transition: .2s ease; }
        .sidebar a:hover { background: rgba(255,255,255,.12); color: white; }
        .sidebar a.active { background: white; color: #1d4ed8; font-weight: 700; }
        .logout-link { margin-top: 30px; color: #fecaca !important; border-top: 1px solid rgba(255,255,255,.1); padding-top: 20px !important; }
        .main { margin-left: 250px; padding: 32px; }
        .topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
        .page-title { font-size: 28px; font-weight: 700; color: #0f172a; }
        .user-info { display: flex; align-items: center; gap: 12px; padding: 10px 16px; background: white; border-radius: 10px; border: 1px solid #e2e8f0; }
        .avatar { width: 36px; height: 36px; border-radius: 50%; background: linear-gradient(135deg,#2563eb,#3b82f6); display: flex; align-items: center; justify-content: center; color: white; font-weight: 700; }
        .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 28px; }
        .stat-card, .panel { background: white; border-radius: 14px; border: 1px solid #e2e8f0; box-shadow: 0 4px 12px rgba(0,0,0,.04); }
        .stat-card { padding: 24px; }
        .stat-label { font-size: 13px; color: #64748b; text-transform: uppercase; letter-spacing: .5px; font-weight: 600; margin-bottom: 8px; }
        .stat-value { font-size: 36px; font-weight: 700; color: #0f172a; margin-bottom: 8px; }
        .stat-change { font-size: 12px; color: #64748b; }
        .content-grid { display: grid; grid-template-columns: 1.2fr .8fr; gap: 24px; }
        .panel { padding: 24px; margin-bottom: 24px; }
        .panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
        .panel-title { font-size: 18px; color: #0f172a; font-weight: 700; }
        .link { color: #2563eb; font-size: 13px; text-decoration: none; font-weight: 700; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; }
        th { color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: .05em; }
        .badge { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; background: #dbeafe; color: #1d4ed8; }
        .badge.danger { background: #fee2e2; color: #dc2626; }
        .badge.ok { background: #dcfce7; color: #16a34a; }
        @media (max-width: 900px) { .sidebar { position: static; width: 100%; min-height: auto; } .main { margin-left: 0; } .stats-grid, .content-grid { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment</div>
    <div class="role">Administrator Portal</div>
    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/workload">Workload Monitor</a>
    <a href="${pageContext.request.contextPath}/admin/jobs">Job Postings</a>
    <a href="${pageContext.request.contextPath}/admin/users">User Accounts</a>
    <a href="${pageContext.request.contextPath}/admin/audit">Audit Log</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<main class="main">
    <div class="topbar">
        <div>
            <h1 class="page-title">Admin Dashboard</h1>
            <p class="stat-change">System-wide recruitment, workload, and governance overview.</p>
        </div>
        <div class="user-info">
            <div class="avatar">AD</div>
            <div>${currentUser.username}</div>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Users</div>
            <div class="stat-value">${totalUsers}</div>
            <div class="stat-change">${taCount} TAs, ${moCount} MOs, ${adminCount} admins</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Job Postings</div>
            <div class="stat-value">${totalJobs}</div>
            <div class="stat-change">${openJobs} open, ${closedJobs} closed, ${cancelledJobs} cancelled</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Applications</div>
            <div class="stat-value">${applicationStats.total}</div>
            <div class="stat-change">${applicationStats.accepted} accepted, ${applicationStats.rejected} rejected</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Overloaded TAs</div>
            <div class="stat-value">${overloadedCount}</div>
            <div class="stat-change">Threshold: ${maxWeeklyHours} hrs/week</div>
        </div>
    </div>

    <div class="content-grid">
        <section class="panel">
            <div class="panel-header">
                <h2 class="panel-title">Recent Job Postings</h2>
                <a class="link" href="${pageContext.request.contextPath}/admin/jobs">View all</a>
            </div>
            <table>
                <thead><tr><th>Title</th><th>Module</th><th>Owner</th><th>Status</th></tr></thead>
                <tbody>
                <c:forEach var="job" items="${recentJobs}">
                    <tr>
                        <td>${job.title}</td>
                        <td>${job.moduleCode}</td>
                        <td>${job.moName}</td>
                        <td><span class="badge">${job.status}</span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty recentJobs}">
                    <tr><td colspan="4">No job postings found.</td></tr>
                </c:if>
                </tbody>
            </table>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2 class="panel-title">Application Status</h2>
            </div>
            <table>
                <tbody>
                <tr><td>Pending</td><td><span class="badge">${applicationStats.pending}</span></td></tr>
                <tr><td>Accepted</td><td><span class="badge ok">${applicationStats.accepted}</span></td></tr>
                <tr><td>Rejected</td><td><span class="badge danger">${applicationStats.rejected}</span></td></tr>
                <tr><td>Withdrawn</td><td><span class="badge">${applicationStats.withdrawn}</span></td></tr>
                </tbody>
            </table>
        </section>
    </div>

    <section class="panel">
        <div class="panel-header">
            <h2 class="panel-title">Recruitment Distribution</h2>
            <a class="link" href="${pageContext.request.contextPath}/admin/jobs">Review jobs</a>
        </div>
        <table>
            <thead><tr><th>Module</th><th>Jobs</th><th>Vacancies</th><th>Filled</th><th>Applications</th><th>Accepted</th></tr></thead>
            <tbody>
            <c:forEach var="row" items="${recruitmentDistribution}">
                <tr>
                    <td>${row.module}</td>
                    <td>${row.jobCount}</td>
                    <td>${row.vacancies}</td>
                    <td>${row.filled}</td>
                    <td>${row.applicationCount}</td>
                    <td><span class="badge ok">${row.acceptedCount}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty recruitmentDistribution}">
                <tr><td colspan="6">No recruitment records found.</td></tr>
            </c:if>
            </tbody>
        </table>
    </section>

    <section class="panel">
        <div class="panel-header">
            <h2 class="panel-title">Recent Login Activity</h2>
            <a class="link" href="${pageContext.request.contextPath}/admin/audit?action=LOGIN">Open login log</a>
        </div>
        <table>
            <thead><tr><th>Time</th><th>Account</th><th>Role</th><th>Outcome</th><th>Details</th></tr></thead>
            <tbody>
            <c:forEach var="entry" items="${recentLoginActivity}">
                <tr>
                    <td>${entry.timestamp}</td>
                    <td>${entry.username}</td>
                    <td>${entry.role}</td>
                    <td><span class="badge ${entry.outcome == 'FAILED' ? 'danger' : 'ok'}">${entry.outcome}</span></td>
                    <td>${entry.details}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentLoginActivity}">
                <tr><td colspan="5">No login activity found.</td></tr>
            </c:if>
            </tbody>
        </table>
    </section>

    <section class="panel">
        <div class="panel-header">
            <h2 class="panel-title">Recent Audit Activity</h2>
            <a class="link" href="${pageContext.request.contextPath}/admin/audit">Open audit log</a>
        </div>
        <table>
            <thead><tr><th>Time</th><th>User</th><th>Action</th><th>Outcome</th><th>Details</th></tr></thead>
            <tbody>
            <c:forEach var="entry" items="${recentAudit}">
                <tr>
                    <td>${entry.timestamp}</td>
                    <td>${entry.username}</td>
                    <td>${entry.action}</td>
                    <td><span class="badge ${entry.outcome == 'FAILED' ? 'danger' : 'ok'}">${entry.outcome}</span></td>
                    <td>${entry.details}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentAudit}">
                <tr><td colspan="5">No audit activity yet.</td></tr>
            </c:if>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
