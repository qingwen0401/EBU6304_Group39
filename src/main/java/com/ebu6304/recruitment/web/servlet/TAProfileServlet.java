package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.UserRepository;
import com.ebu6304.recruitment.services.ApplicationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TA 个人档案 Servlet
 * GET  /ta/profile          → 显示档案页
 * GET  /ta/profile?action=edit → 显示编辑页
 * POST /ta/profile          → 保存档案（含文件上传），跳转回档案页
 *
 * @author Group39
 * @version 1.0
 */
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize       = 10 * 1024 * 1024, // 10 MB
    maxRequestSize    = 15 * 1024 * 1024  // 15 MB
)
public class TAProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        Optional<TA> taOpt = userRepository.findTAById(currentUser.getUserId());
        TA ta = taOpt.orElse(null);

        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            request.setAttribute("ta", ta);
            request.getRequestDispatcher("/WEB-INF/jsp/ta/edit-profile.jsp").forward(request, response);
        } else {
            request.setAttribute("ta", ta);
            request.getRequestDispatcher("/WEB-INF/jsp/ta/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        UserRepository userRepository =
                (UserRepository) getServletContext().getAttribute("userRepository");
        ApplicationService applicationService =
                (ApplicationService) getServletContext().getAttribute("applicationService");

        Optional<TA> taOpt = userRepository.findTAById(currentUser.getUserId());
        if (!taOpt.isPresent()) {
            response.sendRedirect(request.getContextPath() + "/ta/profile");
            return;
        }
        TA ta = taOpt.get();

        // 更新各字段（只更新非空值）
        String fullName   = request.getParameter("fullName");
        String email      = request.getParameter("email");
        String studentId  = request.getParameter("studentId");
        String degreeProgram = request.getParameter("degreeProgram");
        String skillsStr  = request.getParameter("skills");
        String bio        = request.getParameter("bio");

        if (fullName     != null && !fullName.trim().isEmpty())     ta.setFullName(fullName.trim());
        if (email        != null && !email.trim().isEmpty())        ta.setEmail(email.trim());
        if (studentId    != null && !studentId.trim().isEmpty())    ta.setStudentId(studentId.trim());
        if (degreeProgram != null)                                   ta.setMajor(degreeProgram.trim());
        if (bio          != null)                                    ta.setBio(bio.trim());

        // 解析技能列表（逗号或换行分隔）
        if (skillsStr != null && !skillsStr.trim().isEmpty()) {
            List<String> skills = Arrays.stream(skillsStr.split("[,\n]"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            ta.setSkills(skills);
        }

        // 处理每周可用时间（收集复选框值，构建JSON字符串）
        String[] times = {"morning", "afternoon", "evening"};
        String[] days  = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
        StringBuilder availJson = new StringBuilder("{");
        for (int ti = 0; ti < times.length; ti++) {
            if (ti > 0) availJson.append(",");
            availJson.append("\"").append(times[ti]).append("\":{");
            for (int di = 0; di < days.length; di++) {
                if (di > 0) availJson.append(",");
                String paramName = "avail_" + times[ti] + "_" + days[di];
                boolean checked = "on".equals(request.getParameter(paramName));
                availJson.append("\"").append(days[di]).append("\":").append(checked);
            }
            availJson.append("}");
        }
        availJson.append("}");
        ta.setAvailability(availJson.toString());

        // 处理 CV 文件上传
        try {
            Part cvPart = request.getPart("cvFile");
            if (cvPart != null && cvPart.getSize() > 0) {
                String submittedFileName = cvPart.getSubmittedFileName();
                if (submittedFileName != null && !submittedFileName.isBlank()) {
                    String savedPath = applicationService.uploadCv(
                            ta.getUserId(),
                            submittedFileName,
                            cvPart.getInputStream().readAllBytes()
                    );
                    ta.setCvPath(savedPath);
                }
            }
        } catch (Exception e) {
            // 文件上传失败不影响其他字段保存
            getServletContext().log("CV upload failed: " + e.getMessage());
        }

        userRepository.saveTA(ta);

        // 更新 session 中的用户信息
        request.getSession().setAttribute("currentUser", ta);

        response.sendRedirect(request.getContextPath() + "/ta/profile");
    }
}
