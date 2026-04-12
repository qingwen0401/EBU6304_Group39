<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workload Monitor - Admin Portal</title>
    <style>
        body { margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; display: flex; height: 100vh; }
        .sidebar { width: 250px; background-color: #2c3e50; color: white; display: flex; flex-direction: column; }
        .sidebar h2 { text-align: center; padding: 20px 0; margin: 0; border-bottom: 1px solid #34495e; font-size: 20px; }
        .sidebar a { padding: 15px 25px; text-decoration: none; color: #ecf0f1; display: block; transition: 0.3s; font-size: 15px;}
        .sidebar a:hover, .sidebar a.active { background-color: #34495e; border-left: 4px solid #3498db; }
        .main-content { flex: 1; display: flex; flex-direction: column; overflow-y: auto;}
        .topbar { background-color: white; padding: 15px 30px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .topbar h1 { margin: 0; font-size: 22px; color: #333; }

        .container { padding: 30px; }
        table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
        th { background-color: #f8f9fa; color: #333; font-weight: 600; }

        /* 状态徽章 */
        .badge { padding: 5px 10px; border-radius: 12px; font-size: 12px; font-weight: bold; color: white; }
        .status-IDLE { background-color: #95a5a6; }
        .status-NORMAL { background-color: #2ecc71; }
        .status-WARNING { background-color: #f39c12; }
        .status-OVERLOADED { background-color: #e74c3c; animation: blink 1.5s infinite; }

        @keyframes blink { 50% { opacity: 0.6; } }
    </style>
</head>
<body>

    <div class="sidebar">
        <h2>TA System</h2>
        <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard / Overview</a>
        <a href="${pageContext.request.contextPath}/admin/workload" class="active">Workload Monitor</a>
        <a href="#">Fairness Audit</a>
        <a href="#">System Logs</a>
    </div>

    <div class="main-content">
        <div class="topbar">
            <h1>TA Workload Monitor</h1>
        </div>

        <div class="container">
            <table>
                <thead>
                    <tr>
                        <th>Student Name</th>
                        <th>Student ID</th>
                        <th>Active Jobs</th>
                        <th>Weekly Hours</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ta" items="${workloadReport}">
                        <tr>
                            <td>${ta.taName}</td>
                            <td>${ta.studentId}</td>
                            <td>${ta.jobCount}</td>
                            <td style="${ta.isOverloaded ? 'color:#e74c3c; font-weight:bold;' : ''}">
                                ${ta.totalWeeklyHours} / 20 hrs
                            </td>
                            <td>
                                <span class="badge status-${ta.workloadStatus}">
                                    ${ta.workloadStatus}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>