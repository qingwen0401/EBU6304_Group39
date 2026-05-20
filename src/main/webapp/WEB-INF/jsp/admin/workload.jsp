<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workload Monitor - Admin Portal</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: Arial, sans-serif; }
        body { background: #f4f7fb; color: #1e293b; }

        .sidebar {
            width: 250px;
            min-height: 100vh;
            background: linear-gradient(180deg,#1e3a8a,#1d4ed8);
            padding: 28px 18px;
            position: fixed;
            left: 0;
            top: 0;
            color: white;
            box-shadow: 4px 0 18px rgba(15,23,42,.08);
        }

        .brand { font-size: 18px; font-weight: 700; margin-bottom: 8px; }
        .role {
            font-size: 12px;
            color: #bfdbfe;
            margin-bottom: 28px;
            padding-bottom: 20px;
            border-bottom: 1px solid rgba(255,255,255,.1);
        }

        .nav-title {
            font-size: 11px;
            letter-spacing: .08em;
            text-transform: uppercase;
            color: #bfdbfe;
            margin: 20px 0 12px;
            font-weight: 700;
        }

        .sidebar a {
            display: flex;
            color: #dbeafe;
            text-decoration: none;
            font-size: 14px;
            margin: 6px 0;
            padding: 11px 14px;
            border-radius: 10px;
            transition: .2s ease;
        }

        .sidebar a:hover { background: rgba(255,255,255,.12); color: white; }
        .sidebar a.active { background: white; color: #1d4ed8; font-weight: 700; }

        .logout-link {
            margin-top: 30px;
            color: #fecaca !important;
            border-top: 1px solid rgba(255,255,255,.1);
            padding-top: 20px !important;
        }

        .main {
            margin-left: 250px;
            padding: 32px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .page-title {
            font-size: 28px;
            font-weight: 700;
            color: #0f172a;
        }

        .panel, .stat-card {
            background: white;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0,0,0,.04);
        }

        .panel {
            padding: 24px;
            margin-bottom: 24px;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-bottom: 24px;
        }

        .stat-card { padding: 20px; }

        .stat-label {
            font-size: 13px;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: .5px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .stat-value {
            font-size: 32px;
            font-weight: 700;
            color: #0f172a;
        }

        .toolbar {
            display: grid;
            grid-template-columns: repeat(5, minmax(130px, 1fr));
            gap: 12px;
            align-items: end;
            margin-bottom: 18px;
        }

        label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #64748b;
            margin-bottom: 6px;
            text-transform: uppercase;
        }

        select, input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #cbd5e1;
            border-radius: 10px;
            background: white;
            color: #0f172a;
        }

        .btn {
            border: none;
            border-radius: 10px;
            padding: 10px 14px;
            background: #2563eb;
            color: white;
            font-weight: 700;
            text-decoration: none;
            cursor: pointer;
            text-align: center;
        }

        .btn.secondary { background: #0f172a; }
        .btn.ghost { background: #e2e8f0; color: #0f172a; }
        .btn.warning { background: #dc2626; }

        .btn.warning:hover {
            background: #b91c1c;
        }

        .btn.warning.outline {
            background: white;
            color: #dc2626;
            border: 1px solid #dc2626;
        }

        .btn.warning.outline:hover {
            background: #fef2f2;
        }

        .notify-meta {
            display: block;
            margin-top: 6px;
            font-size: 11px;
            color: #64748b;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px 10px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
            font-size: 14px;
            vertical-align: top;
        }

        th {
            color: #64748b;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: .05em;
        }

        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
            background: #dbeafe;
            color: #1d4ed8;
        }

        .status-OVERLOADED { background: #fee2e2; color: #dc2626; }
        .status-WARNING { background: #ffedd5; color: #ea580c; }
        .status-NORMAL { background: #dcfce7; color: #16a34a; }
        .status-IDLE { background: #e2e8f0; color: #475569; }

        .notice {
            background: #dcfce7;
            color: #166534;
            border: 1px solid #bbf7d0;
            padding: 12px 14px;
            border-radius: 10px;
            margin-bottom: 18px;
            font-weight: 700;
        }

        .notice.error {
            background: #fee2e2;
            color: #991b1b;
            border-color: #fecaca;
        }

        .inline-form {
            display: inline;
        }

        @media (max-width: 900px) {
            .sidebar {
                position: static;
                width: 100%;
                min-height: auto;
            }

            .main {
                margin-left: 0;
            }

            .stats-grid, .toolbar {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="brand">TA Recruitment</div>
    <div class="role">Administrator Portal</div>

    <div class="nav-title">Management</div>
    <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/workload" class="active">Workload Monitor</a>
    <a href="${pageContext.request.contextPath}/admin/jobs">Job Postings</a>
    <a href="${pageContext.request.contextPath}/admin/users">User Accounts</a>
    <a href="${pageContext.request.contextPath}/admin/audit">Audit Log</a>
    <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
</div>

<main class="main">
    <c:if test="${param.notified == '1' && param.again == '1'}">
        <div class="notice">Reminder notification has been sent to the selected TA.</div>
    </c:if>
    <c:if test="${param.notified == '1' && param.again != '1'}">
        <div class="notice">Notification has been sent to the selected TA.</div>
    </c:if>
    <c:if test="${param.notified == '2'}">
        <div class="notice">Workload has been force cancelled and notifications sent to TA and MO.</div>
    </c:if>
    <c:if test="${param.notified == '0'}">
        <div class="notice error">Failed to send notification. Please try again.</div>
    </c:if>

    <div class="topbar">
        <div>
            <h1 class="page-title">Workload Monitor</h1>
            <p class="stat-label">Filter by term, module, and workload status.</p>
        </div>
        <a class="btn secondary"
           href="${pageContext.request.contextPath}/admin/workload?semester=${semester}&module=${selectedModule}&status=${selectedStatus}&export=csv">
            Export CSV
        </a>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Max Weekly Threshold</div>
            <div class="stat-value">${maxWeeklyHours} hrs</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Warning Starts Above</div>
            <div class="stat-value">${warningWeeklyHours} hrs</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Fairness Index</div>
            <div class="stat-value">${fairnessIndex}</div>
        </div>
    </div>

    <section class="panel">
        <form class="toolbar" method="get" action="${pageContext.request.contextPath}/admin/workload">
            <div>
                <label>Term</label>
                <select name="semester">
                    <option value="">All terms</option>
                    <c:forEach var="option" items="${semesterOptions}">
                        <option value="${option}" ${option == semester ? 'selected' : ''}>${option}</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label>Module</label>
                <select name="module">
                    <option value="">All modules</option>
                    <c:forEach var="option" items="${moduleOptions}">
                        <option value="${option}" ${option == selectedModule ? 'selected' : ''}>${option}</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label>Status</label>
                <select name="status">
                    <option value="">All statuses</option>
                    <option value="IDLE" ${selectedStatus == 'IDLE' ? 'selected' : ''}>IDLE</option>
                    <option value="NORMAL" ${selectedStatus == 'NORMAL' ? 'selected' : ''}>NORMAL</option>
                    <option value="WARNING" ${selectedStatus == 'WARNING' ? 'selected' : ''}>WARNING</option>
                    <option value="OVERLOADED" ${selectedStatus == 'OVERLOADED' ? 'selected' : ''}>OVERLOADED</option>
                </select>
            </div>
            <button class="btn" type="submit">Apply</button>
            <a class="btn ghost" href="${pageContext.request.contextPath}/admin/workload">Clear</a>
        </form>

        <form class="toolbar" method="post" action="${pageContext.request.contextPath}/admin/workload">
            <div>
                <label>Configure Threshold</label>
                <input type="number" name="maxWeeklyHours" min="1" max="80" value="${maxWeeklyHours}">
            </div>
            <button class="btn secondary" type="submit">Save Threshold</button>
        </form>
    </section>

    <section class="panel">
        <table>
            <thead>
            <tr>
                <th>TA</th>
                <th>Student ID</th>
                <th>Modules</th>
                <th>Active Jobs</th>
                <th>Weekly Hours</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="ta" items="${workloadReport}">
                <tr>
                    <td>${ta.taName}</td>
                    <td>${ta.studentId}</td>
                    <td>${ta.modules}</td>
                    <td>${ta.jobCount}</td>
                    <td>${ta.totalWeeklyHours} / ${maxWeeklyHours} hrs</td>
                    <td>
                        <span class="badge status-${ta.workloadStatus}">
                                ${ta.workloadStatus}
                        </span>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${ta.workloadStatus == 'OVERLOADED'}">
                                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/workload">
                                    <input type="hidden" name="action" value="notifyOverload">
                                    <input type="hidden" name="taId" value="${ta.taId}">
                                    <input type="hidden" name="taName" value="${ta.taName}">
                                    <input type="hidden" name="totalWeeklyHours" value="${ta.totalWeeklyHours}">
                                    <input type="hidden" name="semester" value="${semester}">
                                    <input type="hidden" name="module" value="${selectedModule}">
                                    <input type="hidden" name="status" value="${selectedStatus}">
                                    <c:choose>
                                        <c:when test="${ta.workloadNotified}">
                                            <button class="btn warning outline" type="submit">Notify Again</button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn warning" type="submit">Notify TA</button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                                <c:if test="${ta.workloadNotified}">
                                    <span class="notify-meta">
                                        Sent ${ta.notificationCount} time(s),
                                        last: ${ta.lastNotifiedAt}
                                    </span>
                                </c:if>
                                <c:if test="${ta.notificationCount >= 3}">
                                    <button class="btn danger"
                                            onclick="showForceCancelModal('${ta.taId}', '${ta.taName}', '${semester}')">
                                        Force Cancel
                                    </button>
                                </c:if>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty workloadReport}">
                <tr><td colspan="7">No workload data matches the filters.</td></tr>
            </c:if>
            </tbody>
        </table>
    </section>

    <section class="panel">
        <div class="stat-label">Workload History</div>
        <table>
            <thead>
            <tr>
                <th>Created</th>
                <th>TA</th>
                <th>Job</th>
                <th>Module</th>
                <th>Term</th>
                <th>Hours</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="record" items="${records}">
                <tr>
                    <td>${record.createdAt}</td>
                    <td>${record.taName}</td>
                    <td>${record.jobTitle}</td>
                    <td>${record.moduleCode}</td>
                    <td>${record.semester}</td>
                    <td>${record.weeklyHours}</td>
                    <td><span class="badge">${record.status}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty records}">
                <tr><td colspan="7">No historical workload records found.</td></tr>
            </c:if>
            </tbody>
        </table>
    </section>
</main>

<div id="forceCancelModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:1000;">
    <div style="position:absolute; top:50%; left:50%; transform:translate(-50%,-50%); background:white; padding:32px; border-radius:14px; max-width:500px; width:90%;">
        <h2 style="margin-bottom:16px; color:#dc2626;">Force Cancel Workload</h2>
        <p style="margin-bottom:20px; color:#475569;">
            Select which workload assignments to cancel for <strong id="modalTaName"></strong>.
            Selected records will be cancelled and notifications sent to the TA and affected MOs.
        </p>
        <form id="forceCancelForm" method="post" action="${pageContext.request.contextPath}/admin/workload" onsubmit="return validateSelection();">
            <input type="hidden" name="action" value="forceCancel">
            <input type="hidden" name="taId" id="modalTaId">
            <input type="hidden" name="taName" id="modalTaNameHidden">
            <input type="hidden" name="semester" id="modalSemester">
            <input type="hidden" name="module" value="${selectedModule}">
            <input type="hidden" name="status" value="${selectedStatus}">
            <div id="recordsContainer"></div>
            <div style="margin-top:20px;">
                <label style="display:block; margin-bottom:8px; font-weight:600;">Reason (optional):</label>
                <textarea name="reason" style="width:100%; padding:10px; border:1px solid #e2e8f0; border-radius:8px; font-family:inherit;" rows="3" placeholder="Enter reason for cancellation..."></textarea>
            </div>
            <div style="margin-top:24px; display:flex; gap:12px; justify-content:flex-end;">
                <button type="button" class="btn secondary" onclick="closeForceCancelModal()">Cancel</button>
                <button type="submit" class="btn danger">Confirm Force Cancel</button>
            </div>
        </form>
    </div>
</div>

<script>
function showForceCancelModal(taId, taName, semester) {
    document.getElementById('modalTaName').textContent = taName;
    document.getElementById('modalTaId').value = taId;
    document.getElementById('modalTaNameHidden').value = taName;
    document.getElementById('modalSemester').value = semester;

    var recordsContainer = document.getElementById('recordsContainer');
    recordsContainer.innerHTML = '<p style="color:#64748b; font-size:14px;">Loading workload records...</p>';

    var records = [];
    <c:forEach var="ta" items="${workloadReport}">
        if ('${ta.taId}' === taId) {
            <c:forEach var="record" items="${ta.records}">
                <c:if test="${record.status == 'ACTIVE'}">
                    records.push({
                        recordId: '${record.recordId}',
                        moduleCode: '${record.moduleCode}',
                        jobTitle: '${record.jobTitle}',
                        weeklyHours: ${record.weeklyHours},
                        moId: '${record.moId}'
                    });
                </c:if>
            </c:forEach>
        }
    </c:forEach>

    if (records.length === 0) {
        recordsContainer.innerHTML = '<p style="color:#dc2626;">No active workload records found.</p>';
    } else {
        var html = '<div style="margin-bottom:16px;"><strong>Select workload records to cancel:</strong></div>';
        html += '<div style="max-height:200px; overflow-y:auto; border:1px solid #e2e8f0; border-radius:8px; padding:12px;">';
        records.forEach(function(record, index) {
            var checkboxId = 'record_' + index;
            html += '<label for="' + checkboxId + '" style="display:flex; padding:8px; border-bottom:1px solid #f1f5f9; cursor:pointer; align-items:center;">';
            html += '<input type="checkbox" id="' + checkboxId + '" name="recordId" value="' + record.recordId + '" ';
            html += 'data-module="' + record.moduleCode + '" data-mo="' + record.moId + '" ';
            html += 'style="margin-right:12px; width:18px; height:18px; cursor:pointer;" checked>';
            html += '<div style="flex:1; display:flex; justify-content:space-between;">';
            html += '<div><strong>' + record.moduleCode + '</strong><br><span style="font-size:13px; color:#64748b;">' + record.jobTitle + '</span></div>';
            html += '<div style="text-align:right; padding-left:12px;">' + record.weeklyHours + ' hrs/week</div>';
            html += '</div>';
            html += '</label>';
        });
        html += '</div>';
        recordsContainer.innerHTML = html;
    }

    document.getElementById('forceCancelModal').style.display = 'block';
}

function closeForceCancelModal() {
    document.getElementById('forceCancelModal').style.display = 'none';
}

function validateSelection() {
    var checkboxes = document.querySelectorAll('#recordsContainer input[type="checkbox"]:checked');
    if (checkboxes.length === 0) {
        alert('Please select at least one workload record to cancel.');
        return false;
    }

    var form = document.getElementById('forceCancelForm');
    checkboxes.forEach(function(checkbox) {
        var moduleInput = document.createElement('input');
        moduleInput.type = 'hidden';
        moduleInput.name = 'moduleCode';
        moduleInput.value = checkbox.getAttribute('data-module');
        form.appendChild(moduleInput);

        var moInput = document.createElement('input');
        moInput.type = 'hidden';
        moInput.name = 'moId';
        moInput.value = checkbox.getAttribute('data-mo');
        form.appendChild(moInput);
    });

    return true;
}
</script>
</body>
</html>