<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit TA Profile</title>
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

        .form-layout {
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

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px 18px;
        }

        .form-group {
            margin: 0;
        }

        .full-width {
            grid-column: 1 / -1;
        }

        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 700;
            color: #334155;
            margin-bottom: 7px;
        }

        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%;
            padding: 11px 13px;
            border: 1px solid #cbd5e1;
            border-radius: 10px;
            box-sizing: border-box;
            font-size: 14px;
            background: #fff;
            outline: none;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .form-group input:focus,
        .form-group textarea:focus,
        .form-group select:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .form-group textarea {
            resize: vertical;
            min-height: 110px;
        }

        .required {
            color: #ef4444;
        }

        .cv-note {
            margin-top: 10px;
            font-size: 12px;
            color: #475569;
            line-height: 1.6;
        }

        .cv-note a {
            color: #2563eb;
            text-decoration: none;
            font-weight: 700;
        }

        .cv-note a:hover {
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

        .availability-table input[type="checkbox"] {
            width: 16px;
            height: 16px;
            cursor: pointer;
        }

        .btn-group {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 8px;
            flex-wrap: wrap;
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

        .btn-cancel {
            background: #e2e8f0;
            color: #334155;
        }

        .btn-cancel:hover {
            background: #cbd5e1;
        }

        .btn-save {
            background: #2563eb;
            color: white;
        }

        .btn-save:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }

        @media (max-width: 900px) {
            .form-grid {
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
        <div class="page-title">Edit TA Profile</div>
        <div class="page-subtitle">
            Update your academic information, skills, CV, and weekly availability
            to improve your chances of being selected for TA positions.
        </div>
    </div>

    <form id="profileForm" method="post" action="${pageContext.request.contextPath}/ta/profile"
          enctype="multipart/form-data">
        <div class="form-layout">
            <div class="section">
                <h4>Personal Details</h4>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Full Name <span class="required">*</span></label>
                        <input type="text" id="fullName" name="fullName"
                               value="${ta != null && not empty ta.fullName ? ta.fullName : ''}"
                               placeholder="Enter your full name" required>
                    </div>

                    <div class="form-group">
                        <label>Student ID <span class="required">*</span></label>
                        <input type="text" id="studentId" name="studentId"
                               value="${ta != null && not empty ta.studentId ? ta.studentId : ''}"
                               placeholder="e.g. 20260330" required>
                    </div>

                    <div class="form-group full-width">
                        <label>University Email <span class="required">*</span></label>
                        <input type="email" id="email" name="email"
                               value="${ta != null && not empty ta.email ? ta.email : ''}"
                               placeholder="user@university.ac.uk" required>
                    </div>

                    <div class="form-group full-width">
                        <label>Degree Program</label>
                        <select id="degreeProgram" name="degreeProgram">
                            <option value="BSc Computer Science"
                            ${ta != null && 'BSc Computer Science' == ta.major ? 'selected' : ''}>BSc Computer Science</option>
                            <option value="MSc Computer Science"
                            ${ta != null && 'MSc Computer Science' == ta.major ? 'selected' : ''}>MSc Computer Science</option>
                            <option value="PhD Computer Science"
                            ${ta != null && 'PhD Computer Science' == ta.major ? 'selected' : ''}>PhD Computer Science</option>
                            <option value="BSc Electronic Engineering"
                            ${ta != null && 'BSc Electronic Engineering' == ta.major ? 'selected' : ''}>BSc Electronic Engineering</option>
                            <option value="MSc Electronic Engineering"
                            ${ta != null && 'MSc Electronic Engineering' == ta.major ? 'selected' : ''}>MSc Electronic Engineering</option>
                            <c:if test="${ta != null && not empty ta.major
                                          && ta.major != 'BSc Computer Science'
                                          && ta.major != 'MSc Computer Science'
                                          && ta.major != 'PhD Computer Science'
                                          && ta.major != 'BSc Electronic Engineering'
                                          && ta.major != 'MSc Electronic Engineering'}">
                                <option value="${ta.major}" selected>${ta.major}</option>
                            </c:if>
                        </select>
                    </div>
                </div>
            </div>

            <div class="section">
                <h4>Skills &amp; CV</h4>
                <div class="form-grid">
                    <div class="form-group full-width">
                        <label>Skills</label>
                        <textarea id="skills" name="skills" rows="4"
                                  placeholder="List your technical skills, e.g. Python, Java, SQL, ARM Assembly..."><c:if test="${ta != null && not empty ta.skills}"><c:forEach var="skill" items="${ta.skills}" varStatus="s">${skill}<c:if test="${!s.last}">, </c:if></c:forEach></c:if></textarea>
                    </div>

                    <div class="form-group full-width">
                        <label>Upload your latest CV (PDF only)</label>
                        <input type="file" id="cvFile" name="cvFile" accept=".pdf">
                        <c:if test="${ta != null && not empty ta.cvPath}">
                            <div class="cv-note">
                                Current CV:
                                <a href="${pageContext.request.contextPath}/api/ta/cv/view?path=${ta.cvPath}" target="_blank">
                                    <c:out value="${ta.cvPath}" />
                                </a>
                                <br>
                                Upload a new file to replace it.
                            </div>
                        </c:if>
                    </div>

                    <div class="form-group full-width">
                        <label>Bio / Personal Statement</label>
                        <textarea name="bio" rows="4"
                                  placeholder="Briefly describe your background and why you want to be a TA...">${ta != null && not empty ta.bio ? ta.bio : ''}</textarea>
                    </div>
                </div>
            </div>

            <div class="section">
                <h4>Weekly Availability</h4>
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
                        <tbody>
                        <tr>
                            <td>Morning (09:00–12:00)</td>
                            <td><input type="checkbox" class="availability" name="avail_morning_mon" data-time="morning" data-day="mon"></td>
                            <td><input type="checkbox" class="availability" name="avail_morning_tue" data-time="morning" data-day="tue"></td>
                            <td><input type="checkbox" class="availability" name="avail_morning_wed" data-time="morning" data-day="wed"></td>
                            <td><input type="checkbox" class="availability" name="avail_morning_thu" data-time="morning" data-day="thu"></td>
                            <td><input type="checkbox" class="availability" name="avail_morning_fri" data-time="morning" data-day="fri"></td>
                            <td><input type="checkbox" class="availability" name="avail_morning_sat" data-time="morning" data-day="sat"></td>
                            <td><input type="checkbox" class="availability" name="avail_morning_sun" data-time="morning" data-day="sun"></td>
                        </tr>
                        <tr>
                            <td>Afternoon (13:00–17:00)</td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_mon" data-time="afternoon" data-day="mon"></td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_tue" data-time="afternoon" data-day="tue"></td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_wed" data-time="afternoon" data-day="wed"></td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_thu" data-time="afternoon" data-day="thu"></td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_fri" data-time="afternoon" data-day="fri"></td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_sat" data-time="afternoon" data-day="sat"></td>
                            <td><input type="checkbox" class="availability" name="avail_afternoon_sun" data-time="afternoon" data-day="sun"></td>
                        </tr>
                        <tr>
                            <td>Evening (17:00–20:00)</td>
                            <td><input type="checkbox" class="availability" name="avail_evening_mon" data-time="evening" data-day="mon"></td>
                            <td><input type="checkbox" class="availability" name="avail_evening_tue" data-time="evening" data-day="tue"></td>
                            <td><input type="checkbox" class="availability" name="avail_evening_wed" data-time="evening" data-day="wed"></td>
                            <td><input type="checkbox" class="availability" name="avail_evening_thu" data-time="evening" data-day="thu"></td>
                            <td><input type="checkbox" class="availability" name="avail_evening_fri" data-time="evening" data-day="fri"></td>
                            <td><input type="checkbox" class="availability" name="avail_evening_sat" data-time="evening" data-day="sat"></td>
                            <td><input type="checkbox" class="availability" name="avail_evening_sun" data-time="evening" data-day="sun"></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/ta/profile" class="btn btn-cancel">Cancel</a>
                <button type="button" class="btn btn-save" onclick="saveProfile()">Save Changes</button>
            </div>
        </div>
    </form>
</div>

<script>
    window.onload = function() {
        <c:if test="${ta != null && not empty ta.availability}">
        var avail = ${ta.availability};
        var checkboxes = document.querySelectorAll('.availability');
        checkboxes.forEach(function(cb) {
            var time = cb.dataset.time;
            var day  = cb.dataset.day;
            if (avail[time] && avail[time][day]) {
                cb.checked = true;
            }
        });
        </c:if>
    };

    function validateName(name) {
        if (!name || name.length < 2) {
            alert("Invalid Name!\nFormat: At least 2 characters.\nExample: Tom Smith");
            return false;
        }
        if (/^\d+$/.test(name)) {
            alert("Invalid Name!\nName cannot be all numbers.");
            return false;
        }
        return true;
    }

    function validateEmail(email) {
        var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!re.test(email)) {
            alert("Invalid Email!\nFormat: example@university.ac.uk");
            return false;
        }
        return true;
    }

    function validateStudentId(id) {
        if (!/^\d+$/.test(id)) {
            alert("Invalid Student ID!\nFormat: Numbers only.\nExample: 20260330");
            return false;
        }
        return true;
    }

    function saveProfile() {
        var fullName  = document.getElementById('fullName').value.trim();
        var studentId = document.getElementById('studentId').value.trim();
        var email     = document.getElementById('email').value.trim();

        if (!validateName(fullName)) return;
        if (!validateStudentId(studentId)) return;
        if (!validateEmail(email)) return;

        document.getElementById('profileForm').submit();
    }
</script>
</body>
</html>