
// TA 个人资料 + 申请模块 核心逻辑
//格式校验 + 无自动创建申请 + 可撤销单个申请


// 个人资料存储键
const TA_PROFILE_KEY = "ta_profile";
// 申请记录存储键
const TA_APPLICATIONS_KEY = "ta_applications";
const TA_USER_ID_KEY = "ta_user_id";
const ALLOWED_CV_EXTENSIONS = [".pdf", ".doc"];
const API_BASE = window.location.protocol === "file:" ? "http://localhost:8081" : "";

/**
 * 读取个人资料
 */
function getProfile() {
    const saved = localStorage.getItem(TA_PROFILE_KEY);
    return saved ? JSON.parse(saved) : {
        fullName: "",
        studentId: "",
        email: "",
        degreeProgram: "BSc Computer Science",
        skills: "",
        availability: {},
        cvPath: ""
    };
}

/**
 * 保存个人资料
 */
function saveProfileToStorage(profile) {
    localStorage.setItem(TA_PROFILE_KEY, JSON.stringify(profile));
}

/**
 * 读取申请列表
 */
function getApplications() {
    const saved = localStorage.getItem(TA_APPLICATIONS_KEY);
    return saved ? JSON.parse(saved) : [];
}

/**
 * 保存申请列表
 */
function saveApplications(apps) {
    localStorage.setItem(TA_APPLICATIONS_KEY, JSON.stringify(apps));
}

/**
 * 页面加载：渲染个人资料
 */
function loadProfile() {
    const profile = getProfile();
    const container = document.getElementById("profileContent");

    if (!profile.fullName) {
        container.innerHTML = `
            <p style="font-size:13px; color:#666;">No profile yet. Please <a href="ta-edit.html" style="color:#0066cc;">create your profile</a> first.</p>
        `;
        return;
    }

    let html = `
        <div class="field">
            <label>Full Name</label>
            <div class="value">${profile.fullName}</div>
        </div>
        <div class="field">
            <label>Student ID</label>
            <div class="value">${profile.studentId}</div>
        </div>
        <div class="field">
            <label>University Email</label>
            <div class="value">${profile.email}</div>
        </div>
        <div class="field">
            <label>Degree Program</label>
            <div class="value">${profile.degreeProgram}</div>
        </div>
        <div class="field">
            <label>Skills</label>
            <div class="value">${profile.skills || "Not provided"}</div>
        </div>
        <div class="field">
            <label>CV Path</label>
            <div class="value">${profile.cvPath || "Not uploaded"}</div>
        </div>
        <div class="field">
            <label>Weekly Availability</label>
            <div class="value">${formatAvailability(profile.availability)}</div>
        </div>
    `;
    container.innerHTML = html;
}

/**
 * 格式化可用时间
 */
function formatAvailability(availability) {
    if (!availability) return "Not set";
    const timeMap = { morning: "Morning", afternoon: "Afternoon", evening: "Evening" };
    const dayMap = { mon: "Mon", tue: "Tue", wed: "Wed", thu: "Thu", fri: "Fri", sat: "Sat", sun: "Sun" };
    let result = [];
    for (const time in availability) {
        for (const day in availability[time]) {
            if (availability[time][day]) {
                result.push(`${dayMap[day]} ${timeMap[time]}`);
            }
        }
    }
    return result.join(", ") || "No available time";
}

/**
 * 加载资料到编辑框
 */
function loadEditForm() {
    const profile = getProfile();
    document.getElementById("fullName").value = profile.fullName;
    document.getElementById("studentId").value = profile.studentId;
    document.getElementById("email").value = profile.email;
    document.getElementById("degreeProgram").value = profile.degreeProgram;
    document.getElementById("skills").value = profile.skills;

    const checkboxes = document.querySelectorAll(".availability");
    checkboxes.forEach(cb => {
        const time = cb.dataset.time;
        const day = cb.dataset.day;
        cb.checked = profile.availability?.[time]?.[day] || false;
    });

    renderCurrentCvInfo(profile.cvPath);
}

// ======================
// 格式校验函数
// ======================
function validateName(name) {
    if (!name || name.length < 2) {
        alert("Invalid Name!\nFormat: At least 2 characters, letters only.\nExample: Tom Smith");
        return false;
    }
    if (/^\d+$/.test(name)) {
        alert("Invalid Name!\nName cannot be all numbers.");
        return false;
    }
    return true;
}

function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
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

// ======================
// 保存 Profile（无自动创建申请）
// ======================
async function saveProfile() {
    const fullName = document.getElementById("fullName").value.trim();
    const studentId = document.getElementById("studentId").value.trim();
    const email = document.getElementById("email").value.trim();

    if (!validateName(fullName)) return;
    if (!validateStudentId(studentId)) return;
    if (!validateEmail(email)) return;

    const cvFileInput = document.getElementById("cvFile");
    const cvFile = cvFileInput ? cvFileInput.files[0] : null;
    if (cvFile && !isAllowedCvFile(cvFile.name)) {
        alert("Invalid CV file type. Please upload a .pdf or .doc file.");
        return;
    }

    const availability = {};
    document.querySelectorAll(".availability").forEach(cb => {
        const time = cb.dataset.time;
        const day = cb.dataset.day;
        if (!availability[time]) availability[time] = {};
        availability[time][day] = cb.checked;
    });

    const profile = {
        fullName,
        studentId,
        email,
        degreeProgram: document.getElementById("degreeProgram").value,
        skills: document.getElementById("skills").value.trim(),
        availability,
        cvPath: getProfile().cvPath || ""
    };

    const taId = getCurrentTaId();
    if (cvFile) {
        try {
            profile.cvPath = await uploadCvFile(cvFile, taId);
        } catch (e) {
            alert("CV upload failed: " + e.message);
            return;
        }
    }

    saveProfileToStorage(profile);

    alert("Profile saved successfully!");
    location.href = "ta-profile.html";
}

function getCurrentTaId() {
    const existing = localStorage.getItem(TA_USER_ID_KEY);
    if (existing) return existing;
    const demoId = "TA-DEMO";
    localStorage.setItem(TA_USER_ID_KEY, demoId);
    return demoId;
}

function renderCurrentCvInfo(cvPath) {
    const info = document.getElementById("currentCvInfo");
    const viewBtn = document.getElementById("viewCvBtn");
    if (!info) return;
    if (!cvPath) {
        info.textContent = "Current CV: Not uploaded";
        if (viewBtn) viewBtn.style.display = "none";
        return;
    }
    const fileName = cvPath.split("/").pop() || cvPath;
    info.textContent = `Current CV: ${fileName} (kept unless you upload a new file)`;
    if (viewBtn) viewBtn.style.display = "inline-block";
}

function viewCurrentCv() {
    const profile = getProfile();
    if (!profile.cvPath) {
        alert("No CV uploaded yet.");
        return;
    }
    const url = `${API_BASE}/api/ta/cv/view?path=${encodeURIComponent(profile.cvPath)}`;
    window.open(url, "_blank", "noopener");
}

function isAllowedCvFile(fileName) {
    if (!fileName) return false;
    const lower = fileName.toLowerCase();
    return ALLOWED_CV_EXTENSIONS.some(ext => lower.endsWith(ext));
}

async function uploadCvFile(file, taId) {
    const formData = new FormData();
    formData.append("taId", taId);
    formData.append("cvFile", file);

    const response = await fetch(`${API_BASE}/api/ta/cv/upload`, {
        method: "POST",
        body: formData
    });
    const payload = await response.json();
    if (!response.ok || !payload.success) {
        throw new Error(payload.message || "Upload rejected");
    }
    return payload.data;
}

// ======================
// 申请列表页面
// ======================
function loadApplications() {
    const apps = getApplications();
    const activeBody = document.getElementById("activeAppsBody");
    const historyBody = document.getElementById("historyAppsBody");

    if (!activeBody) return;

    activeBody.innerHTML = apps.filter(a => a.status === "Pending").map(app => `
        <tr>
            <td>${app.module}</td>
            <td>${app.date}</td>
            <td><span class="status status-pending">${app.status}</span></td>
            <td>
                <button class="btn btn-view" onclick="viewApp('${app.module}')">View</button>
                <button class="btn btn-withdraw" onclick="withdrawApplication('${app.module}')" style="background:#dc2626;color:white;margin-left:5px;border:none;padding:3px 6px;border-radius:3px;cursor:pointer;">Withdraw</button>
            </td>
        </tr>
    `).join("");

    historyBody.innerHTML = apps.filter(a => a.status !== "Pending").map(app => `
        <tr>
            <td>2024/25</td>
            <td>Autumn</td>
            <td>${app.module.split(" - ")[0]}</td>
            <td>TA</td>
            <td>${app.date}</td>
            <td>${app.outcome}</td>
            <td>
                <button class="btn btn-view" onclick="viewFeedback('${app.module}')">View</button>
            </td>
        </tr>
    `).join("");
}

function viewApp(module) {
    const app = getApplications().find(a => a.module === module);
    alert(`Application\nModule: ${app.module}\nStatus: ${app.status}`);
}

function viewFeedback(module) {
    const app = getApplications().find(a => a.module === module);
    alert("Feedback: " + app.feedback);
}

// 撤销单个申请（核心功能）

function withdrawApplication(moduleName) {
    if (confirm(`⚠️ Are you sure you want to withdraw this application?\nCourse: ${moduleName}`)) {
        let apps = getApplications();
        apps = apps.filter(item => item.module !== moduleName);
        saveApplications(apps);
        alert("✅ Application withdrawn successfully!");
        location.reload();
    }
}

function switchTab(tab) {
    document.getElementById("activeApps").style.display = tab === "active" ? "block" : "none";
    document.getElementById("historyApps").style.display = tab === "history" ? "block" : "none";
    document.querySelectorAll(".tabs button").forEach(b => b.classList.remove("active"));
    event.target.classList.add("active");
}

// 监听通知点击变已读事件
document.addEventListener('DOMContentLoaded', function() {
    // 找到页面上所有带有 unread 类的通知卡片/列表项
    const unreadNotifications = document.querySelectorAll('.notification-item.unread');

    unreadNotifications.forEach(item => {
        item.addEventListener('click', function() {
            // 获取绑定在 HTML 标签上的通知 ID
            const notifId = this.getAttribute('data-id');

            // 如果没有 ID，说明数据绑错了，直接跳过
            if (!notifId) return;

            // 向我们刚写的 Servlet 发送请求
            fetch('/notifications/read', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    'notificationId': notifId
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // 后端修改成功后，前端去掉 unread 样式，加上 read 样式（比如消除红点）
                        this.classList.remove('unread');
                        this.classList.add('read');

                        // 可选：如果你顶部导航栏有未读消息数字（如 <span id="badgeCount">3</span>）
                        // 可以在这里获取该元素并减 1
                        const badge = document.getElementById('badgeCount');
                        if (badge) {
                            let count = parseInt(badge.innerText);
                            if (!isNaN(count) && count > 0) {
                                count--;
                                badge.innerText = count > 0 ? count : '';
                                if(count === 0) badge.style.display = 'none';
                            }
                        }
                    } else {
                        console.error('Failed to mark as read:', data.message);
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    });
});