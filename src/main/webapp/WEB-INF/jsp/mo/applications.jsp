<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Review Applications - MO Dashboard</title>
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

        .tabs {
            display: flex;
            gap: 8px;
            margin-bottom: 24px;
            border-bottom: 2px solid #e2e8f0;
        }

        .tab {
            padding: 12px 24px;
            background: none;
            border: none;
            border-bottom: 3px solid transparent;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            color: #64748b;
            transition: all 0.2s;
        }

        .tab.active {
            color: #2563eb;
            border-bottom-color: #2563eb;
        }

        .tab:hover {
            color: #1e40af;
        }

        .actions-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding: 16px;
            background: white;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
        }

        .bulk-actions {
            display: flex;
            gap: 12px;
        }

        .btn {
            padding: 10px 18px;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-primary {
            background: #2563eb;
            color: white;
        }

        .btn-primary:hover {
            background: #1d4ed8;
        }

        .btn-danger {
            background: #dc2626;
            color: white;
        }

        .btn-danger:hover {
            background: #b91c1c;
        }

        .btn-secondary {
            background: #f1f5f9;
            color: #475569;
            border: 1px solid #cbd5e1;
        }

        .btn-secondary:hover {
            background: #e2e8f0;
        }

        .applications-table {
            background: white;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            overflow: hidden;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: #f8fafc;
        }

        th {
            padding: 14px 16px;
            text-align: left;
            font-size: 13px;
            font-weight: 700;
            color: #475569;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        td {
            padding: 16px;
            border-top: 1px solid #f1f5f9;
            font-size: 14px;
        }

        tr:hover {
            background: #f8fafc;
        }

        .status-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
        }

        .status-pending {
            background: #fef3c7;
            color: #92400e;
        }

        .status-accepted {
            background: #d1fae5;
            color: #065f46;
        }

        .status-rejected {
            background: #fee2e2;
            color: #991b1b;
        }


        .action-buttons {
            display: flex;
            gap: 8px;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #94a3b8;
        }

        .empty-state svg {
            width: 64px;
            height: 64px;
            margin-bottom: 16px;
            opacity: 0.5;
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
            max-width: 500px;
            width: 90%;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }

        .modal-header {
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 16px;
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

        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 14px;
            resize: vertical;
            min-height: 100px;
        }

        .modal-footer {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-bottom: 28px;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
        }

        .stat-value {
            font-size: 32px;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .stat-label {
            font-size: 13px;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .ai-match-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 24px;
            color: white;
        }

        .ai-match-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }

        .ai-match-title {
            font-size: 18px;
            font-weight: 700;
        }

        .ai-match-subtitle {
            font-size: 13px;
            opacity: 0.9;
            margin-top: 4px;
        }

        .btn-ai {
            background: white;
            color: #667eea;
            font-weight: 700;
        }

        .btn-ai:hover {
            background: #f8f9fa;
        }

        .ai-results {
            display: none;
            margin-top: 20px;
        }

        .ai-results.active {
            display: block;
        }

        .ai-result-card {
            background: white;
            border-radius: 10px;
            padding: 16px;
            margin-bottom: 12px;
            color: #1e293b;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .ai-result-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;
        }

        .ai-score {
            font-size: 32px;
            font-weight: 700;
            color: #667eea;
        }

        .ai-score.high {
            color: #16a34a;
        }

        .ai-score.medium {
            color: #ea580c;
        }

        .ai-score.low {
            color: #dc2626;
        }

        .matched-skills {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
            margin-bottom: 12px;
        }

        .skill-tag {
            background: #dcfce7;
            color: #166534;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 600;
        }

        .ai-reason {
            font-size: 14px;
            color: #475569;
            font-style: italic;
            padding: 12px;
            background: #f8fafc;
            border-radius: 8px;
            border-left: 3px solid #667eea;
        }

        .api-key-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 2000;
            align-items: center;
            justify-content: center;
        }

        .api-key-modal.active {
            display: flex;
        }

        .loading-spinner {
            display: inline-block;
            width: 16px;
            height: 16px;
            border: 2px solid #f3f3f3;
            border-top: 2px solid #667eea;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
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
    <a href="${pageContext.request.contextPath}/mo/applications" class="active">
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
        <h2>Application Review Dashboard</h2>
        <p>Review, rate, and manage TA applications with internal notes and bulk actions.</p>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-value">${applicationsByStatus['PENDING'].size()}</div>
            <div class="stat-label">Pending Review</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">${applicationsByStatus['ACCEPTED'].size()}</div>
            <div class="stat-label">Accepted</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">${applicationsByStatus['REJECTED'].size()}</div>
            <div class="stat-label">Rejected</div>
        </div>
    </div>

    <div class="ai-match-section">
        <div class="ai-match-header">
            <div>
                <div class="ai-match-title">🤖 AI-Powered Skill Matching</div>
                <div class="ai-match-subtitle">Analyze applicants using DeepSeek AI to find the best matches</div>
            </div>
            <div style="display: flex; align-items: center; gap: 8px;">
                <select id="job-select" class="job-select" style="padding: 10px 14px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.3); background: white; color: #1e293b; font-size: 14px; min-width: 200px;">
                    <option value="">Select a job...</option>
                </select>
                <button class="btn btn-ai" onclick="showApiKeyModal()">Configure API Key</button>
                <button class="btn btn-ai" onclick="runAIMatching()">
                    <span id="ai-btn-text">Run AI Matching</span>
                    <span id="ai-loading" class="loading-spinner" style="display:none;"></span>
                </button>
            </div>
        </div>
        <div id="ai-results" class="ai-results"></div>
    </div>

    <div class="tabs">
        <button class="tab active" data-tab="all">All Applications</button>
        <button class="tab" data-tab="pending">Pending</button>
        <button class="tab" data-tab="accepted">Accepted</button>
        <button class="tab" data-tab="rejected">Rejected</button>
    </div>

    <div class="actions-bar">
        <div class="bulk-actions">
            <button class="btn btn-danger" onclick="bulkReject()">Bulk Reject Selected</button>
            <button class="btn btn-secondary" onclick="clearSelection()">Clear Selection</button>
        </div>
        <div>
            <span id="selected-count">0 selected</span>
        </div>
    </div>

    <div class="applications-table">
        <table>
            <thead>
                <tr>
                    <th><input type="checkbox" id="select-all"></th>
                    <th>Applicant</th>
                    <th>Job Title</th>
                    <th>Applied Date</th>
                    <th>GPA</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="applications-body">
                <c:choose>
                    <c:when test="${empty applications}">
                        <tr>
                            <td colspan="7">
                                <div class="empty-state">
                                    <p>No applications found.</p>
                                </div>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="app" items="${applications}">
                            <tr data-status="${app.status}">
                                <td><input type="checkbox" class="app-checkbox" value="${app.applicationId}"></td>
                                <td><strong>${app.taName}</strong></td>
                                <td>${app.jobTitle}</td>
                                <td>${app.appliedAt.substring(0, 10)}</td>
                                <td>${app.taGpa}</td>
                                <td>
                                    <span class="status-badge status-${app.status.toLowerCase()}">${app.status}</span>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <c:if test="${app.status == 'PENDING'}">
                                            <button class="btn btn-primary btn-sm" onclick="reviewApplication('${app.applicationId}', 'accept')">Accept</button>
                                            <button class="btn btn-danger btn-sm" onclick="reviewApplication('${app.applicationId}', 'reject')">Reject</button>
                                        </c:if>
                                        <button class="btn btn-secondary btn-sm" onclick="addNote('${app.applicationId}')">Add Note</button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>
</div>

<!-- Review Modal -->
<div id="review-modal" class="modal">
    <div class="modal-content">
        <div class="modal-header">Review Application</div>
        <div class="modal-body">
            <div class="form-group">
                <label>Review Note</label>
                <textarea id="review-note" placeholder="Add your review comments..."></textarea>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
            <button class="btn btn-primary" onclick="submitReview()">Submit</button>
        </div>
    </div>
</div>

<!-- API Key Configuration Modal -->
<div id="api-key-modal" class="api-key-modal">
    <div class="modal-content">
        <div class="modal-header">Configure DeepSeek API Key</div>
        <div class="modal-body">
            <div class="form-group">
                <label>DeepSeek API Key</label>
                <input type="password" id="api-key-input" placeholder="sk-..." style="width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 8px;">
                <p style="font-size: 12px; color: #64748b; margin-top: 8px;">
                    Get your API key from <a href="https://platform.deepseek.com" target="_blank">platform.deepseek.com</a>
                </p>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeApiKeyModal()">Cancel</button>
            <button class="btn btn-primary" onclick="saveApiKey()">Save</button>
        </div>
    </div>
</div>

<script>
    let currentApplicationId = null;
    let currentDecision = null;

    // Tab switching
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            filterApplications(tab.dataset.tab);
        });
    });

    function filterApplications(status) {
        const rows = document.querySelectorAll('#applications-body tr');
        rows.forEach(row => {
            if (status === 'all') {
                row.style.display = '';
            } else {
                row.style.display = row.dataset.status === status.toUpperCase() ? '' : 'none';
            }
        });
    }

    // Select all checkbox
    document.getElementById('select-all').addEventListener('change', function() {
        document.querySelectorAll('.app-checkbox').forEach(cb => {
            if (cb.closest('tr').style.display !== 'none') {
                cb.checked = this.checked;
            }
        });
        updateSelectedCount();
    });

    document.querySelectorAll('.app-checkbox').forEach(cb => {
        cb.addEventListener('change', updateSelectedCount);
    });

    function updateSelectedCount() {
        const count = document.querySelectorAll('.app-checkbox:checked').length;
        document.getElementById('selected-count').textContent = count + ' selected';
    }

    function clearSelection() {
        document.querySelectorAll('.app-checkbox').forEach(cb => cb.checked = false);
        document.getElementById('select-all').checked = false;
        updateSelectedCount();
    }

    function reviewApplication(applicationId, decision) {
        currentApplicationId = applicationId;
        currentDecision = decision;
        document.getElementById('review-modal').classList.add('active');
    }

    function closeModal() {
        document.getElementById('review-modal').classList.remove('active');
        document.getElementById('review-note').value = '';
    }

    function submitReview() {
        const note = document.getElementById('review-note').value;

        fetch('${pageContext.request.contextPath}/mo/applications', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'review',
                applicationId: currentApplicationId,
                decision: currentDecision,
                note: note
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
        });

        closeModal();
    }

    function addNote(applicationId) {
        const note = prompt('Enter your internal note:');
        if (note) {
            fetch('${pageContext.request.contextPath}/mo/applications', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: new URLSearchParams({
                    action: 'add-note',
                    applicationId: applicationId,
                    note: note
                })
            })
            .then(res => res.json())
            .then(data => {
                alert(data.success ? data.message : 'Error: ' + data.error);
            });
        }
    }

    function bulkReject() {
        const selected = Array.from(document.querySelectorAll('.app-checkbox:checked')).map(cb => cb.value);
        if (selected.length === 0) {
            alert('Please select applications to reject');
            return;
        }

        if (!confirm(`Reject ${selected.length} applications?`)) return;

        const note = prompt('Enter rejection reason (optional):');

        fetch('${pageContext.request.contextPath}/mo/applications', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'bulk-reject',
                'applicationIds[]': selected,
                note: note || 'Bulk rejection'
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
        });
    }

    // API Key Modal Functions
    function showApiKeyModal() {
        document.getElementById('api-key-modal').classList.add('active');
    }

    function closeApiKeyModal() {
        document.getElementById('api-key-modal').classList.remove('active');
        document.getElementById('api-key-input').value = '';
    }

    function saveApiKey() {
        const apiKey = document.getElementById('api-key-input').value.trim();
        if (!apiKey) {
            alert('Please enter an API key');
            return;
        }

        fetch('${pageContext.request.contextPath}/mo/applications', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'save-api-key',
                apiKey: apiKey
            })
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert('API key saved successfully!');
                closeApiKeyModal();
            } else {
                alert('Error: ' + data.error);
            }
        })
        .catch(err => {
            alert('Failed to save API key: ' + err.message);
        });
    }

    // AI Matching Functions
    function runAIMatching() {
        const jobSelect = document.getElementById('job-select');
        const jobId = jobSelect.value;

        if (!jobId) {
            alert('Please select a job from the dropdown first');
            return;
        }

        const btnText = document.getElementById('ai-btn-text');
        const loading = document.getElementById('ai-loading');
        btnText.style.display = 'none';
        loading.style.display = 'inline-block';

        fetch('${pageContext.request.contextPath}/mo/applications', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                action: 'ai-match',
                jobId: jobId
            })
        })
        .then(res => res.json())
        .then(data => {
            btnText.style.display = 'inline';
            loading.style.display = 'none';

            if (data.success) {
                displayAIResults(data.results);
            } else {
                alert('Error: ' + data.error);
            }
        })
        .catch(err => {
            btnText.style.display = 'inline';
            loading.style.display = 'none';
            alert('AI matching failed: ' + err.message);
        });
    }

    function displayAIResults(results) {
        const container = document.getElementById('ai-results');
        container.innerHTML = '';

        if (results.length === 0) {
            container.innerHTML = '<p style="color: white;">No pending applications to analyze.</p>';
            container.classList.add('active');
            return;
        }

        results.forEach((result, index) => {
            if (result.error) {
                const errorCard = document.createElement('div');
                errorCard.className = 'ai-result-card';
                errorCard.innerHTML = '<div style="color: #dc2626;"><strong>' +
                    result.taName + '</strong>: ' + result.error + '</div>';
                container.appendChild(errorCard);
                return;
            }

            const card = document.createElement('div');
            card.className = 'ai-result-card';

            const scoreClass = result.matchingScore >= 75 ? 'high' :
                              result.matchingScore >= 50 ? 'medium' : 'low';

            let skillsHtml = '';
            if (result.matchedSkills && result.matchedSkills.length > 0) {
                skillsHtml = '<div class="matched-skills">' +
                    result.matchedSkills.map(skill =>
                        '<span class="skill-tag">' + skill + '</span>'
                    ).join('') +
                    '</div>';
            }

            card.innerHTML = `
                <div class="ai-result-header">
                    <div>
                        <strong style="font-size: 16px;">#` + (index + 1) + ` ` + result.taName + `</strong>
                        <div style="font-size: 12px; color: #64748b; margin-top: 4px;">
                            Application ID: ` + result.applicationId + `
                        </div>
                    </div>
                    <div class="ai-score ` + scoreClass + `">` + result.matchingScore + `</div>
                </div>
                ` + skillsHtml + `
                <div class="ai-reason">` + result.reason + `</div>
            `;

            container.appendChild(card);
        });

        container.classList.add('active');
    }

    // Load MO's jobs into dropdown on page load
    function loadJobsDropdown() {
        fetch('${pageContext.request.contextPath}/mo/applications?action=get-jobs')
            .then(res => res.json())
            .then(jobs => {
                const jobSelect = document.getElementById('job-select');
                jobs.forEach(job => {
                    const option = document.createElement('option');
                    option.value = job.jobId;
                    option.textContent = job.title;
                    jobSelect.appendChild(option);
                });
            })
            .catch(err => {
                console.error('Failed to load jobs:', err);
            });
    }

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', loadJobsDropdown);
</script>
</body>
</html>
