<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TA Profile - Personal Info &amp; Weekly Availability</title>
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
            margin: 8px 0;
            text-decoration: none;
            color: #dbeafe;
            font-size: 14px;
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

        .empty-note a {
            color: #2563eb;
            text-decoration: none;
            font-weight: 700;
        }

        .empty-note a:hover {
            text-decoration: underline;
        }

        .btn-group {
            display: flex;
            justify-content: flex-end;
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

        .btn-edit {
            background: #2563eb;
            color: white;
        }

        .btn-edit:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
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
<div class="sidebar">
    <div class="brand">TA Recruitment System</div>
    <div class="nav-title">Navigation</div>

    <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/profile" class="active">My Profile</a>
    <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<div class="main">
    <div class="page-header">
        <div class="breadcrumb">Home &gt; My Profile</div>
        <div class="page-title">My Profile</div>
        <div class="page-subtitle">
            Review your personal information, skills, CV, and weekly availability.
            Keep your profile updated to improve your chances of being selected.
        </div>
    </div>

    <div class="content-layout">
        <c:choose>
            <c:when test="${ta != null}">
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

                        <div class="field full-width">
                            <label>University Email</label>
                            <div class="value">${not empty ta.email ? ta.email : 'Not provided'}</div>
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
            </c:when>

            <c:otherwise>
                <div class="section">
                    <div class="empty-note">
                        No profile yet. Please
                        <a href="${pageContext.request.contextPath}/ta/profile?action=edit">create your profile</a>
                        first.
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/ta/profile?action=edit" class="btn btn-edit">Edit Profile</a>
        </div>
    </div>
</div>
</body>
</html>