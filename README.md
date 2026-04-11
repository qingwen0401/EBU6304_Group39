# BUPT TA Recruitment System

A **Teaching Assistant (TA) Recruitment Management System** for BUPT International School.  
Built with **Java Servlet + JSP + Maven**, using JSON files for data storage (no database required).  
Supports three user roles: **Module Organiser (MO)**, **Teaching Assistant (TA)**, and **Administrator (Admin)**.

---

## Group 39 Members

| GitHub | Name | QMID | BUPTID | Role |
|--------|------|------|--------|------|
| [qingwen0401] | 尹晴尚 | 231222992 | 2023213197 | Leader |
| [Fzx501] | 方紫熙 | 231222981 | 2023213198 | Member |
| [Baooo118] | 杨舒涵 | 231222350 | 2023213204 | Member |
| [joycegjy] | 郭佳仪 | 231222110 | 2023213199 | Member |
| [manyuemei0423] | 吕雨蔓 | 231220862 | 2023213200 | Member |
| [willing] | 张乐怡 | 231220161 | 2023213203 | Member |
| Shuyue-Xing | 邢舒悦 | - | - | Support TA |

---

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| JDK | 17 or 21 (recommended) | https://adoptium.net/ |
| Maven | 3.8+ (bundled in IDEA, no separate install needed) | - |
| IntelliJ IDEA | Community or Ultimate | https://www.jetbrains.com/idea/ |

---

## How to Run the Web Application

This project is a **JSP + Servlet Web Application** packaged as a WAR file. It requires a Servlet container (Tomcat 10.x) to run. Three startup methods are provided below.

---

### Method 1: Maven `cargo:run` (Recommended - No Tomcat install needed)

This is the simplest method. Maven automatically downloads and runs an embedded Tomcat 10 instance. No separate Tomcat installation required.

**From the command line:**

```bash
# Clone the repository
git clone https://github.com/qingwen0401/EBU6304_Group39.git
cd EBU6304_Group39

# Build and start the embedded Tomcat server
mvn cargo:run
```

**From IntelliJ IDEA (any edition):**

1. Open the project in IDEA (File -> Open -> select the project folder)
2. Wait for Maven to finish importing (bottom progress bar)
3. Open the **Maven** panel on the right sidebar (View -> Tool Windows -> Maven)
4. Expand **Plugins -> cargo**
5. Double-click **cargo:run**
6. Wait for the console to show: `Press Ctrl-C to stop the container...`

**Access the application:** http://localhost:8080/

> Note: The first run downloads Tomcat 10 dependencies (~20MB). Ensure you have internet access.
> If downloads are slow, configure the Aliyun Maven mirror (see FAQ below).

**To stop the server:** Press `Ctrl+C` in the terminal, or click the red Stop button in IDEA.

---

### Method 2: IntelliJ IDEA with Local Tomcat (IDEA Ultimate only)

> Requires **IntelliJ IDEA Ultimate** edition (Community edition does not support Tomcat integration).
> Requires **Tomcat 10.x** installed locally. Download: https://tomcat.apache.org/download-10.cgi

**Steps:**

1. Download and extract Tomcat 10.x to a local directory (e.g. `C:\tomcat10` or `/opt/tomcat10`)

2. Build the WAR file first:
   ```bash
   mvn clean package
   ```
   The WAR file will be at: `target/ta-recruitment-system-1.0.0.war`

3. Configure a Run Configuration in IDEA:
   - Menu: **Run -> Edit Configurations**
   - Click **+** (top left) -> **Tomcat Server -> Local**
   - Set **Name**: `Tomcat 10`
   - Click **Configure** next to "Application server" -> select your Tomcat installation directory
   - Switch to the **Deployment** tab
   - Click **+** -> **Artifact** -> select `ta-recruitment-system-1.0.0:war`
   - Set **Application context** to `/` (root)
   - Click **OK**

4. Click the green **Run** button (or press `Shift+F10`)

5. IDEA will open the browser automatically at http://localhost:8080/

---

### Method 3: Deploy WAR to Tomcat Manually

Use this method if you have Tomcat 10.x already installed and running.

1. Build the WAR file:
   ```bash
   mvn clean package
   ```

2. Copy the WAR file to Tomcat's `webapps` directory:
   ```bash
   # Windows example
   copy target\ta-recruitment-system-1.0.0.war C:\tomcat10\webapps\ROOT.war

   # Linux/Mac example
   cp target/ta-recruitment-system-1.0.0.war /opt/tomcat10/webapps/ROOT.war
   ```
   > Naming it `ROOT.war` deploys it at the root context `/`. You can also name it anything else (e.g. `ta.war`) and access it at `/ta`.

3. Start Tomcat:
   ```bash
   # Windows
   C:\tomcat10\bin\startup.bat

   # Linux/Mac
   /opt/tomcat10/bin/startup.sh
   ```

4. Access the application: http://localhost:8080/

5. To stop Tomcat:
   ```bash
   # Windows
   C:\tomcat10\bin\shutdown.bat

   # Linux/Mac
   /opt/tomcat10/bin/shutdown.sh
   ```

---

## Data Storage

All data is stored as JSON files. By default they are created in the `data/` directory relative to where Tomcat is started:

- `data/ta_profiles.json` - TA user accounts and profiles
- `data/mo_profiles.json` - Module Organiser accounts
- `data/jobs.json` - Job postings
- `data/applications.json` - TA applications
- `data/workload_records.json` - Workload tracking

> When running via `mvn cargo:run`, data files are created in the project root `data/` directory.
> When running via a standalone Tomcat, data files are created in Tomcat's working directory.

---

## Project Structure

```
EBU6304_Group39/
├── pom.xml                              # Maven configuration
├── README.md
├── data/                                # JSON data storage (auto-created)
│   ├── ta_profiles.json
│   ├── mo_profiles.json
│   ├── admin_profiles.json
│   ├── jobs.json
│   ├── applications.json
│   ├── workload_records.json
│   └── job_templates.json
├── src/main/
│   ├── java/com/ebu6304/recruitment/
│   │   ├── models/                      # Domain Models (8 files)
│   │   │   ├── User.java               # Base user entity
│   │   │   ├── TA.java                 # Teaching Assistant
│   │   │   ├── ModuleOrganiser.java    # Module Organiser
│   │   │   ├── JobPosting.java         # Job posting entity
│   │   │   ├── Application.java        # Application entity
│   │   │   ├── WorkloadRecord.java     # Workload tracking
│   │   │   ├── JobTemplate.java        # Job template entity
│   │   │   └── CvFileData.java         # CV file data transfer object
│   │   ├── repositories/               # Data Access Layer (4 files)
│   │   │   ├── UserRepository.java     # User CRUD operations
│   │   │   ├── JobRepository.java      # Job CRUD operations
│   │   │   ├── ApplicationRepository.java  # Application CRUD operations
│   │   │   └── WorkloadRepository.java # Workload CRUD operations
│   │   ├── services/                   # Business Logic Layer (4 files)
│   │   │   ├── AuthService.java        # Authentication & authorization
│   │   │   ├── JobService.java         # Job posting management
│   │   │   ├── ApplicationService.java # Application processing
│   │   │   └── WorkloadService.java    # Workload tracking
│   │   ├── controllers/                # Business Controllers (5 files)
│   │   │   ├── TAController.java       # TA-specific operations
│   │   │   ├── MOJobController.java    # MO job management
│   │   │   ├── MOApplicationController.java  # MO application review
│   │   │   ├── AdminController.java    # Admin operations
│   │   │   └── ControllerResult.java   # Controller response wrapper
│   │   ├── utils/                      # Utilities (3 files)
│   │   │   ├── JsonFileUtil.java       # JSON file I/O operations
│   │   │   ├── PasswordUtil.java       # Password hashing (SHA-256)
│   │   │   └── IdGenerator.java        # Unique ID generation
│   │   └── web/                        # Web Layer (17 files)
│   │       ├── AppInitializer.java     # ServletContextListener (DI container)
│   │       ├── AuthFilter.java         # Authentication filter
│   │       └── servlet/                # Servlets (15 files - THE BACKEND)
│   │           ├── LoginServlet.java
│   │           ├── RegisterServlet.java
│   │           ├── LogoutServlet.java
│   │           ├── TADashboardServlet.java
│   │           ├── TAProfileServlet.java
│   │           ├── JobServlet.java
│   │           ├── ApplicationServlet.java
│   │           ├── CvUploadServlet.java
│   │           ├── CvViewServlet.java
│   │           ├── MODashboardServlet.java
│   │           ├── MOCreateJobServlet.java
│   │           ├── MOApplicationReviewServlet.java
│   │           ├── MOAnalyticsServlet.java
│   │           ├── MOTemplateServlet.java
│   │           └── AdminDashboardServlet.java
│   └── webapp/
│       ├── index.jsp                   # Entry point
│       └── WEB-INF/
│           ├── web.xml                 # Servlet mappings & filters
│           └── jsp/                    # JSP views (13 files)
│               ├── login.jsp
│               ├── register.jsp
│               ├── ta/                 # TA views (5 files)
│               │   ├── dashboard.jsp
│               │   ├── job-market.jsp
│               │   ├── applications.jsp
│               │   ├── profile.jsp
│               │   └── edit-profile.jsp
│               ├── mo/                 # MO views (5 files)
│               │   ├── dashboard.jsp
│               │   ├── create-job.jsp
│               │   ├── applications.jsp
│               │   ├── analytics.jsp
│               │   └── templates.jsp
│               └── admin/              # Admin views (1 file)
│                   └── dashboard.jsp
└── doc/
    ├── ProductBacklog_group39.xlsx
    ├── Prototype_group39.pdf
    └── Report_group39.pdf
```

---

## Architecture: Layered Design Explained

This project follows a **classic 4-tier architecture** without using Spring Boot or any framework. All layers are manually implemented using pure Java Servlets.

###  Layer Overview

```
┌─────────────────────────────────────────────────────────┐
│  WEB LAYER (Servlets)                                   │  ← HTTP Entry Point
│  • Receives HTTP requests                               │
│  • Calls Services/Controllers                           │
│  • Forwards to JSP views                                │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  SERVICE LAYER (Business Logic)                         │  ← Core Logic
│  • AuthService, JobService, ApplicationService          │
│  • Validates business rules                             │
│  • Orchestrates multiple repositories                   │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  REPOSITORY LAYER (Data Access)                         │  ← Data Persistence
│  • UserRepository, JobRepository, etc.                  │
│  • CRUD operations on JSON files                        │
│  • No business logic, pure data access                  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  MODEL LAYER (Domain Entities)                          │  ← Data Structures
│  • User, TA, JobPosting, Application, etc.              │
│  • POJOs with getters/setters                           │
│  • No logic, just data containers                       │
└─────────────────────────────────────────────────────────┘
```

###  Why This Layering?

#### 1. **Models** (Domain Entities)
**Purpose**: Define the data structures that represent business concepts.

**Why separate?**
- **Single Responsibility**: Each model represents ONE business entity
- **Reusability**: Models are used across all layers
- **Type Safety**: Strongly-typed objects prevent data errors

**Example**: `Application.java` defines what an application IS (fields, status constants), but NOT how to save it or process it.

#### 2. **Repositories** (Data Access Layer)
**Purpose**: Handle ALL data persistence operations (read/write JSON files).

**Why separate?**
- **Separation of Concerns**: Data access logic is isolated from business logic
- **Testability**: Can mock repositories to test services without touching files
- **Flexibility**: Can switch from JSON to database without changing services

**Example**: `ApplicationRepository.java` knows HOW to save/load applications from JSON, but NOT WHEN or WHY to save them.

**Actual Usage in Servlets**: ❌ **NOT directly called**. Servlets call Services, which call Repositories.

#### 3. **Services** (Business Logic Layer)
**Purpose**: Implement core business rules and orchestrate operations.

**Why separate?**
- **Business Rules Centralization**: All validation, authorization, and workflow logic in one place
- **Transaction Management**: Services coordinate multiple repository calls
- **Reusability**: Same service methods used by multiple servlets

**Example**: `ApplicationService.acceptApplication()` validates permissions, checks job vacancies, updates application status, creates workload record, and updates job filled count — orchestrating multiple repositories.

**Actual Usage in Servlets**: ✅ **HEAVILY USED**. 13 out of 15 servlets directly call services via `AppInitializer.getXxxService()`.

**Example from MODashboardServlet.java:34-36**:
```java
this.jobService = AppInitializer.getJobService();
this.applicationService = AppInitializer.getApplicationService();
```

#### 4. **Controllers** (Optional Business Coordinators)
**Purpose**: Provide higher-level business operations that combine multiple services.

**Why separate?**
- **Complex Workflows**: Some operations need coordination across multiple services
- **Code Reuse**: Avoid duplicating complex logic in multiple servlets
- **Cleaner Servlets**: Keep servlets thin by delegating complex operations

**Example**: `MOJobController.createJob()` validates input, calls `JobService.postJob()`, and returns a structured result.

**Actual Usage in Servlets**: ⚠️ **PARTIALLY USED**. Only 4 out of 15 servlets use controllers (MOCreateJobServlet, MOApplicationReviewServlet, CvUploadServlet, CvViewServlet). Most servlets call services directly.

**Why not always used?** Controllers are optional — when a servlet only needs one service call, it calls the service directly. Controllers are only used for complex multi-step operations.

#### 5. **Servlets** (Web Layer / HTTP Handlers)
**Purpose**: Handle HTTP requests and responses.

**Why separate?**
- **Web-Specific Logic**: HTTP parsing, session management, request/response handling
- **Thin Layer**: Servlets should be thin — just route requests to services and forward to views
- **Framework Boundary**: Isolates web framework (Servlet API) from business logic

**Example**: `MODashboardServlet.doGet()` gets the current user from session, calls services to fetch data, puts data in request attributes, and forwards to JSP.

**Actual Usage**: ✅ **THE BACKEND**. All 15 servlets are the actual HTTP endpoints. They are mapped in `web.xml` to URL patterns.

---

### 🔄 Dependency Injection (Manual)

Since we don't use Spring Boot, dependency injection is done manually via `AppInitializer.java`:

**AppInitializer.java:32-73** (ServletContextListener):
```java
// 1. Create repositories
UserRepository userRepository = new UserRepository();
JobRepository jobRepository = new JobRepository();
ApplicationRepository applicationRepository = new ApplicationRepository();
WorkloadRepository workloadRepository = new WorkloadRepository();

// 2. Create services (inject repositories)
AuthService authService = new AuthService(userRepository);
JobService jobService = new JobService(jobRepository, userRepository);
ApplicationService applicationService = new ApplicationService(
    applicationRepository, jobRepository, userRepository, workloadRepository);
WorkloadService workloadService = new WorkloadService(workloadRepository, userRepository);

// 3. Store in ServletContext (acts as DI container)
ctx.setAttribute("authService", authService);
ctx.setAttribute("jobService", jobService);
ctx.setAttribute("applicationService", applicationService);
ctx.setAttribute("workloadService", workloadService);
```

**Servlets retrieve services via static helper methods**:
```java
// In MODashboardServlet.init()
this.jobService = AppInitializer.getJobService();
this.applicationService = AppInitializer.getApplicationService();
```

---

### 📊 Layer Usage Statistics

| Layer | Files | Used by Servlets? | Usage Pattern |
|-------|-------|-------------------|---------------|
| **Models** | 8 | ✅ Yes | Data containers passed between layers |
| **Repositories** | 4 | ❌ No | Only called by Services |
| **Services** | 4 | ✅ Yes (13/15) | Primary business logic entry point |
| **Controllers** | 5 | ⚠️ Partial (4/15) | Optional for complex workflows |
| **Utils** | 3 | ✅ Yes | Helper functions (ID generation, JSON I/O, password hashing) |
| **Servlets** | 15 | N/A | **THE BACKEND** - all HTTP endpoints |

---

### 🎯 Key Takeaway

**Servlets ARE the backend** — they are the HTTP entry points. But they are kept thin by delegating to:
- **Services** for business logic (heavily used)
- **Controllers** for complex multi-service operations (occasionally used)
- **Repositories** for data access (never called directly by servlets)

This layering ensures:
- ✅ **Testability**: Each layer can be tested independently
- ✅ **Maintainability**: Changes to one layer don't affect others
- ✅ **Clarity**: Each class has a single, clear responsibility

---

## URL Routes

### Authentication & Common
| URL | Method | Description |
|-----|--------|-------------|
| `/` | GET | Redirects to `/login` |
| `/login` | GET/POST | Login page / Authenticate user |
| `/register` | GET/POST | Registration page / Create TA account |
| `/logout` | GET | Logout and clear session |

### TA Routes
| URL | Method | Description |
|-----|--------|-------------|
| `/ta/dashboard` | GET | TA dashboard (stats + recent apps) |
| `/ta/jobs` | GET | Browse open job postings |
| `/ta/jobs` | POST | Submit a job application (JSON response) |
| `/ta/applications` | GET | View my applications |
| `/ta/applications` | POST | Withdraw an application (JSON response) |
| `/ta/profile` | GET | View TA profile |
| `/ta/profile?action=edit` | GET | Edit profile form |
| `/ta/profile` | POST | Save profile changes |
| `/api/ta/cv/upload` | POST | Upload CV file |
| `/api/ta/cv/view` | GET | View/download CV file |

### MO (Module Organiser) Routes
| URL | Method | Description |
|-----|--------|-------------|
| `/mo/dashboard` | GET | MO dashboard with overview |
| `/mo/create-job` | GET/POST | Create new job posting form / Submit job |
| `/mo/applications` | GET | View all applications for MO's jobs |
| `/mo/applications/review` | POST | Review application (accept/reject) |
| `/mo/applications/bulk-reject` | POST | Bulk reject multiple applications |
| `/mo/analytics` | GET | Recruitment analytics & statistics |
| `/mo/templates` | GET | Job templates library |
| `/mo/templates/save` | POST | Save job posting as template |
| `/mo/templates/use` | POST | Use template to create new job |

### Admin Routes
| URL | Method | Description |
|-----|--------|-------------|
| `/admin/dashboard` | GET | Admin dashboard (system overview) |

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 |
| Web Layer | Jakarta Servlet 6.0 + JSP 3.1 |
| Template Engine | JSTL 3.0 |
| Build Tool | Maven 3.8+ |
| Embedded Server | Tomcat 10.x (via Cargo Maven plugin) |
| Data Storage | JSON files (Gson library) |
| No database | No Spring Boot | No external services required |

---

## FAQ

**Q: `mvn cargo:run` fails with "port 8080 already in use"?**  
A: Another process is using port 8080. Either stop that process, or change the port in `pom.xml`:
```xml
<cargo.servlet.port>8090</cargo.servlet.port>
```
Then access the app at http://localhost:8090/

**Q: IDEA shows "Cannot resolve symbol" errors?**  
A: Wait for Maven dependency download to complete (bottom progress bar), or click the refresh button in the Maven panel.

**Q: Data files are in the wrong location?**  
A: Delete all `.json` files in the `data/` directory and restart the server. They will be recreated automatically.

**Q: Maven downloads are slow?**  
A: Configure the Aliyun Maven mirror. In IDEA: File -> Settings -> Build -> Maven -> User settings file, point to a `settings.xml` containing:
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

**Q: Can I still run the standalone demo (non-web)?**  
A: Yes. Right-click `RecruitmentApp.java` in IDEA and select "Run 'RecruitmentApp.main()'". This runs a console demo of the backend logic without starting a web server.
