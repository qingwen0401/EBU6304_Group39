<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TA Profile - ${ta.fullName}</title>
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

        .container {
            max-width: 1200px;
            margin: 0 auto;
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

        .breadcrumb a {
            color: #2563eb;
            text-decoration: none;
        }

        .breadcrumb a:hover {
            text-decoration: underline;
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

        .content-layout {
            display: grid;
            grid-template-columns: 1fr;
            gap: 22px;
        }

        .section {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 24px;
            box-shadow: 0 8px 22px rgba(15, 23, 42, 0.05);
        }

        .section h4 {
            margin-top: 0;
            margin-bottom: 18px;
            color: #0f172a;
            font-size: 18px;
            font-weight: 700;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px 18px;
        }

        .field {
            margin: 0;
        }

        .full-width {
            grid-column: 1 / -1;
        }

        .field label {
            display: block;
            font-size: 13px;
            color: #475569;
            margin-bottom: 7px;
            font-weight: 700;
        }

        .field .value {
            padding: 12px 14px;
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            font-size: 14px;
            color: #1e293b;
            line-height: 1.7;
            min-height: 46px;
        }

        .field .value.muted {
            color: #94a3b8;
            font-style: italic;
        }

        .skills-list {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-top: 2px;
        }

        .skill-tag {
            background: #eff6ff;
            color: #1d4ed8;
            border: 1px solid #bfdbfe;
            padding: 6px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
        }

        .cv-link {
            color: #2563eb;
            font-size: 14px;
            font-weight: 700;
            text-decoration: none;
        }

        .cv-link:hover {
            text-decoration: underline;
        }

        .availability-wrap {
            overflow-x: auto;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
        }

        .availability-table {
            width: 100%;
            min-width: 760px;
            border-collapse: collapse;
            font-size: 13px;
            background: white;
        }

        .availability-table th,
        .availability-table td {
            border-bottom: 1px solid #e5e7eb;
            padding: 12px 10px;
            text-align: center;
        }

        .availability-table th {
            background: #f8fafc;
            color: #475569;
            font-weight: 700;
        }

        .availability-table td:first-child {
            text-align: left;
            font-weight: 600;
            color: #334155;
            background: #fcfdff;
            min-width: 170px;
        }

        .avail-yes {
            color: #22c55e;
            font-weight: 700;
            font-size: 16px;
        }

        .avail-no {
            color: #cbd5e1;
            font-weight: 700;
        }

        .empty-note {
            font-size: 14px;
            color: #64748b;
            padding: 18px;
            border: 1px dashed #cbd5e1;
            border-radius: 14px;
            background: #f8fafc;
        }

        .btn-group {
            display: flex;
            justify-content: flex-start;
            gap: 12px;
            margin-top: 2px;
        }

        .btn {
            padding: 12px 18px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 700;
            text-decoration: none;
            display: inline-block;
            transition: 0.2s ease;
        }

        .btn-secondary {
            background: #f1f5f9;
            color: #475569;
            border: 1px solid #cbd5e1;
        }

        .btn-secondary:hover {
            background: #e2e8f0;
        }

        @media (max-width: 900px) {
            .info-grid {
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

            .page-header,
            .section {
                padding: 18px;
            }

            .page-title {
                font-size: 24px;
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
    <a href="${pageContext.request.contextPath}/mo/jobs">
        <span class="icon">📄</span> My Jobs
    </a>
    <a href="${pageContext.request.contextPath}/mo/applications">
        <span class="icon">📋</span> Applications
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

<!-- 主内容区 -->
<div class="main">
<div class="container">
    <div class="page-header">
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/mo/applications">Applications</a> &gt; TA Profile
        </div>
        <div class="page-title">${not empty ta.fullName ? ta.fullName : ta.username}</div>
        <div class="page-subtitle">
            Complete profile information for this Teaching Assistant candidate.
        </div>
    </div>

    <div class="content-layout">
        <div class="section">
            <h4>Personal Details</h4>
            <div class="info-grid">
                <div class="field">
                    <label>Full Name</label>
                    <div class="value">${not empty ta.fullName ? ta.fullName : ta.username}</div>
                </div>

                <div class="field">
                    <label>Student ID</label>
                    <div class="value">${not empty ta.studentId ? ta.studentId : 'Not provided'}</div>
                </div>

                <div class="field">
                    <label>University Email</label>
                    <div class="value">${not empty ta.email ? ta.email : 'Not provided'}</div>
                </div>

                <div class="field">
                    <label>GPA</label>
                    <div class="value">${not empty ta.gpa ? ta.gpa : 'Not provided'}</div>
                </div>

                <div class="field full-width">
                    <label>Degree Program</label>
                    <div class="value">${not empty ta.major ? ta.major : 'Not provided'}</div>
                </div>

                <c:if test="${not empty ta.bio}">
                    <div class="field full-width">
                        <label>Bio / Personal Statement</label>
                        <div class="value">${ta.bio}</div>
                    </div>
                </c:if>
            </div>
        </div>

        <div class="section">
            <h4>Skills &amp; CV</h4>
            <div class="info-grid">
                <div class="field full-width">
                    <label>Skills</label>
                    <c:choose>
                        <c:when test="${not empty ta.skills}">
                            <div class="skills-list">
                                <c:forEach var="skill" items="${ta.skills}">
                                    <span class="skill-tag">${skill}</span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="value muted">Not provided</div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="field full-width">
                    <label>CV</label>
                    <c:choose>
                        <c:when test="${not empty ta.cvPath}">
                            <div class="value">
                                <a href="${pageContext.request.contextPath}/api/ta/cv/view?path=${ta.cvPath}"
                                   class="cv-link" target="_blank">
                                    &#128196; View / Download CV (PDF)
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="value muted">No CV uploaded yet</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="section">
            <h4>Weekly Availability</h4>
            <c:choose>
                <c:when test="${not empty ta.availability}">
                    <div class="availability-wrap">
                        <table class="availability-table">
                            <thead>
                            <tr>
                                <th></th>
                                <th>Monday</th>
                                <th>Tuesday</th>
                                <th>Wednesday</th>
                                <th>Thursday</th>
                                <th>Friday</th>
                                <th>Saturday</th>
                                <th>Sunday</th>
                            </tr>
                            </thead>
                            <tbody id="availBody">
                            <tr>
                                <td>Morning (09:00-12:00)</td>
                                <td id="m-mon"></td><td id="m-tue"></td><td id="m-wed"></td>
                                <td id="m-thu"></td><td id="m-fri"></td><td id="m-sat"></td><td id="m-sun"></td>
                            </tr>
                            <tr>
                                <td>Afternoon (13:00-17:00)</td>
                                <td id="a-mon"></td><td id="a-tue"></td><td id="a-wed"></td>
                                <td id="a-thu"></td><td id="a-fri"></td><td id="a-sat"></td><td id="a-sun"></td>
                            </tr>
                            <tr>
                                <td>Evening (17:00-20:00)</td>
                                <td id="e-mon"></td><td id="e-tue"></td><td id="e-wed"></td>
                                <td id="e-thu"></td><td id="e-fri"></td><td id="e-sat"></td><td id="e-sun"></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <script>
                        (function() {
                            var avail = ${ta.availability};
                            var timeMap = {morning:'m', afternoon:'a', evening:'e'};
                            var days = ['mon','tue','wed','thu','fri','sat','sun'];
                            for (var t in timeMap) {
                                for (var i = 0; i < days.length; i++) {
                                    var d = days[i];
                                    var cell = document.getElementById(timeMap[t] + '-' + d);
                                    if (cell) {
                                        var yes = avail[t] && avail[t][d];
                                        cell.innerHTML = yes ? '<span class="avail-yes">&#10003;</span>' : '<span class="avail-no">-</span>';
                                    }
                                }
                            }
                        })();
                    </script>
                </c:when>
                <c:otherwise>
                    <div class="empty-note">No availability set yet.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="btn-group">
        <a href="${pageContext.request.contextPath}/mo/applications" class="btn btn-secondary">Back to Applications</a>
    </div>
</div>
</div>
</body>
</html>
