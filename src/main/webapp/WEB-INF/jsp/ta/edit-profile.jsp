<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit TA Profile</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f9f9f9; }
        .container { max-width: 900px; margin: 0 auto; background: white; border: 1px solid #e0e0e0; position: relative; }
        .nav-sidebar { width: 120px; float: left; background: #f0f0f0; padding: 15px; min-height: 700px; }
        .nav-sidebar a { display: block; margin: 8px 0; text-decoration: none; color: #333; font-size: 12px; }
        .nav-sidebar a.active { font-weight: bold; color: #0066cc; }
        .nav-sidebar a:hover { color: #0066cc; }
        .content { margin-left: 140px; padding: 20px; }
        .breadcrumb { font-size: 12px; color: #666; margin-bottom: 15px; }
        .section { margin-bottom: 25px; padding: 15px; border: 1px solid #e0e0e0; border-radius: 4px; }
        .section h4 { margin-top: 0; color: #444; font-size: 14px; }
        .form-group { margin: 10px 0; }
        .form-group label { display: block; font-size: 12px; color: #666; margin-bottom: 3px; }
        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 3px; box-sizing: border-box; font-size: 13px;
        }
        .required { color: red; }
        /* 周可用性表格 */
        .availability-table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 12px; }
        .availability-table th, .availability-table td {
            border: 1px solid #ddd; padding: 6px; text-align: center;
        }
        .availability-table th { background: #f8f9fa; }
        /* 按钮组 */
        .btn-group { text-align: right; margin-top: 20px; }
        .btn { padding: 6px 12px; border: none; border-radius: 3px; cursor: pointer; font-size: 12px; margin-left: 8px; text-decoration: none; display: inline-block; }
        .btn-cancel { background: #6c757d; color: white; }
        .btn-save { background: #007bff; color: white; }
        .btn-save:hover { background: #0056b3; }
        .side-note { position: absolute; right: 20px; top: 20px; width: 80px; height: 80px; background: #f1c40f; color: white; text-align: center; padding: 5px; font-size: 10px; }
    </style>
</head>
<body>
<div class="container">
    <div class="nav-sidebar">
        <p style="font-size: 12px; font-weight: bold;">Navigation</p>
        <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
        <a href="${pageContext.request.contextPath}/ta/profile" class="active">My Profile</a>
        <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
        <a href="${pageContext.request.contextPath}/logout" style="color:#dc2626;margin-top:20px;">Logout</a>
    </div>

    <div class="content">
        <div class="breadcrumb">Home &gt; My Profile</div>
        <h2>Edit TA Profile</h2>

        <form id="profileForm" method="post" action="${pageContext.request.contextPath}/ta/profile"
              enctype="multipart/form-data">
            <div class="section">
                <h4>Personal Details</h4>
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
                           placeholder="e.g., 12345678" required>
                </div>
                <div class="form-group">
                    <label>University Email <span class="required">*</span></label>
                    <input type="email" id="email" name="email"
                           value="${ta != null && not empty ta.email ? ta.email : ''}"
                           placeholder="user@university.ac.uk" required>
                </div>
                <div class="form-group">
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

            <div class="section">
                <h4>Skills &amp; CV</h4>
                <div class="form-group">
                    <label>Skills</label>
                    <textarea id="skills" name="skills" rows="4"
                              placeholder="List your technical skills, e.g., Python, ARM Assembly, Java..."><c:if test="${ta != null && not empty ta.skills}"><c:forEach var="skill" items="${ta.skills}" varStatus="s">${skill}<c:if test="${!s.last}">, </c:if></c:forEach></c:if></textarea>
                </div>
                <div class="form-group">
                    <label>Upload your latest CV (PDF only)</label>
                    <input type="file" id="cvFile" name="cvFile" accept=".pdf" style="padding: 3px;">
                    <c:if test="${ta != null && not empty ta.cvPath}">
                        <div style="margin-top:8px;font-size:12px;color:#374151;">
                            Current CV:
                            <a href="${pageContext.request.contextPath}/api/ta/cv/view?path=${ta.cvPath}"
                               target="_blank" style="color:#0066cc;text-decoration:none;">
                                <c:out value="${ta.cvPath}" />
                            </a>
                        </div>
                        <small style="color:#666;font-size:11px;">Upload a new file to replace it.</small>
                    </c:if>
                </div>
                <div class="form-group">
                    <label>Bio / Personal Statement</label>
                    <textarea name="bio" rows="3"
                              placeholder="Briefly describe your background and why you want to be a TA...">${ta != null && not empty ta.bio ? ta.bio : ''}</textarea>
                </div>
            </div>

            <div class="section">
                <h4>Weekly Availability</h4>
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
                        <td>Morning (09:00-12:00)</td>
                        <td><input type="checkbox" class="availability" name="avail_morning_mon" data-time="morning" data-day="mon"></td>
                        <td><input type="checkbox" class="availability" name="avail_morning_tue" data-time="morning" data-day="tue"></td>
                        <td><input type="checkbox" class="availability" name="avail_morning_wed" data-time="morning" data-day="wed"></td>
                        <td><input type="checkbox" class="availability" name="avail_morning_thu" data-time="morning" data-day="thu"></td>
                        <td><input type="checkbox" class="availability" name="avail_morning_fri" data-time="morning" data-day="fri"></td>
                        <td><input type="checkbox" class="availability" name="avail_morning_sat" data-time="morning" data-day="sat"></td>
                        <td><input type="checkbox" class="availability" name="avail_morning_sun" data-time="morning" data-day="sun"></td>
                    </tr>
                    <tr>
                        <td>Afternoon (13:00-17:00)</td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_mon" data-time="afternoon" data-day="mon"></td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_tue" data-time="afternoon" data-day="tue"></td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_wed" data-time="afternoon" data-day="wed"></td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_thu" data-time="afternoon" data-day="thu"></td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_fri" data-time="afternoon" data-day="fri"></td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_sat" data-time="afternoon" data-day="sat"></td>
                        <td><input type="checkbox" class="availability" name="avail_afternoon_sun" data-time="afternoon" data-day="sun"></td>
                    </tr>
                    <tr>
                        <td>Evening (17:00-20:00)</td>
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

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/ta/profile" class="btn btn-cancel">Cancel</a>
                <button type="button" class="btn btn-save" onclick="saveProfile()">Save Changes</button>
            </div>
        </form>
    </div>

    <div class="side-note">My Profile<br>Screen</div>
</div>

<script>
    // 页面加载时，如果有已保存的可用时间，预填复选框
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

    // 格式校验 + 提交
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

        if (!validateName(fullName))     return;
        if (!validateStudentId(studentId)) return;
        if (!validateEmail(email))       return;

        document.getElementById('profileForm').submit();
    }
</script>
</body>
</html>
