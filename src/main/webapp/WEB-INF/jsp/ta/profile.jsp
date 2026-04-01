<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TA Profile - Personal Info &amp; Weekly Availability</title>
    <style>
        /* 全局样式 */
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f9f9f9; }
        .container { max-width: 900px; margin: 0 auto; background: white; border: 1px solid #e0e0e0; position: relative; }
        /* 导航栏样式 */
        .nav-sidebar { width: 120px; float: left; background: #f0f0f0; padding: 15px; min-height: 600px; }
        .nav-sidebar a { display: block; margin: 8px 0; text-decoration: none; color: #333; font-size: 12px; }
        .nav-sidebar a.active { font-weight: bold; color: #0066cc; }
        .nav-sidebar a:hover { color: #0066cc; }
        /* 主内容区 */
        .content { margin-left: 140px; padding: 20px; }
        .breadcrumb { font-size: 12px; color: #666; margin-bottom: 15px; }
        .section { margin-bottom: 25px; padding: 15px; border: 1px solid #e0e0e0; border-radius: 4px; }
        .section h4 { margin-top: 0; color: #444; font-size: 14px; }
        .field { margin: 10px 0; }
        .field label { display: block; font-size: 12px; color: #666; margin-bottom: 3px; }
        .field .value { padding: 6px; background: #f8f9fa; border: 1px solid #eee; border-radius: 3px; font-size: 13px; }
        /* 按钮样式 */
        .btn-group { text-align: right; }
        .btn { padding: 6px 12px; border: none; border-radius: 3px; cursor: pointer; font-size: 12px; text-decoration: none; display: inline-block; }
        .btn-edit { background: #007bff; color: white; }
        .btn-edit:hover { background: #0056b3; }
        /* 右侧悬浮提示框 */
        .side-note { position: absolute; right: 20px; top: 20px; width: 80px; height: 80px; background: #f1c40f; color: white; text-align: center; padding: 5px; font-size: 10px; }
        /* 技能标签 */
        .skills-list { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 5px; }
        .skill-tag { background: #e0f2fe; color: #0369a1; padding: 3px 8px; border-radius: 3px; font-size: 12px; }
        /* 可用时间表格 */
        .availability-table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 12px; }
        .availability-table th, .availability-table td { border: 1px solid #ddd; padding: 6px; text-align: center; }
        .availability-table th { background: #f8f9fa; }
        .avail-yes { color: #22c55e; font-weight: bold; }
        .avail-no  { color: #d1d5db; }
        /* CV 链接 */
        .cv-link { color: #0066cc; font-size: 13px; }
    </style>
</head>
<body>
<div class="container">
    <!-- 左侧导航栏 -->
    <div class="nav-sidebar">
        <p style="font-size: 12px; font-weight: bold;">Navigation</p>
        <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/ta/jobs">Job Market</a>
        <a href="${pageContext.request.contextPath}/ta/profile" class="active">My Profile</a>
        <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
        <a href="${pageContext.request.contextPath}/logout" style="color:#dc2626;margin-top:20px;">Logout</a>
    </div>

    <!-- 主内容区 -->
    <div class="content">
        <div class="breadcrumb">Home &gt; My Profile</div>
        <h2>TA Profile - Personal Info &amp; Weekly Availability</h2>

        <c:choose>
            <c:when test="${ta != null}">
                <div class="section">
                    <h4>Personal Details</h4>
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
                        <label>Degree Program</label>
                        <div class="value">${not empty ta.major ? ta.major : 'Not provided'}</div>
                    </div>
                    <c:if test="${not empty ta.bio}">
                        <div class="field">
                            <label>Bio / Personal Statement</label>
                            <div class="value">${ta.bio}</div>
                        </div>
                    </c:if>
                </div>

                <div class="section">
                    <h4>Skills &amp; CV</h4>
                    <div class="field">
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
                                <div class="value" style="color:#94a3b8;font-style:italic;">Not provided</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="field">
                        <label>CV</label>
                        <c:choose>
                            <c:when test="${not empty ta.cvPath}">
                                <div class="value">
                                    <a href="${pageContext.request.contextPath}/api/ta/cv/view?path=${ta.cvPath}" class="cv-link" target="_blank">
                                        &#128196; View / Download CV (PDF)
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="value" style="color:#94a3b8;font-style:italic;">No CV uploaded yet</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="section">
                    <h4>Weekly Availability</h4>
                    <c:choose>
                        <c:when test="${not empty ta.availability}">
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
                            <p style="font-size:13px;color:#94a3b8;font-style:italic;">No availability set yet.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <div class="section">
                    <p style="font-size:13px;color:#666;">
                        No profile yet. Please <a href="${pageContext.request.contextPath}/ta/profile?action=edit" style="color:#0066cc;">create your profile</a> first.
                    </p>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/ta/profile?action=edit" class="btn btn-edit">Edit Profile</a>
        </div>
    </div>

    <!-- 右侧悬浮提示（仿照原型） -->
    <div class="side-note">My Profile<br>Screen</div>
</div>
</body>
</html>
