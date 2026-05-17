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

        .sidebar {
            width: 250px;
            min-height: 100vh;
            background: linear-gradient(180deg, #1e3a8a, #1d4ed8);
            padding: 28px 18px;
            position: fixed;
            left: 0;
            top: 0;
            color: white;
            box-shadow: 4px 0 18px rgba(15, 23, 42, 0.08);
            z-index: 1000;
        }

        .sidebar .brand {
            font-size: 18px;
            font-weight: 700;
            line-height: 1.3;
            margin-bottom: 8px;
        }

        .sidebar .role {
            font-size: 12px;
            color: #bfdbfe;
            margin-bottom: 28px;
            padding-bottom: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .sidebar .nav-title {
            font-size: 11px;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: #bfdbfe;
            margin: 20px 0 12px;
            font-weight: 700;
        }

        .sidebar a {
            display: flex;
            align-items: center;
            color: #dbeafe;
            text-decoration: none;
            font-size: 14px;
            margin: 6px 0;
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

        .sidebar a .icon {
            margin-right: 10px;
            font-size: 16px;
        }

        .sidebar .logout-link {
            margin-top: 30px;
            color: #fecaca;
            border-top: 1px solid rgba(255, 255, 255, 0.1);
            padding-top: 20px;
        }

        .sidebar .logout-link:hover {
            background: rgba(239, 68, 68, 0.18);
            color: white;
        }

        .main {
            margin-left: 250px;
            padding: 32px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .page-title {
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 10px 16px;
            background: white;
            border-radius: 10px;
            border: 1px solid #e2e8f0;
        }

        .user-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 700;
            font-size: 14px;
        }

        .user-name {
            font-size: 14px;
            font-weight: 600;
            color: #0f172a;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 32px;
        }

        .stat-card {
            background: white;
            padding: 24px;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
            transition: all 0.2s;
        }

        .stat-card:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
            transform: translateY(-2px);
        }

        .stat-label {
            font-size: 13px;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .stat-value {
            font-size: 36px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .stat-change {
            font-size: 12px;
            color: #64748b;
        }

        .stat-change.positive {
            color: #16a34a;
        }

        .stat-change.warning {
            color: #ea580c;
        }

        .content-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 24px;
            margin-bottom: 24px;
        }

        .panel {
            background: white;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
            padding: 24px;
        }

        .panel-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .panel-title {
            font-size: 18px;
            font-weight: 700;
            color: #0f172a;
        }

        .panel-action {
            font-size: 13px;
            color: #2563eb;
            text-decoration: none;
            font-weight: 600;
        }

        .panel-action:hover {
            color: #1d4ed8;
        }

        .job-card {
            padding: 16px;
            border: 1px solid #f1f5f9;
            border-radius: 10px;
            margin-bottom: 12px;
            transition: all 0.2s;
        }

        .job-card:hover {
            background: #f8fafc;
            border-color: #e2e8f0;
        }

        .job-card-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 10px;
        }

        .job-title {
            font-size: 15px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .job-module {
            font-size: 12px;
            color: #64748b;
        }

        .job-badge {
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
        }

        .badge-pending {
            background: #fef3c7;
            color: #92400e;
        }

        .badge-open {
            background: #d1fae5;
            color: #065f46;
        }

        .job-stats {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: #64748b;
            margin-top: 10px;
            flex-wrap: wrap;
        }

        .job-stat {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .empty-msg {
            color: #64748b;
            font-size: 14px;
            padding: 24px;
            border: 1px dashed #cbd5e1; /* 虚线框 */
            border-radius: 14px;
            background: #f8fafc; /* 浅灰色背景 */
            text-align: center;
            margin: 10px 0;
            width: 100%;
            display: block;
            box-sizing: border-box; /* 防止宽度撑破页面 */
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

        /* 美化滚动条（让界面更精致） */
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
        .application-item {
            padding: 14px;
            border-bottom: 1px solid #f1f5f9;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .application-item:last-child {
            border-bottom: none;
        }

        .application-info {
            flex: 1;
        }

        .applicant-name {
            font-size: 14px;
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .application-meta {
            font-size: 12px;
            color: #64748b;
        }

        .status-badge {
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
        }

        .status-pending {
            background: #fef3c7;
            color: #92400e;
        }

        .status-reviewing {
            background: #dbeafe;
            color: #1e40af;
        }

        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #94a3b8;
        }

        .empty-state-icon {
            font-size: 48px;
            margin-bottom: 12px;
            opacity: 0.5;
        }

        .quick-actions {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
            margin-bottom: 32px;
        }

        .quick-action-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            padding: 16px;
            background: white;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            text-decoration: none;
            color: #0f172a;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.2s;
        }

        .quick-action-btn:hover {
            border-color: #2563eb;
            background: #eff6ff;
            transform: translateY(-2px);
        }

        .quick-action-btn.primary {
            background: #2563eb;
            color: white;
            border-color: #2563eb;
        }

        .quick-action-btn.primary:hover {
            background: #1d4ed8;
        }

        .notification-list {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .notification-item {
            padding: 14px;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
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

        @media (max-width: 1200px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .content-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .sidebar {
                position: static;
                width: 100%;
                min-height: auto;
            }

            .main {
                margin-left: 0;
                padding: 20px;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .quick-actions {
                grid-template-columns: 1fr;
            }
        }

    </style>
</head>
<body>

<div class="sidebar">
    <div class="brand">TA Recruitment</div>
    <div class="role">Module Organiser Portal</div>

    <div class="nav-title">Main</div>
    <a href="${pageContext.request.contextPath}/mo/dashboard" class="active">
        <span class="icon">📊</span> Dashboard
    </a>
    <a href="${pageContext.request.contextPath}/mo/create-job">
        <span class="icon">➕</span> Create Job
    </a>

    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/mo/jobs">
        <span class="icon">📄</span> My Jobs
    </a>
    <a href="${pageContext.request.contextPath}/mo/applications">
        <span class="icon">📋</span> Applications
        <c:if test="${stats.pendingApplications > 0}">
            <span style="margin-left: auto; background: #fef3c7; color: #92400e; padding: 2px 8px; border-radius: 999px; font-size: 11px; font-weight: 700;">
                    ${stats.pendingApplications}
            </span>
        </c:if>
    </a>
    <a href="${pageContext.request.contextPath}/mo/analytics">
        <span class="icon">📈</span> Analytics
    </a>

    <div class="nav-title">Tools</div>
    <a href="${pageContext.request.contextPath}/mo/templates">
        <span class="icon">📝</span> Templates
    </a>

    <a href="${pageContext.request.contextPath}/logout" class="logout-link">
        <span class="icon">🚪</span> Sign Out
    </a>
</div>

<div class="main">
    <div class="topbar">
        <div class="page-title">Dashboard</div>
        <div class="user-info">
            <div class="user-avatar">${currentUser.fullName.substring(0, 1).toUpperCase()}</div>
            <div class="user-name">${currentUser.fullName}</div>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Total Applications</div>
            <div class="stat-value">${stats.totalApplications}</div>
            <div class="stat-change">
                <c:if test="${stats.pendingApplications > 0}">
                    <span class="warning">${stats.pendingApplications} pending review</span>
                </c:if>
                <c:if test="${stats.pendingApplications == 0}">
                    All caught up!
                </c:if>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Active Jobs</div>
            <div class="stat-value">${stats.openJobs}</div>
            <div class="stat-change">
                ${stats.totalJobs} total jobs posted
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Fill Rate</div>
            <div class="stat-value">${stats.fillRate}%</div>
            <div class="stat-change">
                ${stats.totalFilled} / ${stats.totalVacancies} positions filled
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Accepted</div>
            <div class="stat-value">${stats.acceptedApplications}</div>
            <div class="stat-change positive">
                ${stats.rejectedApplications} rejected
            </div>
        </div>
    </div>

    <div class="quick-actions">
        <a href="${pageContext.request.contextPath}/mo/create-job" class="quick-action-btn primary">
            <span>➕</span> Create New Job Posting
        </a>
        <a href="${pageContext.request.contextPath}/mo/applications" class="quick-action-btn">
            <span>📋</span> Review Applications
            <c:if test="${stats.pendingApplications > 0}">
                (${stats.pendingApplications})
            </c:if>
        </a>
        <a href="${pageContext.request.contextPath}/mo/analytics" class="quick-action-btn">
            <span>📈</span> View Analytics
        </a>
        <a href="${pageContext.request.contextPath}/mo/templates" class="quick-action-btn">
            <span>📝</span> Use Template
        </a>
    </div>

    <div class="panel" style="margin-bottom: 24px;">
        <div class="panel-header">
            <div class="panel-title">
                Notifications
                <span id="unreadBadge" class="badge-unread" style="${unreadCount == 0 ? 'display:none;' : ''}">${unreadCount} unread</span>
                <span id="readBadge" class="badge-read" style="${readCount == 0 ? 'display:none;' : ''}">${readCount} read</span>
            </div>
        </div>

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

    <div class="content-grid">
        <div class="panel">
            <div class="panel-header">
                <div class="panel-title">Jobs Needing Attention</div>
                <a href="${pageContext.request.contextPath}/mo/applications" class="panel-action">View All →</a>
            </div>

            <c:choose>
                <c:when test="${not empty jobsNeedingAttention}">
                    <c:forEach var="job" items="${jobsNeedingAttention}">
                        <div class="job-card">
                            <div class="job-card-header">
                                <div>
                                    <div class="job-title">${job.title}</div>
                                    <div class="job-module">${job.moduleCode} - ${job.moduleName}</div>
                                </div>
                                <span class="job-badge badge-pending">Pending</span>
                            </div>
                            <div class="job-stats">
                                <div class="job-stat">
                                    <span>📝</span>
                                    <c:set var="pendingCount" value="0"/>
                                    <c:forEach var="app" items="${recentApplications}">
                                        <c:if test="${app.jobId == job.jobId && app.status == 'PENDING'}">
                                            <c:set var="pendingCount" value="${pendingCount + 1}"/>
                                        </c:if>
                                    </c:forEach>
                                        ${pendingCount} new applications
                                </div>
                                <div class="job-stat">
                                    <span>👥</span>
                                        ${job.filledCount}/${job.vacancies} filled
                                </div>
                                <div class="job-stat">
                                    <span>📅</span>
                                    Deadline: ${job.deadline}
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-icon">✅</div>
                        <p>All jobs are up to date!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="panel">
            <div class="panel-header">
                <div class="panel-title">Recent Applications</div>
                <a href="${pageContext.request.contextPath}/mo/applications" class="panel-action">View All →</a>
            </div>

            <c:choose>
                <c:when test="${not empty recentApplications}">
                    <c:forEach var="app" items="${recentApplications}">
                        <div class="application-item">
                            <div class="application-info">
                                <div class="applicant-name">${app.taName}</div>
                                <div class="application-meta">
                                        ${app.jobTitle} • Applied ${app.appliedAt.substring(0, 10)}
                                </div>
                            </div>
                            <c:choose>
                                <c:when test="${app.status == 'PENDING'}">
                                    <span class="status-badge status-pending">Pending</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge">${app.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-icon">📭</div>
                        <p>No applications yet</p>
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
    // 切换历史记录的显示/隐藏
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