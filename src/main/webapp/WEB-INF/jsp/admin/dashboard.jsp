<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9fafb; }
        .navbar {
            background: #7c3aed; color: white; padding: 14px 30px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .navbar h1 { font-size: 18px; }
        .navbar a { color: #ddd6fe; font-size: 13px; text-decoration: none; }
        .navbar a:hover { color: white; }
        .container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
        .welcome { font-size: 22px; color: #1e293b; margin-bottom: 8px; }
        .subtitle { color: #64748b; font-size: 14px; margin-bottom: 30px; }
        .stats { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-bottom: 30px; }
        .stat-card {
            background: white; border: 1px solid #e2e8f0; border-radius: 8px;
            padding: 24px; text-align: center;
        }
        .stat-card .number { font-size: 36px; font-weight: bold; color: #7c3aed; }
        .stat-card .label  { font-size: 13px; color: #64748b; margin-top: 6px; }
        .card {
            background: white; border: 1px solid #e2e8f0; border-radius: 8px;
            padding: 30px; text-align: center; color: #64748b; font-size: 15px;
        }
    </style>
</head>
<body>
<div class="navbar">
    <h1>TA Recruitment System — Administrator</h1>
    <a href="${pageContext.request.contextPath}/logout">Sign Out</a>
</div>
<div class="container">
    <div class="welcome">Welcome, ${currentUser.fullName}!</div>
    <div class="subtitle">System Administration Dashboard</div>

    <div class="stats">
        <div class="stat-card">
            <div class="number">${taCount}</div>
            <div class="label">Registered TAs</div>
        </div>
        <div class="stat-card">
            <div class="number">${moCount}</div>
            <div class="label">Module Organisers</div>
        </div>
    </div>

    <div class="card">
        Admin user management features are coming soon.
        <br>Default admin credentials: username <strong>admin</strong> / password <strong>admin123456</strong>
    </div>
</div>
</body>
</html>