<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Job Market - TA Recruitment System</title>
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
            text-decoration: none;
            color: #dbeafe;
            font-size: 14px;
            margin: 8px 0;
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

        .jobs-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 18px;
        }

        .job-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 22px;
            box-shadow: 0 8px 22px rgba(15, 23, 42, 0.05);
            display: flex;
            flex-direction: column;
            min-height: 280px;
        }

        .job-title {
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 10px;
            color: #0f172a;
            line-height: 1.4;
        }

        .job-meta {
            font-size: 13px;
            color: #64748b;
            margin-bottom: 12px;
            line-height: 1.7;
        }

        .job-desc {
            font-size: 14px;
            color: #475569;
            margin-bottom: 14px;
            line-height: 1.7;
            min-height: 68px;
        }

        .job-tags {
            display: flex;
            gap: 8px;
            margin-bottom: 14px;
            flex-wrap: wrap;
        }

        .tag {
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            padding: 5px 10px;
            border-radius: 999px;
            font-size: 12px;
            color: #1d4ed8;
            font-weight: 700;
        }

        .deadline {
            color: #dc2626;
            font-weight: 700;
            font-size: 13px;
            margin: 10px 0 16px;
        }

        .card-footer {
            margin-top: auto;
        }

        .btn-apply,
        .btn-applied {
            border: none;
            padding: 11px 16px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 700;
            width: 100%;
        }

        .btn-apply {
            background: #2563eb;
            color: white;
            cursor: pointer;
            transition: 0.2s ease;
        }

        .btn-apply:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }

        .btn-applied {
            background: #cbd5e1;
            color: white;
            cursor: default;
        }

        .empty-msg {
            color: #64748b;
            font-size: 15px;
            padding: 42px 24px;
            text-align: center;
            border: 1px dashed #cbd5e1;
            border-radius: 18px;
            background: white;
        }

        .modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(15, 23, 42, 0.45);
            z-index: 1000;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal {
            background: white;
            border-radius: 18px;
            padding: 28px;
            width: 100%;
            max-width: 560px;
            box-shadow: 0 18px 48px rgba(15, 23, 42, 0.22);
        }

        .modal h3 {
            font-size: 22px;
            margin-bottom: 10px;
            color: #0f172a;
            line-height: 1.5;
        }

        .modal .modal-subtitle {
            font-size: 14px;
            color: #64748b;
            margin-bottom: 14px;
            line-height: 1.7;
        }

        .modal label {
            font-size: 14px;
            color: #334155;
            display: block;
            margin-bottom: 8px;
            font-weight: 700;
        }

        .modal textarea {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #cbd5e1;
            border-radius: 12px;
            font-size: 14px;
            resize: vertical;
            min-height: 120px;
            outline: none;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .modal textarea:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .modal-buttons {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
            margin-top: 18px;
            flex-wrap: wrap;
        }

        .btn-cancel,
        .btn-submit {
            border: none;
            padding: 11px 16px;
            border-radius: 10px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 700;
        }

        .btn-cancel {
            background: #e2e8f0;
            color: #334155;
        }

        .btn-cancel:hover {
            background: #cbd5e1;
        }

        .btn-submit {
            background: #2563eb;
            color: white;
        }

        .btn-submit:hover {
            background: #1d4ed8;
        }

        .toast {
            position: fixed;
            bottom: 24px;
            right: 24px;
            padding: 13px 20px;
            border-radius: 12px;
            color: white;
            font-size: 14px;
            font-weight: 700;
            z-index: 2000;
            display: none;
            box-shadow: 0 10px 24px rgba(15, 23, 42, 0.18);
        }

        .toast.success { background: #22c55e; }
        .toast.error { background: #ef4444; }

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
            .job-card,
            .modal {
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
    <a href="${pageContext.request.contextPath}/ta/jobs" class="active">Job Market</a>
    <a href="${pageContext.request.contextPath}/ta/applications">My Applications</a>
    <a href="${pageContext.request.contextPath}/ta/profile">My Profile</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<div class="main">
    <div class="page-header">
            <div class="breadcrumb">Home &gt; Job Market</div>
            <div class="page-title">Job Market</div>
            <div class="page-subtitle">
                Explore available TA opportunities, review key job details,
                and apply directly to positions that match your interests and skills.
            </div>
        </div>

        <div class="search-container" style="margin-bottom: 24px;">
            <div style="position: relative; max-width: 600px;">
                <svg style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); color: #64748b;" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                <input type="text" id="jobSearchInput" placeholder="Search by module code, title, tags..."
                       onkeyup="filterJobs()"
                       style="width: 100%; padding: 14px 16px 14px 48px; border-radius: 12px; border: 1px solid #cbd5e1; font-size: 15px; color: #1e293b; outline: none; transition: all 0.2s; box-shadow: 0 4px 6px rgba(15, 23, 42, 0.02);">
            </div>
        </div>


    <c:choose>
        <c:when test="${empty jobs}">
            <div class="empty-msg">No open positions available at the moment. Please check back later.</div>
        </c:when>
        <c:otherwise>
            <div class="jobs-grid">
                <c:forEach var="job" items="${jobs}">
                    <div class="job-card">
                        <div class="job-title">${job.title}</div>

                        <div class="job-meta">
                            <c:if test="${not empty job.moduleCode}">Module: ${job.moduleCode}<br></c:if>
                            <c:if test="${job.hoursPerWeek > 0}">Hours: ${job.hoursPerWeek}/week<br></c:if>
                            <c:if test="${job.hourlyRate > 0}">Pay: ¥${job.hourlyRate}/hr</c:if>
                        </div>

                        <c:if test="${not empty job.description}">
                            <div class="job-desc">${job.description}</div>
                        </c:if>

                        <div class="job-tags">
                            <c:if test="${not empty job.jobType}"><span class="tag">${job.jobType}</span></c:if>
                            <c:if test="${not empty job.semester}"><span class="tag">${job.semester}</span></c:if>
                            <c:forEach var="req" items="${job.requiredSkills}">
                                <span class="tag">${req}</span>
                            </c:forEach>
                        </div>

                        <c:if test="${not empty job.deadline}">
                            <div class="deadline">
                                Deadline: ${job.deadline.length() >= 10 ? job.deadline.substring(0,10) : job.deadline}
                            </div>
                        </c:if>

                        <div class="card-footer">
                            <c:choose>
                                <c:when test="${appliedJobIds.contains(job.jobId)}">
                                    <button class="btn-applied" disabled>Already Applied</button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn-apply" onclick="openApplyModal('${job.jobId}', '${job.title}')">Apply Now</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="modal-overlay" id="applyModal">
    <div class="modal">
        <h3>Apply for: <span id="modalJobTitle"></span></h3>
        <div class="modal-subtitle">
            You can optionally add a short cover letter to explain why you are a good fit for this position.
        </div>
        <label>Cover Letter (optional)</label>
        <textarea id="coverLetter" placeholder="Briefly describe why you are a good fit for this position..."></textarea>
        <div class="modal-buttons">
            <button class="btn-cancel" onclick="closeModal()">Cancel</button>
            <button class="btn-submit" onclick="submitApplication()">Submit Application</button>
        </div>
    </div>
</div>

<div class="toast" id="toast"></div>

<script>
    // ================= 新增：实时模糊搜索逻辑 =================
    function filterJobs() {
        // 1. 获取输入框里的文字并转换成小写
        var input = document.getElementById('jobSearchInput').value.toLowerCase();

        // 2. 拿到页面上所有的“职位卡片”
        var jobCards = document.querySelectorAll('.job-card');

        // 3. 遍历每一个卡片
        jobCards.forEach(function(card) {
            // 获取这个卡片里的所有纯文本内容（包含标题、描述、标签等）
            var cardText = card.innerText.toLowerCase();

            // 4. 判断逻辑：如果卡片文本包含输入框的字，就显示；否则隐藏
            if (cardText.includes(input)) {
                card.style.display = 'flex';
            } else {
                card.style.display = 'none';
            }
        });
    }
    // =========================================================


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

        var body = new URLSearchParams();
        body.append('jobId', currentJobId);
        body.append('coverLetter', coverLetter);

        fetch('${pageContext.request.contextPath}/ta/jobs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: body.toString()
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