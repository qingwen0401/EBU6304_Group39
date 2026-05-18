<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ebu6304.recruitment.models.Application" %>
<%@ page import="com.ebu6304.recruitment.models.JobPosting" %>
<%@ page import="com.ebu6304.recruitment.models.User" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TA Dashboard - Overview</title>
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
            color: #dbeafe;
            text-decoration: none;
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

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
        }

        .breadcrumb {
            font-size: 12px;
            color: #64748b;
        }

        .user-box {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #475569;
            font-size: 13px;
            font-weight: 600;
        }

        .user-avatar {
            width: 36px;
            height: 36px;
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            color: white;
            font-weight: 700;
            box-shadow: 0 6px 16px rgba(37, 99, 235, 0.25);
        }

        .hero {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 28px 30px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
            margin-bottom: 22px;
        }

        .alert-bar {
            background: #fff7ed;
            color: #9a3412;
            padding: 14px 16px;
            border: 1px solid #fed7aa;
            border-radius: 12px;
            margin-bottom: 20px;
            font-size: 14px;
            display: flex;
            justify-content: space-between;
            gap: 12px;
            align-items: center;
        }

        .alert-bar a {
            color: #c2410c;
            font-weight: 700;
            text-decoration: none;
        }

        .alert-bar a:hover {
            text-decoration: underline;
        }

        .alert-bar .close {
            cursor: pointer;
            font-weight: 700;
            color: #9a3412;
        }

        .welcome-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            flex-wrap: wrap;
        }

        .welcome-text h1 {
            font-size: 30px;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .welcome-text p {
            color: #64748b;
            font-size: 14px;
            line-height: 1.7;
        }

        .btn-browse {
            background: #2563eb;
            color: white;
            border: none;
            padding: 12px 18px;
            border-radius: 10px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 700;
            text-decoration: none;
            display: inline-block;
            transition: 0.2s ease;
            white-space: nowrap;
        }

        .btn-browse:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }

        .stats-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 18px;
            margin-bottom: 24px;
        }

        .stat-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            padding: 22px;
            box-shadow: 0 8px 22px rgba(15, 23, 42, 0.05);
        }

        .stat-card .label {
            font-size: 13px;
            color: #64748b;
            margin-bottom: 8px;
            font-weight: 600;
        }

        .stat-card .number {
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
        }

        .content-row {
            display: grid;
            grid-template-columns: 1.6fr 1fr;
            gap: 22px;
        }

        .section {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 22px;
            box-shadow: 0 8px 22px rgba(15, 23, 42, 0.05);
        }

        .section h3 {
            font-size: 18px;
            color: #0f172a;
            margin-bottom: 16px;
        }

        .table-wrap {
            overflow-x: auto;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
        }

        .applications-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
            min-width: 520px;
        }

        .applications-table th,
        .applications-table td {
            padding: 13px 14px;
            text-align: left;
            border-bottom: 1px solid #eef2f7;
        }

        .applications-table th {
            color: #64748b;
            background: #f8fafc;
            font-weight: 700;
        }

        .applications-table tbody tr {
            cursor: pointer;
            transition: 0.2s ease;
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

        .status-PENDING { background: #eab308; }
        .status-ACCEPTED { background: #22c55e; }
        .status-REJECTED { background: #ef4444; }
        .status-WITHDRAWN { background: #94a3b8; }

        .job-list {
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .job-card {
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            padding: 16px;
            background: #f8fafc;
            transition: 0.2s ease;
            cursor: pointer;
        }

        .job-card:hover {
            background: #eff6ff;
            border-color: #bfdbfe;
        }

        .job-card h4 {
            font-size: 16px;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .job-card p {
            color: #64748b;
            font-size: 13px;
            line-height: 1.7;
        }

        .job-card .deadline {
            color: #dc2626;
            font-size: 12px;
            font-weight: 700;
            margin-top: 6px;
        }

        .empty-msg {
            color: #64748b;
            font-size: 14px;
            padding: 18px;
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

        /* 限制列表高度，增加内部滚动条 */
        .notification-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-height: 320px; /* 【关键】超过 3 条左右就会出现滚动条 */
            overflow-y: auto;
            padding-right: 5px;
            margin-bottom: 10px;
        }

        /* 美化滚动条（可选，让界面更精致） */
        .notification-list::-webkit-scrollbar { width: 5px; }
        .notification-list::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }

        /* 历史记录切换按钮样式 */
        .history-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            cursor: pointer;
            padding: 8px 0;
            border-bottom: 1px solid #e2e8f0;
            margin: 15px 0 10px 0;
            color: #64748b;
            transition: color 0.2s;
        }
        .history-header:hover { color: #2563eb; }
        .history-header i { font-size: 12px; transition: transform 0.3s; }
        .history-header.collapsed i { transform: rotate(-90deg); } /* 折叠时旋转箭头 */

        .notification-item {
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 14px 16px;
            background: #f8fafc;
        }

        .notification-title {
            font-size: 14px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 6px;
        }

        .notification-message {
            font-size: 13px;
            color: #475569;
            line-height: 1.6;
        }

        .notification-time {
            margin-top: 8px;
            font-size: 12px;
            color: #94a3b8;
        }

     /*   .notification-count {
            background: #dc2626;
            color: white;
            border-radius: 999px;
            padding: 3px 9px;
            font-size: 12px;
            margin-left: 8px;
        }*/


        /* 通知角标样式 */
        .badge-unread { background: #dc2626; color: white; border-radius: 999px; padding: 3px 9px; font-size: 12px; margin-left: 8px; display: inline-block; }
        .badge-read { background: #22c55e; color: white; border-radius: 999px; padding: 3px 9px; font-size: 12px; margin-left: 8px; display: inline-block; }

        /* 通知历史标题 */
        .history-title { font-size: 13px; font-weight: 700; color: #64748b; margin: 20px 0 10px 0; border-bottom: 1px solid #e2e8f0; padding-bottom: 6px; text-transform: uppercase;}

        /* 未读状态（左侧蓝边，带红点） */
        .notification-item.unread {
            background-color: #ffffff;
            border-left: 4px solid #2563eb;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
            transition: all 0.3s ease;
        }
        .notification-item.unread .notification-title::before {
            content: ""; display: inline-block; width: 8px; height: 8px; background-color: #dc2626; border-radius: 50%; margin-right: 8px; vertical-align: middle;
        }

        /* 已读状态（左侧绿边，底色变灰） */
        .notification-item.read {
            background-color: #f8fafc;
            border-left: 4px solid #22c55e;
            opacity: 0.7;
            transition: all 0.3s ease;
        }

        @media (max-width: 1100px) {
            .content-row {
                grid-template-columns: 1fr;
            }

            .stats-row {
                grid-template-columns: 1fr;
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

            .hero,
            .section,
            .stat-card {
                padding: 18px;
            }

            .welcome-text h1 {
                font-size: 24px;
            }

            .topbar {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment System</div>
    <div class="nav-title">Navigation</div>

    <a href="${pageContext.request.contextPath}/ta/dashboard" class="active">Dashboard</a>
    <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
    <a href="${pageContext.request.contextPath}/ta/profile">My Profile</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<div class="main">
    <div class="topbar">
        <div class="breadcrumb">Home &gt; Dashboard</div>
        <div class="user-box">
            <div class="user-avatar">${currentUser.username.substring(0,1).toUpperCase()}</div>
            <span>${currentUser.username}</span>
        </div>
    </div>

    <div class="hero">
        <div class="alert-bar" id="alertBar">
            <span>
                Complete your profile to increase your chances of selection.
                <a href="${pageContext.request.contextPath}/ta/profile?action=edit">Update Profile</a>
            </span>
            <span class="close" onclick="document.getElementById('alertBar').style.display='none'">×</span>
        </div>

        <div class="welcome-row">
            <div class="welcome-text">
                <h1>Welcome back, ${currentUser.username}!</h1>
                <p>
                    Track your applications, discover new opportunities,
                    and manage your TA recruitment progress from one place.
                </p>
            </div>
            <a href="${pageContext.request.contextPath}/ta/jobs" class="btn-browse">Browse All Jobs</a>
        </div>
    </div>

    <div class="stats-row">
        <div class="stat-card">
            <div class="label">Active Applications</div>
            <div class="number">${activeCount}</div>
        </div>
        <div class="stat-card">
            <div class="label">Total Applications</div>
            <div class="number">${totalCount}</div>
        </div>
        <div class="stat-card">
            <div class="label">Open Positions</div>
            <div class="number">${openJobCount}</div>
        </div>
    </div>

    <div class="section" style="margin-bottom: 22px;"> <h3> Notifications
        <span id="unreadBadge" class="badge-unread" style="${unreadCount == 0 ? 'display:none;' : ''}">${unreadCount} unread</span>
        <span id="readBadge" class="badge-read" style="${readCount == 0 ? 'display:none;' : ''}">${readCount} read</span>
    </h3>

        <div class="notification-list" id="unreadList">
            <c:if test="${empty unreadNotifs}">
                <p class="empty-msg" id="emptyUnreadMsg">You are all caught up!</p>
            </c:if>
            <c:forEach var="notif" items="${unreadNotifs}">
                <div class="notification-item unread" data-id="${notif.notificationId}" style="cursor:pointer;">
                    <div class="notification-title">${notif.title}</div>
                    <div class="notification-message">${notif.message}</div>
                    <div class="notification-time">${notif.createdAt.substring(0, 16)}</div>
                </div>
            </c:forEach>
        </div>

        <div class="history-header" onclick="toggleHistory()">
    <span class="history-title" style="margin:0; border:none; padding:0;">
        Notification History
        <small style="font-weight: normal; color: #94a3b8; font-size: 11px; margin-left: 4px;">(Click to view)</small>
    </span>
            <span id="historyToggleBtn">Show ▼</span>
        </div>

        <div id="historyWrapper" style="display: none;">
            <div class="notification-list" id="readList">
                <c:if test="${empty readNotifs}">
                    <p class="empty-msg" id="emptyReadMsg">No notification history.</p>
                </c:if>
                <c:forEach var="notif" items="${readNotifs}">
                    <div class="notification-item read" data-id="${notif.notificationId}">
                        <div class="notification-title">${notif.title}</div>
                        <div class="notification-message">${notif.message}</div>
                        <div class="notification-time">${notif.createdAt.substring(0, 16)}</div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <div class="content-row">
        <div class="section">
            <h3>Recent Applications</h3>
            <c:choose>
                <c:when test="${empty recentApps}">
                    <p class="empty-msg">
                        No applications yet.
                        <a href="${pageContext.request.contextPath}/ta/jobs">Browse jobs</a> to get started.
                    </p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="applications-table">
                            <thead>
                            <tr>
                                <th>Job Title</th> <th>Date Applied</th>
                                <th>Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${recentApps}">
                                <tr onclick="location.href='${pageContext.request.contextPath}/ta/applications'">

                                    <td>${app.jobTitle}</td>

                                    <td>${app.appliedAt != null ? app.appliedAt.toString().substring(0,10) : '-'}</td>
                                    <td><span class="status status-${app.status}">${app.status}</span></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="section">
            <h3>New Postings for You</h3>
            <c:choose>
                <c:when test="${empty recommendedJobs}">
                    <p class="empty-msg">No open positions at the moment.</p>
                </c:when>
                <c:otherwise>
                    <div class="job-list">
                        <c:forEach var="job" items="${recommendedJobs}">
                            <div class="job-card" onclick="location.href='${pageContext.request.contextPath}/ta/jobs'">
                                <h4>${job.title}</h4>
                                <p>${job.moduleCode} | ${job.hoursPerWeek != null ? job.hoursPerWeek : '?'} hrs/week</p>
                                <p class="deadline">Deadline: ${job.deadline != null ? job.deadline.toString().substring(0,10) : 'Open'}</p>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // 利用事件委托监听未读列表的点击事件
        const unreadList = document.getElementById('unreadList');
        const readList = document.getElementById('readList');

        if (unreadList) {
            unreadList.addEventListener('click', function(e) {
                // 确保点中的是未读通知卡片
                const item = e.target.closest('.notification-item.unread');
                if (!item) return;

                const notifId = item.getAttribute('data-id');
                if (!notifId) return;

                fetch('${pageContext.request.contextPath}/notifications/read', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ 'notificationId': notifId })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            // 1. 修改卡片样式，去掉鼠标指针
                            item.classList.remove('unread');
                            item.classList.add('read');
                            item.style.cursor = 'default';

                            // 2. 将卡片从当前位置移动到历史记录的【最顶端】
                            const emptyRead = document.getElementById('emptyReadMsg');
                            if (emptyRead) emptyRead.style.display = 'none'; // 隐藏"暂无历史"提示
                            readList.insertBefore(item, readList.firstChild);

                            // 如果历史记录当前是隐藏状态（display 为 none），自动调用 toggleHistory 展开它
                            const wrapper = document.getElementById('historyWrapper');
                            if (wrapper && wrapper.style.display === 'none') {
                                toggleHistory();
                            }

                            // 3. 检查未读列表是否空了，空了就显示"全部已读"提示
                            if (unreadList.querySelectorAll('.notification-item').length === 0) {
                                let emptyUnread = document.getElementById('emptyUnreadMsg');
                                if (!emptyUnread) {
                                    unreadList.innerHTML = '<p class="empty-msg" id="emptyUnreadMsg" style="margin:0;">You are all caught up!</p>';
                                } else {
                                    emptyUnread.style.display = 'block';
                                }
                            }

                            // 4. 动态更新顶部的红绿数字角标
                            const unreadBadge = document.getElementById('unreadBadge');
                            const readBadge = document.getElementById('readBadge');

                            if (unreadBadge) {
                                let unreadCount = parseInt(unreadBadge.innerText);
                                if (!isNaN(unreadCount) && unreadCount > 0) {
                                    unreadCount--;
                                    unreadBadge.innerText = unreadCount + ' unread';
                                    if (unreadCount === 0) unreadBadge.style.display = 'none';
                                }
                            }

                            if (readBadge) {
                                let readCount = parseInt(readBadge.innerText) || 0;
                                readCount++;
                                readBadge.innerText = readCount + ' read';
                                readBadge.style.display = 'inline-block';
                            }
                        }
                    })
                    .catch(err => console.error('Error:', err));
            });
        }
    });
    function toggleHistory() {
        const wrapper = document.getElementById('historyWrapper');
        const btn = document.getElementById('historyToggleBtn');
        if (wrapper.style.display === 'none') {
            wrapper.style.display = 'block';
            btn.innerText = 'Hide ▲';
        } else {
            wrapper.style.display = 'none';
            btn.innerText = 'Show ▼';
        }
    }
</script>
</body>
</html>