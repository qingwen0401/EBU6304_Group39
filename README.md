# BUPT TA Recruitment System

A **Teaching Assistant (TA) Recruitment Management System** for BUPT International School.  
Built with **pure Java + Maven**, using JSON files for data storage (no database required).  
Supports three user roles: **Module Organiser (MO)**, **Teaching Assistant (TA)**, and **Administrator (Admin)**.

---

## 📋 Group 39 Members

| GitHub | Name | QMID | BUPTID | Role |
|--------|------|------|--------|------|
| [qingwen0401] | 尹晴尚 Qingshang Yin | 231222992 | 2023213197 | Leader |
| [Fzx501] | 方紫熙 Zixi Fang | 231222981 | 2023213198 | Member |
| [Baooo118] | 杨舒涵 Shuhan Yang | 231222350 | 2023213204 | Member |
| [joycegjy] | 郭佳仪 Jiayi Guo | 231222110 | 2023213199 | Member |
| [manyuemei0423] | 吕雨蔓 Yuman Lyu | 231220862 | 2023213200 | Member |
| [willing] | 张乐怡 Leyi Zhang | 231220161 | 2023213203 | Member |
| Shuyue-Xing | 邢书悦 | — | — | Support TA |

---

## 🚀 Quick Start（推荐使用 IntelliJ IDEA 打开）

### 前置要求

| 工具 | 版本要求 | 下载地址 |
|------|----------|----------|
| JDK | 17 或 21（推荐） | https://adoptium.net/ |
| Maven | 3.8+（IDEA 内置，无需单独安装） | — |
| IntelliJ IDEA | Community 或 Ultimate | https://www.jetbrains.com/idea/ |

---

### 第一步：克隆仓库

```bash
git clone https://github.com/qingwen0401/EBU6304_Group39.git
cd EBU6304_Group39
```

---

### 第二步：用 IDEA 打开项目（重要！）

1. 打开 IntelliJ IDEA
2. 点击菜单 **File → Open**
3. 选择克隆下来的 `EBU6304_Group39` **文件夹**（不是某个具体文件）
4. IDEA 会自动检测到 `pom.xml`，弹出提示 **"Maven build scripts found"**
5. 点击 **"Load Maven Project"** / **"Trust Project"**
6. 等待右下角进度条完成（Maven 自动下载依赖，首次约需 1~3 分钟）

> ⚠️ **注意**：必须以"文件夹"方式打开，不要直接打开某个 `.java` 文件。

---

### 第三步：配置 JDK

如果 IDEA 提示 "Project JDK is not defined"：

1. 菜单 **File → Project Structure**（快捷键 `Ctrl+Alt+Shift+S`）
2. 左侧选 **Project**
3. **SDK** 下拉框选择你本机安装的 JDK（17 或 21）
4. 如果列表为空，点击 **"Add SDK → JDK"**，手动选择 JDK 安装目录
5. 点击 **OK** 保存

---

### 第四步：编译项目

**方式 A：使用 IDEA 内置 Maven（推荐）**

1. 点击右侧边栏的 **Maven** 面板（或菜单 View → Tool Windows → Maven）
2. 展开 **Lifecycle**
3. 双击 **compile**
4. 底部 Build 窗口显示 `BUILD SUCCESS` 即编译成功

**方式 B：使用 IDEA 菜单**

菜单 **Build → Build Project**（快捷键 `Ctrl+F9`）

**方式 C：命令行（在项目根目录执行）**

```bash
mvn compile
```

---

### 第五步：运行主程序

**方式 A：IDEA 直接运行（推荐）**

1. 在左侧 Project 面板中，展开：
   ```
   src/main/java/com/ebu6304/recruitment/
   ```
2. 找到 **`RecruitmentApp.java`**，右键点击
3. 选择 **"Run 'RecruitmentApp.main()'"**
4. 底部 Run 窗口会输出完整的演示流程

**方式 B：Maven 命令行运行**

```bash
mvn compile exec:java -Dexec.mainClass="com.ebu6304.recruitment.RecruitmentApp"
```

**方式 C：先打包再运行**

```bash
mvn package
java -jar target/recruitment-system-1.0.jar
```

---

### 预期输出

运行成功后，控制台应显示：

```
========== TA Recruitment System Demo ==========

[Step 1] Register Module Organiser...
[SUCCESS] MO registered: dr.smith

[Step 2] Register Teaching Assistant...
[SUCCESS] TA registered: alice.wang

[Step 3] MO posts a job...
[SUCCESS] Job posted successfully

[Step 4] TA browses available jobs...
[SUCCESS] Found 1 open jobs

[Step 5] TA applies for the job...
[SUCCESS] Application submitted successfully

[Step 6] MO reviews applications...
[SUCCESS] Retrieved 1 applications

[Step 7] MO accepts the application...
[SUCCESS] Application accepted successfully

[Step 8] TA checks application status...
[SUCCESS] Found 1 applications (status: ACCEPTED)

[Step 9] Admin checks workload...
[SUCCESS] Workload report: 1 TA, 8h/week, NORMAL

[Step 10] Admin checks system stats...
[SUCCESS] System stats: 1 job, 1 accepted application

========== Demo Complete ==========
```

---

## 📁 项目结构

```
EBU6304_Group39/
├── pom.xml                          # Maven 项目配置（依赖、JDK版本等）
├── README.md                        # 本文件
├── data/                            # JSON 数据文件（运行时自动生成/更新）
│   ├── applications.json
│   ├── jobs.json
│   ├── mo_profiles.json
│   ├── ta_profiles.json
│   └── workload_records.json
├── src/main/java/com/ebu6304/recruitment/
│   ├── RecruitmentApp.java          # 主入口，演示完整工作流
│   ├── models/                      # 数据模型层
│   │   ├── User.java
│   │   ├── ModuleOrganiser.java
│   │   ├── TA.java
│   │   ├── JobPosting.java
│   │   ├── Application.java
│   │   └── WorkloadRecord.java
│   ├── repositories/                # 数据访问层（读写 JSON 文件）
│   │   ├── UserRepository.java
│   │   ├── JobRepository.java
│   │   ├── ApplicationRepository.java
│   │   └── WorkloadRepository.java
│   ├── services/                    # 业务逻辑层
│   │   ├── AuthService.java
│   │   ├── JobService.java
│   │   ├── ApplicationService.java
│   │   └── WorkloadService.java
│   ├── controllers/                 # 控制层（封装请求/响应）
│   │   ├── MOJobController.java
│   │   ├── MOApplicationController.java
│   │   ├── TAController.java
│   │   ├── AdminController.java
│   │   └── ControllerResult.java
│   └── utils/                       # 工具类
│       ├── JsonFileUtil.java
│       ├── PasswordUtil.java
│       └── IdGenerator.java
└── doc/                             # 项目文档
    ├── ProductBacklog_group39.xlsx
    ├── Prototype_group39.pdf
    └── Report_group39.pdf
```

---

## ⚙️ 技术栈

| 组件 | 技术 |
|------|------|
| 语言 | Java 21 |
| 构建工具 | Maven 3.8+ |
| 数据存储 | JSON 文件（Gson 库） |
| 依赖管理 | `pom.xml` |
| 无需 | 数据库 / Spring Boot / 外部服务器 |

---

## ❓ 常见问题

**Q: IDEA 打开后报错 "Cannot resolve symbol"？**  
A: 等待 Maven 依赖下载完成（右下角进度条），或手动点击 Maven 面板的刷新按钮（🔄）。

**Q: 运行时报错 "Could not find or load main class"？**  
A: 确认已执行 `mvn compile` 或 Build Project，确保 `target/classes` 目录存在。

**Q: data/ 目录下的 JSON 文件内容不对？**  
A: 删除 `data/` 目录下所有 `.json` 文件，重新运行程序，会自动重新生成。

**Q: Maven 下载依赖很慢？**  
A: 在 IDEA 的 Maven 设置中配置国内镜像源（阿里云）：  
菜单 **File → Settings → Build → Maven → User settings file**，  
指向一个包含以下内容的 `settings.xml`：
```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <name>Aliyun Maven</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
