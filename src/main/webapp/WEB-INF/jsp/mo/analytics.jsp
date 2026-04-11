<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Recruitment Analytics - MO Dashboard</title>
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

        /* 左侧导航栏 */
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

        /* 主内容区 */
        .main {
            margin-left: 250px;
            padding: 32px;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
        }

        .page-header {
            margin-bottom: 28px;
        }

        .page-header h2 {
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .page-header p {
            color: #64748b;
            font-size: 15px;
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
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

        .stat-value {
            font-size: 36px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 8px;
        }

        .stat-label {
            font-size: 13px;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            font-weight: 600;
        }

        .stat-change {
            font-size: 12px;
            margin-top: 8px;
        }

        .stat-change.positive {
            color: #16a34a;
        }

        .stat-change.negative {
            color: #dc2626;
        }

        .charts-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
            margin-bottom: 32px;
        }

        .chart-card {
            background: white;
            padding: 28px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

        .chart-card h3 {
            font-size: 18px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 20px;
        }

        .chart-container {
            position: relative;
            height: 300px;
        }

        .jobs-table-card {
            background: white;
            padding: 28px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

        .jobs-table-card h3 {
            font-size: 18px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: #f8fafc;
        }

        th {
            padding: 12px 16px;
            text-align: left;
            font-size: 12px;
            font-weight: 700;
            color: #475569;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        td {
            padding: 14px 16px;
            border-top: 1px solid #f1f5f9;
            font-size: 14px;
        }

        tr:hover {
            background: #f8fafc;
        }

        .progress-bar {
            width: 100%;
            height: 8px;
            background: #e2e8f0;
            border-radius: 999px;
            overflow: hidden;
        }

        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #2563eb, #3b82f6);
            border-radius: 999px;
            transition: width 0.3s ease;
        }

        .metric-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
        }

        .metric-high {
            background: #d1fae5;
            color: #065f46;
        }

        .metric-medium {
            background: #fef3c7;
            color: #92400e;
        }

        .metric-low {
            background: #fee2e2;
            color: #991b1b;
        }

        @media (max-width: 1200px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .charts-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

<!-- 左侧导航栏 -->
<div class="sidebar">
    <div class="brand">TA Recruitment</div>
    <div class="role">Module Organiser Portal</div>

    <div class="nav-title">Main</div>
    <a href="${pageContext.request.contextPath}/mo/dashboard">
        <span class="icon">📊</span> Dashboard
    </a>
    <a href="${pageContext.request.contextPath}/mo/create-job">
        <span class="icon">➕</span> Create Job
    </a>

    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/mo/applications">
        <span class="icon">📋</span> Applications
    </a>
    <a href="${pageContext.request.contextPath}/mo/analytics" class="active">
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

<!-- 主内容区 -->
<div class="main">
<div class="container">
    <div class="page-header">
        <h2>Recruitment Summary & Analytics</h2>
        <p>Visualize admission ratios, monitor fill rates, and track recruitment progress.</p>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-value">${analytics.totalJobs}</div>
            <div class="stat-label">Total Job Postings</div>
            <div class="stat-change positive">↑ ${analytics.openJobs} currently open</div>
        </div>

        <div class="stat-card">
            <div class="stat-value">${analytics.fillRate}%</div>
            <div class="stat-label">Overall Fill Rate</div>
            <div class="stat-change">${analytics.totalFilled} / ${analytics.totalVacancies} positions filled</div>
        </div>

        <div class="stat-card">
            <div class="stat-value">${analytics.totalApplications}</div>
            <div class="stat-label">Total Applications</div>
            <div class="stat-change">${analytics.pendingApplications} pending review</div>
        </div>

        <div class="stat-card">
            <div class="stat-value">${analytics.acceptanceRate}%</div>
            <div class="stat-label">Acceptance Rate</div>
            <div class="stat-change">${analytics.acceptedApplications} accepted / ${analytics.rejectedApplications} rejected</div>
        </div>
    </div>

    <div class="charts-grid">
        <div class="chart-card">
            <h3>Application Status Distribution</h3>
            <div class="chart-container">
                <canvas id="applicationStatusChart"></canvas>
            </div>
        </div>

        <div class="chart-card">
            <h3>Job Status Distribution</h3>
            <div class="chart-container">
                <canvas id="jobStatusChart"></canvas>
            </div>
        </div>
    </div>

    <div class="jobs-table-card">
        <h3>Job Performance Breakdown</h3>
        <table>
            <thead>
                <tr>
                    <th>Job Title</th>
                    <th>Module</th>
                    <th>Applications</th>
                    <th>Accepted</th>
                    <th>Fill Rate</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="job" items="${jobs}">
                    <c:set var="jobStat" value="${analytics.jobStats[job.jobId]}" />
                    <tr>
                        <td><strong>${job.title}</strong></td>
                        <td>${job.moduleCode}</td>
                        <td>${jobStat.totalApplications}</td>
                        <td>${jobStat.accepted}</td>
                        <td>
                            <div style="display: flex; align-items: center; gap: 12px;">
                                <div class="progress-bar" style="flex: 1;">
                                    <div class="progress-fill" style="width: ${jobStat.fillRate}%"></div>
                                </div>
                                <span style="font-size: 13px; font-weight: 600; color: #64748b; min-width: 45px;">
                                    ${String.format("%.0f", jobStat.fillRate)}%
                                </span>
                            </div>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${jobStat.fillRate >= 80}">
                                    <span class="metric-badge metric-high">High</span>
                                </c:when>
                                <c:when test="${jobStat.fillRate >= 50}">
                                    <span class="metric-badge metric-medium">Medium</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="metric-badge metric-low">Low</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Application Status Pie Chart
    const appStatusCtx = document.getElementById('applicationStatusChart').getContext('2d');
    new Chart(appStatusCtx, {
        type: 'pie',
        data: {
            labels: ['Pending', 'Accepted', 'Rejected'],
            datasets: [{
                data: [
                    ${analytics.applicationStatusData['Pending']},
                    ${analytics.applicationStatusData['Accepted']},
                    ${analytics.applicationStatusData['Rejected']}
                ],
                backgroundColor: ['#fbbf24', '#10b981', '#ef4444'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: {
                            size: 13,
                            weight: '600'
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return label + ': ' + value + ' (' + percentage + '%)';
                        }
                    }
                }
            }
        }
    });

    // Job Status Pie Chart
    const jobStatusCtx = document.getElementById('jobStatusChart').getContext('2d');
    new Chart(jobStatusCtx, {
        type: 'pie',
        data: {
            labels: ['Open', 'Closed', 'Other'],
            datasets: [{
                data: [
                    ${analytics.jobStatusData['Open']},
                    ${analytics.jobStatusData['Closed']},
                    ${analytics.jobStatusData['Other']}
                ],
                backgroundColor: ['#3b82f6', '#6366f1', '#94a3b8'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: {
                            size: 13,
                            weight: '600'
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return label + ': ' + value + ' (' + percentage + '%)';
                        }
                    }
                }
            }
        }
    });
</script>
</body>
</html>
