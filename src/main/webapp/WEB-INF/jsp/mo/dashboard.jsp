<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>MO Dashboard - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9fafb; }
        .navbar {
            background: #1e40af; color: white; padding: 14px 30px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .navbar h1 { font-size: 18px; }
        .navbar a { color: #bfdbfe; font-size: 13px; text-decoration: none; }
        .navbar a:hover { color: white; }
        .container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
        .welcome { font-size: 22px; color: #1e293b; margin-bottom: 8px; }
        .subtitle { color: #64748b; font-size: 14px; margin-bottom: 30px; }
        .card {
            background: white; border: 1px solid #e2e8f0; border-radius: 8px;
            padding: 30px; text-align: center; color: #64748b; font-size: 15px;
        }
    </style>
</head>
<body>
<div class="navbar">
    <h1>TA Recruitment System — Module Organiser</h1>
    <a href="${pageContext.request.contextPath}/logout">Sign Out</a>
</div>
<div class="container">
    <div class="welcome">Welcome, ${currentUser.fullName}!</div>
    <div class="subtitle">Module Organiser Dashboard</div>
    <div class="card">
        MO features (post jobs, review applications) are coming soon.<br>
        Your account is active and ready.
    </div>
</div>
</body>
</html>