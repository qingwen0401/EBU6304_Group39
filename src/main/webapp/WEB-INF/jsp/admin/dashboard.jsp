<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Portal - TA Recruitment System</title>
    <style>
        /* 基础页面设置，模仿原型图的现代感 */
        body { margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; display: flex; height: 100vh; }

        /* 左侧导航栏 */
        .sidebar { width: 250px; background-color: #2c3e50; color: white; display: flex; flex-direction: column; }
        .sidebar h2 { text-align: center; padding: 20px 0; margin: 0; border-bottom: 1px solid #34495e; font-size: 20px; font-weight: 600;}
        .sidebar a { padding: 15px 25px; text-decoration: none; color: #ecf0f1; display: block; transition: 0.3s; font-size: 15px;}
        .sidebar a:hover, .sidebar a.active { background-color: #34495e; border-left: 4px solid #3498db; }

        /* 右侧主体区域 */
        .main-content { flex: 1; display: flex; flex-direction: column; }

        /* 顶部栏 */
        .topbar { background-color: white; padding: 15px 30px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); display: flex; justify-content: space-between; align-items: center; }
        .topbar h1 { margin: 0; font-size: 22px; color: #333; font-weight: 600;}
        .user-info { font-weight: 500; color: #555; display: flex; align-items: center; }
        .logout-btn { background-color: #e74c3c; color: white; padding: 6px 12px; text-decoration: none; border-radius: 4px; font-size: 14px; margin-left: 15px; transition: 0.3s;}
        .logout-btn:hover { background-color: #c0392b; }

        /* 数据卡片区域 */
        .dashboard-container { padding: 30px; }
        .cards { display: flex; gap: 20px; }
        .card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); flex: 1; border-top: 4px solid #3498db; }
        .card h3 { margin: 0 0 10px 0; color: #7f8c8d; font-size: 16px; font-weight: normal; text-transform: uppercase; letter-spacing: 0.5px;}
        .card .number { margin: 0; font-size: 42px; font-weight: bold; color: #2c3e50; }
        .card .details { color: #95a5a6; font-size: 13px; margin-top: 10px; }
    </style>
</head>
<body>

    <div class="sidebar">
            <h2>TA System</h2>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Dashboard / Overview</a>
            <a href="${pageContext.request.contextPath}/admin/workload">Workload Monitor</a>
            <a href="#">Fairness Audit</a>
            <a href="#">System Logs</a>
        </div>

    <div class="main-content">

        <div class="topbar">
            <h1>Admin Portal</h1>
            <div class="user-info">
                Welcome, ${currentUser.username != null ? currentUser.username : 'Admin User'}!
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
            </div>
        </div>

        <div class="dashboard-container">
            <div class="cards">
                <div class="card">
                    <h3>Total System Users</h3>
                    <p class="number">${totalUsers}</p>
                    <p class="details">Includes ${taCount} TAs and ${moCount} MOs</p>
                </div>

                <div class="card" style="border-top-color: #2ecc71;">
                    <h3>Total Job Postings</h3>
                    <p class="number">${totalJobs}</p>
                    <p class="details">All jobs created by MOs</p>
                </div>
            </div>
        </div>

    </div>

</body>
</html>