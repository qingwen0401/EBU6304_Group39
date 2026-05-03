<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Applications - TA Recruitment System</title>
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
            width: 230px;
            min-height: 100vh;
            background: linear-gradient(180deg, #1e3a8a, #1d4ed8);
            padding: 28px 18px;
            position: fixed;
            left: 0;
            top: 0;
            color: white;
            box-shadow: 4px 0 18px rgba(15, 23, 42, 0.08);
        }

        .sidebar .brand {
            font-size: 20px;
            font-weight: 700;
            line-height: 1.3;
            margin-bottom: 28px;
        }

        .sidebar .nav-title {
            font-size: 12px;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: #bfdbfe;
            margin-bottom: 14px;
            font-weight: 700;
        }

        .sidebar a {
            display: block;
            text-decoration: none;
            color: #dbeafe;
            font-size: 14px;
            margin: 8px 0;
            padding: 11px 14px;
            border-radius: 10px;
            transition: 0.2s ease;
        }

        .sidebar a:hover {
            background: rgba(255, 255, 255, 0.12);
            color: white;
        }

        .sidebar a.active {
            background: white;
            color: #1d4ed8;
            font-weight: 700;
        }

        .sidebar .logout-link {
            margin-top: 22px;
            color: #fecaca;
        }

        .sidebar .logout-link:hover {
            background: rgba(239, 68, 68, 0.18);
            color: white;
        }

        .main {
            margin-left: 230px;
            padding: 32px;
        }

        .page-header {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 26px 28px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
            margin-bottom: 22px;
        }

        .breadcrumb {
            font-size: 12px;
            color: #64748b;
            margin-bottom: 10px;
        }

        .page-title {
            font-size: 30px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .page-subtitle {
            color: #64748b;
            font-size: 14px;
            line-height: 1.7;
        }

        .content-panel {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 24px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
        }

        .tab-buttons {
            margin-bottom: 18px;
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .tab-buttons button {
            padding: 10px 18px;
            border: 1px solid #cbd5e1;
            background: #f8fafc;
            cursor: pointer;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 700;
            color: #334155;
            transition: 0.2s ease;
        }

        .tab-buttons button:hover {
            background: #eff6ff;
            border-color: #93c5fd;
            color: #1d4ed8;
        }

        .tab-buttons button.active {
            background: #2563eb;
            color: white;
            border-color: #2563eb;
            box-shadow: 0 6px 18px rgba(37, 99, 235, 0.22);
        }

        .table-wrap {
            overflow-x: auto;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
        }

        .applications-table {
            width: 100%;
            background: white;
            border-collapse: collapse;
            min-width: 760px;
        }

        .applications-table th,
        .applications-table td {
            padding: 14px 16px;
            text-align: left;
            font-size: 14px;
            border-bottom: 1px solid #eef2f7;
            vertical-align: middle;
        }

        .applications-table th {
            background: #f8fafc;
            color: #475569;
            font-weight: 700;
        }

        .applications-table tbody tr:hover {
            background: #f8fbff;
        }

        .status {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 999px;
            color: white;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 0.02em;
        }

        .status-PENDING { background: #f59e0b; }
        .status-ACCEPTED { background: #22c55e; }
        .status-REJECTED { background: #ef4444; }
        .status-WITHDRAWN { background: #94a3b8; }

        .btn-withdraw {
            background: #ef4444;
            color: white;
            border: none;
            padding: 7px 12px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 12px;
            font-weight: 700;
            transition: 0.2s ease;
        }

        .btn-withdraw:hover {
            background: #dc2626;
        }

        .empty-msg {
            color: #64748b;
            font-size: 14px;
            padding: 26px 20px;
            text-align: center;
            border: 1px dashed #cbd5e1;
            border-radius: 14px;
            background: #f8fafc;
        }

        .empty-msg a {
            color: #2563eb;
            text-decoration: none;
            font-weight: 700;
        }

        .empty-msg a:hover {
            text-decoration: underline;
        }

        .toast {
            position: fixed;
            bottom: 24px;
            right: 24px;
            padding: 13px 20px;
            border-radius: 12px;
            color: white;
            font-size: 14px;
            font-weight: 700;
            z-index: 2000;
            display: none;
            box-shadow: 0 10px 24px rgba(15, 23, 42, 0.18);
        }

        .toast.success { background: #22c55e; }
        .toast.error { background: #ef4444; }

        @media (max-width: 1024px) {
            .sidebar {
                width: 200px;
            }

            .main {
                margin-left: 200px;
                padding: 24px;
            }

            .page-title {
                font-size: 26px;
            }
        }

        @media (max-width: 768px) {
            .sidebar {
                position: static;
                width: 100%;
                min-height: auto;
                border-radius: 0 0 18px 18px;
            }

            .main {
                margin-left: 0;
                padding: 18px;
            }

            .page-header,
            .content-panel {
                padding: 18px;
            }

            .page-title {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment System</div>
    <div class="nav-title">Navigation</div>

    <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/applications" class="active">My Applications</a>
    <a href="${pageContext.request.contextPath}/ta/profile">My Profile</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<div class="main">
    <div class="page-header">
        <div class="breadcrumb">Home &gt; My Applications</div>
        <div class="page-title">My Applications</div>
        <div class="page-subtitle">
            Track your active applications, review final outcomes, and manage pending submissions from one place.
        </div>
    </div>

    <div class="content-panel">
        <div class="tab-buttons">
            <button class="active" id="btnActive" onclick="switchTab('active')">Active Applications</button>
            <button id="btnHistory" onclick="switchTab('history')">Application History</button>
        </div>

        <div id="activeSection">
            <c:choose>
                <c:when test="${empty activeApps}">
                    <div class="empty-msg">
                        No active applications.
                        <a href="${pageContext.request.contextPath}/ta/jobs">Browse jobs</a> to apply.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="applications-table">
                            <thead>
                            <tr>
                                <th>Job ID</th>
                                <th>Applied Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${activeApps}">
                                <tr>
                                    <td>${app.jobId}</td>
                                    <td>${not empty app.appliedAt ? (app.appliedAt.length() >= 10 ? app.appliedAt.substring(0,10) : app.appliedAt) : '-'}</td>
                                    <td><span class="status status-${app.status}">${app.status}</span></td>
                                    <td>
                                        <c:if test="${app.status == 'PENDING'}">
                                            <button class="btn-withdraw" onclick="withdrawApp('${app.applicationId}')">Withdraw</button>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div id="historySection" style="display:none;">
            <c:choose>
                <c:when test="${empty historyApps}">
                    <div class="empty-msg">No application history yet.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="applications-table">
                            <thead>
                            <tr>
                                <th>Job ID</th>
                                <th>Applied Date</th>
                                <th>Outcome</th>
                                <th>Feedback</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${historyApps}">
                                <tr>
                                    <td>${app.jobId}</td>
                                    <td>${not empty app.appliedAt ? (app.appliedAt.length() >= 10 ? app.appliedAt.substring(0,10) : app.appliedAt) : '-'}</td>
                                    <td><span class="status status-${app.status}">${app.status}</span></td>
                                    <td style="color:#64748b;font-size:13px;">${not empty app.reviewNote ? app.reviewNote : '-'}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="toast" id="toast"></div>

<script>
    function switchTab(tab) {
        var activeSection = document.getElementById('activeSection');
        var historySection = document.getElementById('historySection');
        var btnActive = document.getElementById('btnActive');
        var btnHistory = document.getElementById('btnHistory');

        if (tab === 'active') {
            activeSection.style.display = 'block';
            historySection.style.display = 'none';
            btnActive.classList.add('active');
            btnHistory.classList.remove('active');
        } else {
            activeSection.style.display = 'none';
            historySection.style.display = 'block';
            btnActive.classList.remove('active');
            btnHistory.classList.add('active');
        }
    }

    function withdrawApp(applicationId) {
        if (!confirm('Are you sure you want to withdraw this application?')) return;

        var body = new URLSearchParams();
        body.append('applicationId', applicationId);

        fetch('${pageContext.request.contextPath}/ta/applications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: body.toString()
        })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                showToast(data.message, data.success ? 'success' : 'error');
                if (data.success) {
                    setTimeout(function() { location.reload(); }, 1500);
                }
            })
            .catch(function() {
                showToast('Network error. Please try again.', 'error');
            });
    }

    function showToast(msg, type) {
        var t = document.getElementById('toast');
        t.textContent = msg;
        t.className = 'toast ' + type;
        t.style.display = 'block';
        setTimeout(function() { t.style.display = 'none'; }, 3000);
    }
</script>
</body>
</html>