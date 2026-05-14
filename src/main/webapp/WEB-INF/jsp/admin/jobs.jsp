<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Job Postings - Admin Portal</title>
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
        .panel { background: white; border-radius: 14px; border: 1px solid #e2e8f0; box-shadow: 0 4px 12px rgba(0,0,0,.04); padding: 24px; margin-bottom: 24px; }
        .toolbar { display: grid; grid-template-columns: repeat(5, minmax(130px,1fr)); gap: 12px; align-items: end; margin-bottom: 18px; }
        label { display: block; font-size: 12px; font-weight: 700; color: #64748b; margin-bottom: 6px; text-transform: uppercase; }
        select { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 10px; background: white; }
        .btn { border: none; border-radius: 10px; padding: 10px 14px; background: #2563eb; color: white; font-weight: 700; text-decoration: none; text-align: center; }
        .btn.ghost { background: #e2e8f0; color: #0f172a; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px 10px; text-align: left; border-bottom: 1px solid #e2e8f0; font-size: 14px; }
        th { color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: .05em; }
        .badge { display: inline-block; padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; background: #dbeafe; color: #1d4ed8; }
        @media (max-width: 900px) { .sidebar { position: static; width: 100%; min-height: auto; } .main { margin-left: 0; } .toolbar { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment</div><div class="role">Administrator Portal</div>
    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/workload">Workload Monitor</a>
    <a href="${pageContext.request.contextPath}/admin/jobs" class="active">Job Postings</a>
    <a href="${pageContext.request.contextPath}/admin/users">User Accounts</a>
    <a href="${pageContext.request.contextPath}/admin/audit">Audit Log</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>
<main class="main">
    <div class="topbar">
        <h1 class="page-title">All Job Postings</h1>
        <a class="btn" href="${pageContext.request.contextPath}/admin/jobs?status=${selectedStatus}&module=${selectedModule}&semester=${selectedSemester}&export=csv">Export CSV</a>
    </div>
    <section class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/admin/jobs">
            <div><label>Status</label><select name="status">
                <option value="">All statuses</option>
                <option value="OPEN" ${selectedStatus == 'OPEN' ? 'selected' : ''}>OPEN</option>
                <option value="CLOSED" ${selectedStatus == 'CLOSED' ? 'selected' : ''}>CLOSED</option>
                <option value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
            </select></div>
            <div><label>Module</label><select name="module"><option value="">All modules</option>
                <c:forEach var="option" items="${moduleOptions}">
                    <option value="${option}" ${option == selectedModule ? 'selected' : ''}>${option}</option>
                </c:forEach>
            </select></div>
            <div><label>Term</label><select name="semester"><option value="">All terms</option>
                <c:forEach var="option" items="${semesterOptions}">
                    <option value="${option}" ${option == selectedSemester ? 'selected' : ''}>${option}</option>
                </c:forEach>
            </select></div>
            <button class="btn" type="submit">Apply</button>
            <a class="btn ghost" href="${pageContext.request.contextPath}/admin/jobs">Clear</a>
        </form>
        <table>
            <thead><tr><th>Title</th><th>Owner</th><th>Module</th><th>Term</th><th>Deadline</th><th>Vacancies</th><th>Status</th></tr></thead>
            <tbody>
            <c:forEach var="job" items="${jobs}">
                <tr>
                    <td>${job.title}</td><td>${job.moName}</td><td>${job.moduleCode}</td><td>${job.semester}</td>
                    <td>${job.deadline}</td><td>${job.filledCount} / ${job.vacancies}</td><td><span class="badge">${job.status}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty jobs}"><tr><td colspan="7">No jobs match the filters.</td></tr></c:if>
            </tbody>
        </table>
    </section>
</main>
</body>
</html>
