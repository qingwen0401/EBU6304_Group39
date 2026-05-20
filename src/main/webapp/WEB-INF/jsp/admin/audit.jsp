<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Audit Log - Admin Portal</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f4f7fb; color: #1e293b; }
        .sidebar { width: 250px; min-height: 100vh; background: linear-gradient(180deg,#1e3a8a,#1d4ed8); padding: 28px 18px; position: fixed; left: 0; top: 0; color: white; }
        .brand { font-size: 18px; font-weight: 700; margin-bottom: 8px; }
        .role { font-size: 12px; color: #bfdbfe; margin-bottom: 28px; padding-bottom: 20px; border-bottom: 1px solid rgba(255,255,255,.1); }
        .nav-title { font-size: 11px; letter-spacing: .08em; text-transform: uppercase; color: #bfdbfe; margin: 20px 0 12px; font-weight: 700; }
        .sidebar a { display: flex; color: #dbeafe; text-decoration: none; font-size: 14px; margin: 6px 0; padding: 11px 14px; border-radius: 10px; }
        .sidebar a:hover { background: rgba(255,255,255,.12); color: white; }
        .sidebar a.active { background: white; color: #1d4ed8; font-weight: 700; }
        .logout-link { margin-top: 30px; color: #fecaca !important; border-top: 1px solid rgba(255,255,255,.1); padding-top: 20px !important; }
        .main { margin-left: 250px; padding: 32px; }
        .topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
        .page-title { font-size: 28px; font-weight: 700; color: #0f172a; }
        .panel { background: white; border-radius: 14px; border: 1px solid #e2e8f0; box-shadow: 0 4px 12px rgba(0,0,0,.04); padding: 24px; }
        .toolbar { display: grid; grid-template-columns: repeat(5, minmax(130px,1fr)); gap: 12px; align-items: end; margin-bottom: 18px; }
        label { display: block; font-size: 12px; font-weight: 700; color: #64748b; margin-bottom: 6px; text-transform: uppercase; }
        select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 10px; background: white; }
        .btn { border: none; border-radius: 10px; padding: 10px 14px; background: #2563eb; color: white; font-weight: 700; text-decoration: none; text-align: center; }
        .btn.ghost { background: #e2e8f0; color: #0f172a; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; vertical-align: top; }
        th { color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: .05em; }
        .badge { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; background: #dcfce7; color: #16a34a; }
        .badge.fail { background: #fee2e2; color: #dc2626; }
        @media (max-width: 900px) { .sidebar { position: static; width: 100%; min-height: auto; } .main { margin-left: 0; } .toolbar { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment</div><div class="role">Administrator Portal</div>
    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/workload">Workload Monitor</a>
    <a href="${pageContext.request.contextPath}/admin/jobs">Job Postings</a>
    <a href="${pageContext.request.contextPath}/admin/users">User Accounts</a>
    <a href="${pageContext.request.contextPath}/admin/audit" class="active">Audit Log</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>
<main class="main">
    <div class="topbar">
        <h1 class="page-title">Audit Log</h1>
        <a class="btn" href="${pageContext.request.contextPath}/admin/audit?action=${selectedAction}&role=${selectedRole}&outcome=${selectedOutcome}&export=csv">Export CSV</a>
    </div>
    <section class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/admin/audit">
            <div><label>Action</label><select name="action">
                <option value="">All actions</option><option value="LOGIN" ${selectedAction == 'LOGIN' ? 'selected' : ''}>LOGIN</option>
                <option value="CONFIG_UPDATE" ${selectedAction == 'CONFIG_UPDATE' ? 'selected' : ''}>CONFIG_UPDATE</option>
                <option value="USER_STATUS_UPDATE" ${selectedAction == 'USER_STATUS_UPDATE' ? 'selected' : ''}>USER_STATUS_UPDATE</option>
                <option value="MO_CREATED" ${selectedAction == 'MO_CREATED' ? 'selected' : ''}>MO_CREATED</option>
                <option value="MO_DELETED" ${selectedAction == 'MO_DELETED' ? 'selected' : ''}>MO_DELETED</option>
            </select></div>
            <div><label>Role</label><select name="role">
                <option value="">All roles</option><option value="TA" ${selectedRole == 'TA' ? 'selected' : ''}>TA</option>
                <option value="MO" ${selectedRole == 'MO' ? 'selected' : ''}>MO</option><option value="ADMIN" ${selectedRole == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                <option value="UNKNOWN" ${selectedRole == 'UNKNOWN' ? 'selected' : ''}>UNKNOWN</option>
            </select></div>
            <div><label>Outcome</label><select name="outcome">
                <option value="">All outcomes</option><option value="SUCCESS" ${selectedOutcome == 'SUCCESS' ? 'selected' : ''}>SUCCESS</option>
                <option value="FAILED" ${selectedOutcome == 'FAILED' ? 'selected' : ''}>FAILED</option>
            </select></div>
            <button class="btn" type="submit">Apply</button><a class="btn ghost" href="${pageContext.request.contextPath}/admin/audit">Clear</a>
        </form>
        <table>
            <thead><tr><th>Time</th><th>User</th><th>Role</th><th>Action</th><th>Outcome</th><th>IP</th><th>Details</th></tr></thead>
            <tbody>
            <c:forEach var="entry" items="${entries}">
                <tr>
                    <td>${entry.timestamp}</td><td>${entry.username}</td><td>${entry.role}</td><td>${entry.action}</td>
                    <td><span class="badge ${entry.outcome == 'FAILED' ? 'fail' : ''}">${entry.outcome}</span></td>
                    <td>${entry.ipAddress}</td><td>${entry.details}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty entries}"><tr><td colspan="7">No audit entries match the filters.</td></tr></c:if>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
