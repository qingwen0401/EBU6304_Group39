<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Applications - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9fafb; }
        .sidebar {
            width: 120px; height: 100vh; background: #f0f0f0;
            padding: 20px 10px; position: fixed; left: 0; top: 0;
        }
        .sidebar p { font-size: 12px; font-weight: bold; margin-bottom: 15px; }
        .sidebar a { display: block; text-decoration: none; color: #333; font-size: 12px; margin: 8px 0; }
        .sidebar a.active { font-weight: bold; color: #0066cc; }
        .sidebar a:hover { color: #0066cc; }
        .logout-link { color: #dc2626 !important; margin-top: 20px; }
        .main { margin-left: 120px; padding: 20px; }
        .breadcrumb { font-size: 12px; color: #666; margin-bottom: 15px; }
        .tab-buttons { margin-bottom: 15px; }
        .tab-buttons button {
            padding: 8px 16px; margin-right: 5px; border: 1px solid #ddd;
            background: white; cursor: pointer; border-radius: 4px; font-size: 13px;
        }
        .tab-buttons button.active { background: #2563eb; color: white; border-color: #2563eb; }
        .applications-table {
            width: 100%; background: white; border-collapse: collapse;
            border-radius: 6px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .applications-table th, .applications-table td {
            padding: 12px; text-align: left; font-size: 13px; border-bottom: 1px solid #eee;
        }
        .applications-table th { background: #f8f9fa; color: #495057; }
        .status { padding: 4px 8px; border-radius: 4px; color: white; font-size: 12px; }
        .status-PENDING { background: #f59e0b; }
        .status-REVIEWING { background: #2563eb; }
        .status-ACCEPTED { background: #22c55e; }
        .status-REJECTED { background: #ef4444; }
        .status-WITHDRAWN { background: #94a3b8; }
        .btn-withdraw {
            background: #ef4444; color: white; border: none; padding: 4px 10px;
            border-radius: 3px; cursor: pointer; font-size: 12px;
        }
        .btn-withdraw:hover { background: #dc2626; }
        .empty-msg { color: #94a3b8; font-size: 13px; padding: 20px; text-align: center; }
        .toast {
            position: fixed; bottom: 20px; right: 20px; padding: 12px 20px;
            border-radius: 6px; color: white; font-size: 13px; z-index: 2000; display: none;
        }
        .toast.success { background: #22c55e; }
        .toast.error { background: #ef4444; }
    </style>
</head>
<body>
<div class="sidebar">
    <p>Navigation</p>
    <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/applications" class="active">My Applications</a>
    <a href="${pageContext.request.contextPath}/ta/profile">My Profile</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<div class="main">
    <div class="breadcrumb">Home &gt; My Applications</div>

    <div class="tab-buttons">
        <button class="active" id="btnActive" onclick="switchTab('active')">Active Applications</button>
        <button id="btnHistory" onclick="switchTab('history')">Application History</button>
    </div>

    <!-- Active Applications -->
    <div id="activeSection">
        <c:choose>
            <c:when test="${empty activeApps}">
                <div class="empty-msg">No active applications.
                    <a href="${pageContext.request.contextPath}/ta/jobs">Browse jobs</a> to apply.</div>
            </c:when>
            <c:otherwise>
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
            </c:otherwise>
        </c:choose>
    </div>

    <!-- History Applications -->
    <div id="historySection" style="display:none;">
        <c:choose>
            <c:when test="${empty historyApps}">
                <div class="empty-msg">No application history yet.</div>
            </c:when>
            <c:otherwise>
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
                            <td style="color:#64748b;font-size:12px;">${not empty app.reviewNote ? app.reviewNote : '-'}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
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

        var formData = new FormData();
        formData.append('applicationId', applicationId);

        fetch('${pageContext.request.contextPath}/ta/applications', {
            method: 'POST',
            body: formData
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
