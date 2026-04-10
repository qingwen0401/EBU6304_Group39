<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ebu6304.recruitment.models.JobPosting" %>

<%
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
    List<JobPosting> myJobs = (List<JobPosting>) request.getAttribute("myJobs");

    String moduleCode = (String) request.getAttribute("moduleCode");
    String moduleName = (String) request.getAttribute("moduleName");
    String title = (String) request.getAttribute("title");
    String description = (String) request.getAttribute("description");
    String requiredSkills = (String) request.getAttribute("requiredSkills");
    String hoursPerWeek = (String) request.getAttribute("hoursPerWeek");
    String vacancies = (String) request.getAttribute("vacancies");
    String deadline = (String) request.getAttribute("deadline");
    String semester = (String) request.getAttribute("semester");
    String jobType = (String) request.getAttribute("jobType");
    String minGpa = (String) request.getAttribute("minGpa");
    String hourlyRate = (String) request.getAttribute("hourlyRate");

    if (moduleCode == null) moduleCode = "";
    if (moduleName == null) moduleName = "";
    if (title == null) title = "";
    if (description == null) description = "";
    if (requiredSkills == null) requiredSkills = "";
    if (hoursPerWeek == null) hoursPerWeek = "";
    if (vacancies == null) vacancies = "";
    if (deadline == null) deadline = "";
    if (semester == null) semester = "";
    if (jobType == null) jobType = "";
    if (minGpa == null) minGpa = "";
    if (hourlyRate == null) hourlyRate = "";
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Job Posting</title>
    <style>
        * {
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            margin: 0;
            background: #f4f7fb;
            color: #1e293b;
        }

        .topbar {
            background: linear-gradient(135deg, #1e40af, #2563eb);
            color: white;
            padding: 16px 34px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .topbar .title {
            font-size: 20px;
            font-weight: 700;
        }

        .topbar a {
            color: #dbeafe;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
        }

        .topbar a:hover {
            color: white;
        }

        .page {
            max-width: 1240px;
            margin: 28px auto;
            padding: 0 24px 32px;
        }

        .page-header {
            margin-bottom: 20px;
        }

        .page-header h2 {
            margin: 0 0 8px;
            font-size: 30px;
            color: #0f172a;
        }

        .page-header p {
            margin: 0;
            color: #64748b;
            font-size: 15px;
        }

        .alert {
            padding: 14px 16px;
            border-radius: 12px;
            margin-bottom: 18px;
            font-size: 14px;
            font-weight: 600;
        }

        .alert.success {
            background: #ecfdf5;
            color: #166534;
            border: 1px solid #bbf7d0;
        }

        .alert.error {
            background: #fef2f2;
            color: #b91c1c;
            border: 1px solid #fecaca;
        }

        .layout {
            display: grid;
            grid-template-columns: 1.2fr 0.8fr;
            gap: 24px;
            align-items: start;
        }

        .panel {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
        }

        .form-panel {
            padding: 26px 26px 20px;
        }

        .list-panel {
            padding: 22px;
        }

        .panel-title {
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 18px;
            color: #0f172a;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px 18px;
        }

        .full-width {
            grid-column: 1 / -1;
        }

        .field label {
            display: block;
            margin-bottom: 7px;
            font-size: 14px;
            font-weight: 700;
            color: #334155;
        }

        .field input,
        .field textarea {
            width: 100%;
            border: 1px solid #cbd5e1;
            border-radius: 10px;
            padding: 11px 13px;
            font-size: 14px;
            outline: none;
            background: #fff;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .field input:focus,
        .field textarea:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .field textarea {
            min-height: 120px;
            resize: vertical;
        }

        .submit-row {
            margin-top: 22px;
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        .btn {
            border: none;
            border-radius: 10px;
            padding: 12px 20px;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: 0.2s ease;
        }

        .btn-primary {
            background: #2563eb;
            color: white;
        }

        .btn-primary:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }

        .btn-secondary {
            background: #eff6ff;
            color: #1d4ed8;
            border: 1px solid #bfdbfe;
        }

        .btn-secondary:hover {
            background: #dbeafe;
        }

        .jobs-list {
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .job-card {
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            padding: 16px;
            background: #f8fafc;
        }

        .job-card h4 {
            margin: 0 0 10px;
            font-size: 17px;
            color: #0f172a;
        }

        .job-meta {
            font-size: 13px;
            color: #64748b;
            line-height: 1.8;
        }

        .status-badge {
            display: inline-block;
            margin-top: 10px;
            padding: 5px 10px;
            font-size: 12px;
            font-weight: 700;
            border-radius: 999px;
            background: #dbeafe;
            color: #1d4ed8;
        }

        .empty-box {
            padding: 18px;
            border: 1px dashed #cbd5e1;
            border-radius: 12px;
            color: #64748b;
            background: #f8fafc;
            font-size: 14px;
        }

        @media (max-width: 1024px) {
            .layout {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }

            .page {
                padding: 0 16px 24px;
            }

            .page-header h2 {
                font-size: 24px;
            }

            .topbar {
                padding: 14px 18px;
            }
        }
    </style>
</head>
<body>

<div class="topbar">
    <div class="title">TA Recruitment System — MO</div>
    <a href="<%= request.getContextPath() %>/mo/dashboard">Back to Dashboard</a>
</div>

<div class="page">
    <div class="page-header">
        <h2>Create Job Posting</h2>
        <p>Create a new TA position and manage your posted jobs in one place.</p>
    </div>

    <% if (successMessage != null) { %>
    <div class="alert success"><%= successMessage %></div>
    <% } %>

    <% if (errorMessage != null) { %>
    <div class="alert error"><%= errorMessage %></div>
    <% } %>

    <div class="layout">
        <div class="panel form-panel">
            <div class="panel-title">Job Details</div>

            <form method="post" action="<%= request.getContextPath() %>/mo/create-job">
                <div class="form-grid">
                    <div class="field">
                        <label>Module Code</label>
                        <input type="text" name="moduleCode" value="<%= moduleCode %>" required>
                    </div>

                    <div class="field">
                        <label>Module Name</label>
                        <input type="text" name="moduleName" value="<%= moduleName %>" required>
                    </div>

                    <div class="field full-width">
                        <label>Job Title</label>
                        <input type="text" name="title" value="<%= title %>" required>
                    </div>

                    <div class="field full-width">
                        <label>Description</label>
                        <textarea name="description" required><%= description %></textarea>
                    </div>

                    <div class="field full-width">
                        <label>Required Skills (comma separated)</label>
                        <input type="text" name="requiredSkills" value="<%= requiredSkills %>" placeholder="Java, OOP, Git" required>
                    </div>

                    <div class="field">
                        <label>Hours Per Week</label>
                        <input type="number" name="hoursPerWeek" value="<%= hoursPerWeek %>" required>
                    </div>

                    <div class="field">
                        <label>Vacancies</label>
                        <input type="number" name="vacancies" value="<%= vacancies %>" required>
                    </div>

                    <div class="field">
                        <label>Deadline</label>
                        <input type="date" name="deadline" value="<%= deadline %>" required>
                    </div>

                    <div class="field">
                        <label>Semester</label>
                        <input type="text" name="semester" value="<%= semester %>" placeholder="2026 Spring" required>
                    </div>

                    <div class="field">
                        <label>Job Type</label>
                        <input type="text" name="jobType" value="<%= jobType %>" placeholder="LAB_TA" required>
                    </div>

                    <div class="field">
                        <label>Minimum GPA</label>
                        <input type="number" step="0.1" name="minGpa" value="<%= minGpa %>" required>
                    </div>

                    <div class="field">
                        <label>Hourly Rate</label>
                        <input type="number" step="0.1" name="hourlyRate" value="<%= hourlyRate %>" required>
                    </div>
                </div>

                <div class="submit-row">
                    <button class="btn btn-primary" type="submit">Post Job</button>
                    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/mo/dashboard">Cancel</a>
                </div>
            </form>
        </div>

        <div class="panel list-panel">
            <div class="panel-title">My Posted Jobs</div>

            <% if (myJobs != null && !myJobs.isEmpty()) { %>
            <div class="jobs-list">
                <% for (JobPosting job : myJobs) { %>
                <div class="job-card">
                    <h4><%= job.getTitle() %></h4>
                    <div class="job-meta">
                        Module: <%= job.getModuleCode() %><br>
                        Deadline: <%= job.getDeadline() %><br>
                        Vacancies: <%= job.getVacancies() %><br>
                        Hourly Rate: <%= job.getHourlyRate() %><br>
                        Semester: <%= job.getSemester() %>
                    </div>
                    <span class="status-badge"><%= job.getStatus() %></span>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <div class="empty-box">No job postings yet.</div>
            <% } %>
        </div>
    </div>
</div>

</body>
</html>