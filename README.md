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
| [joycegjy] | J郭佳仪 | 231222110 | 2023213199 | Member |
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
├── pom.xml                              # Maven config (dependencies, plugins)
├── README.md
├── data/                                # JSON data files (auto-created at runtime)
├── src/main/
│   ├── java/com/ebu6304/recruitment/
│   │   ├── RecruitmentApp.java          # Standalone demo (non-web)
│   │   ├── models/                      # Data models (User, TA, JobPosting, etc.)
│   │   ├── repositories/               # Data access layer (reads/writes JSON)
│   │   ├── services/                   # Business logic layer
│   │   ├── controllers/                # Legacy controller layer
│   │   ├── utils/                      # Utilities (JsonFileUtil, PasswordUtil, etc.)
│   │   └── web/                        # Web layer (Servlets + Filter)
│   │       ├── AppInitializer.java     # Initializes services on startup
│   │       ├── AuthFilter.java         # Authentication filter
│   │       └── servlet/
│   │           ├── LoginServlet.java
│   │           ├── RegisterServlet.java
│   │           ├── LogoutServlet.java
│   │           ├── TADashboardServlet.java
│   │           ├── JobServlet.java
│   │           ├── ApplicationServlet.java
│   │           └── TAProfileServlet.java
│   ├── resources/
│   │   └── static/                     # Original static HTML prototypes
│   └── webapp/
│       ├── index.jsp                   # Entry point (redirects to login)
│       └── WEB-INF/
│           ├── web.xml                 # Servlet mappings
│           └── jsp/
│               ├── login.jsp
│               ├── register.jsp
│               └── ta/
│                   ├── dashboard.jsp
│                   ├── job-market.jsp
│                   ├── applications.jsp
│                   ├── profile.jsp
│                   └── edit-profile.jsp
└── doc/
    ├── ProductBacklog_group39.xlsx
    ├── Prototype_group39.pdf
    └── Report_group39.pdf
```

---

## URL Routes

| URL | Method | Description |
|-----|--------|-------------|
| `/` | GET | Redirects to `/login` |
| `/login` | GET | Login page |
| `/login` | POST | Authenticate user |
| `/register` | GET | Registration page |
| `/register` | POST | Create TA account |
| `/logout` | GET | Logout and clear session |
| `/ta/dashboard` | GET | TA dashboard (stats + recent apps) |
| `/ta/jobs` | GET | Browse open job postings |
| `/ta/jobs` | POST | Submit a job application (JSON response) |
| `/ta/applications` | GET | View my applications |
| `/ta/applications` | POST | Withdraw an application (JSON response) |
| `/ta/profile` | GET | View TA profile |
| `/ta/profile?action=edit` | GET | Edit profile form |
| `/ta/profile` | POST | Save profile changes |

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
