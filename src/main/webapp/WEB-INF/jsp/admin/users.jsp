<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Accounts - Admin Portal</title>
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
        .page-title { font-size: 28px; font-weight: 700; color: #0f172a; margin-bottom: 28px; }
        .panel { background: white; border-radius: 14px; border: 1px solid #e2e8f0; box-shadow: 0 4px 12px rgba(0,0,0,.04); padding: 24px; margin-bottom: 24px; }
        .toolbar { display: grid; grid-template-columns: repeat(4, minmax(130px,1fr)); gap: 12px; align-items: end; margin-bottom: 18px; }
        .mo-form { display: grid; grid-template-columns: repeat(4, minmax(150px,1fr)); gap: 14px; align-items: end; }
        label { display: block; font-size: 12px; font-weight: 700; color: #64748b; margin-bottom: 6px; text-transform: uppercase; }
        select, input { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 10px; background: white; }
        .btn { border: none; border-radius: 10px; padding: 10px 14px; background: #2563eb; color: white; font-weight: 700; text-decoration: none; cursor: pointer; }
        .btn.danger { background: #dc2626; }
        .btn.ghost { background: #e2e8f0; color: #0f172a; text-align: center; }
        .actions { display: flex; gap: 8px; flex-wrap: wrap; }
        .actions form { margin: 0; }
        .panel-title { font-size: 18px; color: #0f172a; font-weight: 700; margin-bottom: 6px; }
        .panel-subtitle { font-size: 13px; color: #64748b; margin-bottom: 18px; }
        .alert { padding: 12px 14px; border-radius: 10px; margin-bottom: 18px; font-weight: 700; font-size: 14px; }
        .alert.success { background: #dcfce7; color: #166534; }
        .alert.error { background: #fee2e2; color: #991b1b; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; }
        th { color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: .05em; }
        .badge { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; background: #dcfce7; color: #16a34a; }
        .badge.off { background: #fee2e2; color: #dc2626; }
        @media (max-width: 900px) { .sidebar { position: static; width: 100%; min-height: auto; } .main { margin-left: 0; } .toolbar, .mo-form { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment</div><div class="role">Administrator Portal</div>
    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/workload">Workload Monitor</a>
    <a href="${pageContext.request.contextPath}/admin/jobs">Job Postings</a>
    <a href="${pageContext.request.contextPath}/admin/users" class="active">User Accounts</a>
    <a href="${pageContext.request.contextPath}/admin/audit">Audit Log</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>
<main class="main">
    <h1 class="page-title">User Accounts</h1>
    <c:if test="${not empty message}">
        <div class="alert success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert error">${error}</div>
    </c:if>

    <section class="panel">
        <h2 class="panel-title">Add Module Organiser</h2>
        <p class="panel-subtitle">Create MO accounts only. Usernames and emails must be unique, and passwords must be at least 6 characters.</p>
        <form class="mo-form" method="post" action="${pageContext.request.contextPath}/admin/users">
            <input type="hidden" name="action" value="create-mo">
            <div>
                <label>Username</label>
                <input type="text" name="username" required maxlength="50">
            </div>
            <div>
                <label>Password</label>
                <input type="password" name="password" required minlength="6">
            </div>
            <div>
                <label>Email</label>
                <input type="email" name="email" required maxlength="120">
            </div>
            <div>
                <label>Full Name</label>
                <input type="text" name="fullName" required maxlength="100">
            </div>
            <div>
                <label>Department</label>
                <input type="text" name="department" required maxlength="100">
            </div>
            <div>
                <label>Module Code</label>
                <input type="text" name="moduleCode" required maxlength="30">
            </div>
            <div>
                <label>Module Name</label>
                <input type="text" name="moduleName" required maxlength="120">
            </div>
            <button class="btn" type="submit">Add MO</button>
        </form>
    </section>

    <section class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/admin/users">
            <div><label>Role</label><select name="role">
                <option value="">All roles</option><option value="TA" ${selectedRole == 'TA' ? 'selected' : ''}>TA</option>
                <option value="MO" ${selectedRole == 'MO' ? 'selected' : ''}>MO</option><option value="ADMIN" ${selectedRole == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
            </select></div>
            <div><label>Status</label><select name="status">
                <option value="">All statuses</option><option value="ACTIVE" ${selectedStatus == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                <option value="INACTIVE" ${selectedStatus == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
            </select></div>
            <button class="btn" type="submit">Apply</button><a class="btn ghost" href="${pageContext.request.contextPath}/admin/users">Clear</a>
        </form>
        <table>
            <thead><tr><th>Username</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.username}</td><td>${user.fullName}</td><td>${user.email}</td><td>${user.role}</td>
                    <td><span class="badge ${user.active ? '' : 'off'}">${user.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
                    <td>
                        <div class="actions">
                        <form method="post" action="${pageContext.request.contextPath}/admin/users">
                            <input type="hidden" name="userId" value="${user.userId}">
                            <c:choose>
                                <c:when test="${user.active}">
                                    <button class="btn danger" name="action" value="deactivate" type="submit">Deactivate</button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn" name="action" value="activate" type="submit">Activate</button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                        <c:if test="${user.role == 'MO'}">
                            <form method="post" action="${pageContext.request.contextPath}/admin/users"
                                  onsubmit="return confirm('Delete this MO account? Existing job records will remain for reporting.');">
                                <input type="hidden" name="userId" value="${user.userId}">
                                <button class="btn danger" name="action" value="delete-mo" type="submit">Delete MO</button>
                            </form>
                        </c:if>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty users}"><tr><td colspan="6">No users match the filters.</td></tr></c:if>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
