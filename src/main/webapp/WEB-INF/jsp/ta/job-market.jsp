<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Job Market - TA Recruitment System</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f9fafb; }
        .sidebar {
            width: 120px; height: 100vh; background: #f0f0f0;
            padding: 20px 10px; position: fixed; left: 0; top: 0;
        }
        .sidebar p { font-size: 12px; font-weight: bold; margin-bottom: 15px; }
        .sidebar a { display: block; text-decoration: none; color: #333; font-size: 12px; margin: 8px 0; }
        .sidebar a.active { font-weight: bold; color: #0066cc; }
        .sidebar a:hover { color: #0066cc; }
        .logout-link { color: #dc2626 !important; margin-top: 20px; }
        .main { margin-left: 120px; padding: 20px; }
        .breadcrumb { font-size: 12px; color: #666; margin-bottom: 15px; }
        .job-card {
            background: white; border: 1px solid #e2e8f0; border-radius: 8px;
            padding: 18px; margin-bottom: 15px;
        }
        .job-title { font-size: 16px; font-weight: bold; margin-bottom: 8px; color: #1e293b; }
        .job-meta { font-size: 12px; color: #64748b; margin-bottom: 8px; }
        .job-tags { display: flex; gap: 8px; margin-bottom: 10px; flex-wrap: wrap; }
        .tag { background: #f1f5f9; padding: 3px 8px; border-radius: 3px; font-size: 11px; color: #475569; }
        .deadline { color: #dc2626; font-weight: bold; font-size: 13px; margin: 10px 0; }
        .btn-apply {
            background: #2563eb; color: white; border: none;
            padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 13px;
        }
        .btn-apply:hover { background: #1d4ed8; }
        .btn-applied {
            background: #94a3b8; color: white; border: none;
            padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: default;
        }
        .empty-msg { color: #94a3b8; font-size: 14px; padding: 40px; text-align: center; }
        /* Modal */
        .modal-overlay {
            display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.5); z-index: 1000; align-items: center; justify-content: center;
        }
        .modal-overlay.show { display: flex; }
        .modal {
            background: white; border-radius: 8px; padding: 30px; width: 480px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
        }
        .modal h3 { font-size: 16px; margin-bottom: 16px; color: #1e293b; }
        .modal textarea {
            width: 100%; padding: 10px; border: 1px solid #d1d5db; border-radius: 4px;
            font-size: 13px; resize: vertical; min-height: 100px;
        }
        .modal-buttons { display: flex; gap: 10px; justify-content: flex-end; margin-top: 16px; }
        .btn-cancel { background: #f1f5f9; color: #475569; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
        .btn-submit { background: #2563eb; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
        .toast {
            position: fixed; bottom: 20px; right: 20px; padding: 12px 20px;
            border-radius: 6px; color: white; font-size: 13px; z-index: 2000;
            display: none;
        }
        .toast.success { background: #22c55e; }
        .toast.error { background: #ef4444; }
    </style>
</head>
<body>
<div class="sidebar">
    <p>Navigation</p>
    <a href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/ta/jobs" class="active">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
    <a href="${pageContext.request.contextPath}/ta/profile">My Profile</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<div class="main">
    <div class="breadcrumb">Home &gt; Job Market</div>

    <c:choose>
        <c:when test="${empty jobs}">
            <div class="empty-msg">No open positions available at the moment. Please check back later.</div>
        </c:when>
        <c:otherwise>
            <c:forEach var="job" items="${jobs}">
                <div class="job-card">
                    <div class="job-title">${job.title}</div>
                    <div class="job-meta">
                        <c:if test="${not empty job.moduleCode}">Module: ${job.moduleCode} &nbsp;|&nbsp;</c:if>
                        <c:if test="${job.hoursPerWeek > 0}">Hours: ${job.hoursPerWeek}/week &nbsp;|&nbsp;</c:if>
                        <c:if test="${job.hourlyRate > 0}">Pay: ¥${job.hourlyRate}/hr</c:if>
                    </div>
                    <c:if test="${not empty job.description}">
                        <p style="font-size:13px;color:#475569;margin-bottom:10px;">${job.description}</p>
                    </c:if>
                    <div class="job-tags">
                        <c:if test="${not empty job.jobType}"><span class="tag">${job.jobType}</span></c:if>
                        <c:if test="${not empty job.semester}"><span class="tag">${job.semester}</span></c:if>
                        <c:forEach var="req" items="${job.requiredSkills}">
                            <span class="tag">${req}</span>
                        </c:forEach>
                    </div>
                    <c:if test="${not empty job.deadline}">
                        <div class="deadline">Deadline: ${job.deadline.length() >= 10 ? job.deadline.substring(0,10) : job.deadline}</div>
                    </c:if>
                    <c:choose>
                        <c:when test="${appliedJobIds.contains(job.jobId)}">
                            <button class="btn-applied" disabled>Already Applied</button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn-apply" onclick="openApplyModal('${job.jobId}', '${job.title}')">Apply Now</button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<!-- Apply Modal -->
<div class="modal-overlay" id="applyModal">
    <div class="modal">
        <h3>Apply for: <span id="modalJobTitle"></span></h3>
        <label style="font-size:13px;color:#374151;display:block;margin-bottom:6px;">Cover Letter (optional)</label>
        <textarea id="coverLetter" placeholder="Briefly describe why you are a good fit for this position..."></textarea>
        <div class="modal-buttons">
            <button class="btn-cancel" onclick="closeModal()">Cancel</button>
            <button class="btn-submit" onclick="submitApplication()">Submit Application</button>
        </div>
    </div>
</div>

<div class="toast" id="toast"></div>

<script>
    var currentJobId = null;

    function openApplyModal(jobId, jobTitle) {
        currentJobId = jobId;
        document.getElementById('modalJobTitle').textContent = jobTitle;
        document.getElementById('coverLetter').value = '';
        document.getElementById('applyModal').classList.add('show');
    }

    function closeModal() {
        document.getElementById('applyModal').classList.remove('show');
        currentJobId = null;
    }

    function submitApplication() {
        if (!currentJobId) return;
        var coverLetter = document.getElementById('coverLetter').value;

        var formData = new FormData();
        formData.append('jobId', currentJobId);
        formData.append('coverLetter', coverLetter);

        fetch('${pageContext.request.contextPath}/ta/jobs', {
            method: 'POST',
            body: formData
        })
        .then(function(r) { return r.json(); })
        .then(function(data) {
            closeModal();
            showToast(data.message, data.success ? 'success' : 'error');
            if (data.success) {
                setTimeout(function() { location.reload(); }, 1500);
            }
        })
        .catch(function() {
            showToast('Network error. Please try again.', 'error');
        });
    }

    function showToast(msg, type) {
        var t = document.getElementById('toast');
        t.textContent = msg;
        t.className = 'toast ' + type;
        t.style.display = 'block';
        setTimeout(function() { t.style.display = 'none'; }, 3000);
    }
</script>
</body>
</html>
