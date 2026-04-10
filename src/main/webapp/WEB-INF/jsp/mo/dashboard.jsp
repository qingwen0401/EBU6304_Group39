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

        .navbar {
            background: linear-gradient(135deg, #1e40af, #2563eb);
            color: white;
            padding: 16px 36px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        .navbar h1 {
            font-size: 20px;
            font-weight: 700;
        }

        .navbar a {
            color: #dbeafe;
            font-size: 14px;
            text-decoration: none;
            font-weight: 600;
        }

        .navbar a:hover {
            color: white;
        }

        .container {
            max-width: 1200px;
            margin: 36px auto;
            padding: 0 24px;
        }

        .hero {
            background: white;
            border-radius: 16px;
            padding: 28px 32px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
            margin-bottom: 28px;
        }

        .welcome {
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .subtitle {
            color: #64748b;
            font-size: 15px;
            line-height: 1.6;
            margin-bottom: 22px;
        }

        .actions {
            display: flex;
            gap: 14px;
            flex-wrap: wrap;
        }

        .action-btn {
            display: inline-block;
            padding: 12px 20px;
            border-radius: 10px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 700;
            transition: 0.2s ease;
        }

        .action-btn.primary {
            background: #2563eb;
            color: white;
        }

        .action-btn.primary:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }

        .action-btn.secondary {
            background: #eff6ff;
            color: #1d4ed8;
            border: 1px solid #bfdbfe;
        }

        .action-btn.secondary:hover {
            background: #dbeafe;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
        }

        .card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            padding: 24px;
            box-shadow: 0 6px 18px rgba(15, 23, 42, 0.05);
            min-height: 190px;
        }

        .card h3 {
            font-size: 18px;
            margin-bottom: 12px;
            color: #0f172a;
        }

        .card p {
            color: #64748b;
            font-size: 14px;
            line-height: 1.7;
        }

        .tag {
            display: inline-block;
            margin-top: 14px;
            padding: 6px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
            background: #ecfeff;
            color: #0f766e;
        }

        @media (max-width: 960px) {
            .grid {
                grid-template-columns: 1fr;
            }

            .welcome {
                font-size: 24px;
            }

            .navbar {
                padding: 14px 20px;
            }

            .container {
                padding: 0 16px;
            }
        }
    </style>
</head>
<body>
<div class="navbar">
    <h1>TA Recruitment System — Module Organiser</h1>
    <a href="${pageContext.request.contextPath}/logout">Sign Out</a>
</div>

<div class="container">
    <div class="hero">
        <div class="welcome">Welcome, ${currentUser.fullName}!</div>
        <div class="subtitle">
            Manage TA recruitment tasks for your module, create job postings,
            and review recruitment progress from one place.
        </div>

        <div class="actions">
            <a class="action-btn primary" href="${pageContext.request.contextPath}/mo/create-job">
                Create Job Posting
            </a>
            <a class="action-btn secondary" href="${pageContext.request.contextPath}/mo/dashboard">
                Refresh Dashboard
            </a>
        </div>
    </div>

    <div class="grid">
        <div class="card">
            <h3>Post New Jobs</h3>
            <p>
                Create a new TA recruitment post with module details, required skills,
                deadline, payment information, and vacancy settings.
            </p>
            <span class="tag">Core Feature</span>
        </div>

        <div class="card">
            <h3>Manage Recruitment</h3>
            <p>
                Review your published positions and track the status of ongoing recruitment.
                This area is designed to support future applicant screening workflows.
            </p>
            <span class="tag">In Progress</span>
        </div>

        <div class="card">
            <h3>Account Status</h3>
            <p>
                Your Module Organiser account is active. You can now create and manage
                TA job postings for your module through the dashboard.
            </p>
            <span class="tag">Active</span>
        </div>
    </div>
</div>
</body>
</html>