<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Job Templates Library - MO Dashboard</title>
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
            max-width: 1200px;
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

        .templates-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 24px;
        }

        .template-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            transition: all 0.2s;
        }

        .template-card:hover {
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }

        .template-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 16px;
        }

        .template-title {
            font-size: 18px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .template-module {
            font-size: 13px;
            color: #64748b;
            font-weight: 600;
        }

        .template-meta {
            font-size: 13px;
            color: #64748b;
            margin-bottom: 16px;
            line-height: 1.6;
        }

        .template-description {
            font-size: 14px;
            color: #475569;
            line-height: 1.6;
            margin-bottom: 16px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .template-stats {
            display: flex;
            gap: 16px;
            margin-bottom: 16px;
            padding-top: 16px;
            border-top: 1px solid #f1f5f9;
        }

        .stat-item {
            font-size: 12px;
            color: #64748b;
        }

        .stat-value {
            font-weight: 700;
            color: #0f172a;
        }

        .template-actions {
            display: flex;
            gap: 8px;
        }

        .btn {
            padding: 10px 16px;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
            flex: 1;
        }

        .btn-primary {
            background: #2563eb;
            color: white;
        }

        .btn-primary:hover {
            background: #1d4ed8;
        }

        .btn-secondary {
            background: #f1f5f9;
            color: #475569;
            border: 1px solid #cbd5e1;
        }

        .btn-secondary:hover {
            background: #e2e8f0;
        }

        .btn-danger {
            background: #fee2e2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }

        .btn-danger:hover {
            background: #fecaca;
        }

        .empty-state {
            text-align: center;
            padding: 80px 20px;
            color: #94a3b8;
        }

        .empty-state svg {
            width: 80px;
            height: 80px;
            margin-bottom: 20px;
            opacity: 0.5;
        }

        .empty-state h3 {
            font-size: 20px;
            margin-bottom: 8px;
            color: #64748b;
        }

        .empty-state p {
            font-size: 14px;
            margin-bottom: 24px;
        }

        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            align-items: center;
            justify-content: center;
        }

        .modal.active {
            display: flex;
        }

        .modal-content {
            background: white;
            border-radius: 16px;
            padding: 28px;
            max-width: 600px;
            width: 90%;
            max-height: 80vh;
            overflow-y: auto;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }

        .modal-header {
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 20px;
        }

        .modal-body {
            margin-bottom: 24px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            font-weight: 600;
            color: #334155;
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 14px;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }

        .modal-footer {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
        }

        .usage-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
            background: #dbeafe;
            color: #1e40af;
        }

        @media (max-width: 768px) {
            .templates-grid {
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
    <a href="${pageContext.request.contextPath}/mo/analytics">
        <span class="icon">📈</span> Analytics
    </a>

    <div class="nav-title">Tools</div>
    <a href="${pageContext.request.contextPath}/mo/templates" class="active">
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
        <h2>Job Templates Library</h2>
        <p>Store standardized job descriptions for quick re-posting and consistent recruitment.</p>
    </div>

    <c:choose>
        <c:when test="${empty templates}">
            <div class="empty-state">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <h3>No Templates Yet</h3>
                <p>Save your job postings as templates to reuse them later.</p>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/mo/create-job">Create Your First Job</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="templates-grid">
                <c:forEach var="template" items="${templates}">
                    <div class="template-card">
                        <div class="template-header">
                            <div>
                                <div class="template-title">${template.templateName}</div>
                                <div class="template-module">${template.moduleCode} - ${template.moduleName}</div>
                            </div>
                            <span class="usage-badge">${template.usageCount} uses</span>
                        </div>

                        <div class="template-meta">
                            <strong>${template.title}</strong><br>
                            ${template.hoursPerWeek}h/week • £${template.hourlyRate}/hr • GPA ≥ ${template.minGpa}
                        </div>

                        <div class="template-description">
                            ${template.description}
                        </div>

                        <div class="template-stats">
                            <div class="stat-item">
                                Type: <span class="stat-value">${template.jobType}</span>
                            </div>
                            <div class="stat-item">
                                Skills: <span class="stat-value">${template.requiredSkills.size()}</span>
                            </div>
                        </div>

                        <div class="template-actions">
                            <button class="btn btn-primary" onclick="useTemplate('${template.templateId}', '${template.templateName}')">
                                Use Template
                            </button>
                            <button class="btn btn-danger" onclick="deleteTemplate('${template.templateId}', '${template.templateName}')">
                                Delete
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</div>

<!-- Use Template Modal -->
<div id="use-template-modal" class="modal">
    <div class="modal-content">
        <div class="modal-header">Create Job from Template</div>
        <div class="modal-body">
            <p style="margin-bottom: 20px; color: #64748b;">Fill in the details to create a new job posting from this template.</p>

            <div class="form-group">
                <label>Vacancies</label>
                <input type="number" id="vacancies" min="1" value="1" required>
            </div>

            <div class="form-group">
                <label>Application Deadline</label>
                <input type="date" id="deadline" required>
            </div>

            <div class="form-group">
                <label>Semester</label>
                <input type="text" id="semester" placeholder="e.g., 2026 Spring" required>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
            <button class="btn btn-primary" onclick="submitUseTemplate()">Create Job</button>
        </div>
    </div>
</div>

<script>
    let currentTemplateId = null;

    function useTemplate(templateId, templateName) {
        currentTemplateId = templateId;

        // Set default deadline to 2 weeks from now
        const deadline = new Date();
        deadline.setDate(deadline.getDate() + 14);
        document.getElementById('deadline').value = deadline.toISOString().split('T')[0];

        document.getElementById('use-template-modal').classList.add('active');
    }

    function closeModal() {
        document.getElementById('use-template-modal').classList.remove('active');
        currentTemplateId = null;
    }

    function submitUseTemplate() {
        const vacancies = document.getElementById('vacancies').value;
        const deadline = document.getElementById('deadline').value;
        const semester = document.getElementById('semester').value;

        if (!vacancies || !deadline || !semester) {
            alert('Please fill in all fields');
            return;
        }

        fetch('${pageContext.request.contextPath}/mo/templates', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'use',
                templateId: currentTemplateId,
                vacancies: vacancies,
                deadline: deadline,
                semester: semester
            })
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                window.location.href = '${pageContext.request.contextPath}/mo/create-job';
            } else {
                alert('Error: ' + data.error);
            }
        })
        .catch(err => {
            alert('Failed to create job from template');
            console.error(err);
        });

        closeModal();
    }

    function deleteTemplate(templateId, templateName) {
        if (!confirm(`Delete template "${templateName}"? This action cannot be undone.`)) {
            return;
        }

        fetch('${pageContext.request.contextPath}/mo/templates', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'delete',
                templateId: templateId
            })
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                location.reload();
            } else {
                alert('Error: ' + data.error);
            }
        })
        .catch(err => {
            alert('Failed to delete template');
            console.error(err);
        });
    }
</script>
</body>
</html>
