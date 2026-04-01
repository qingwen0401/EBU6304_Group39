package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.TA;
import com.ebu6304.recruitment.models.User;
import com.ebu6304.recruitment.repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TA 个人档案 Servlet
 * GET  /ta/profile          → 显示档案页
 * GET  /ta/profile?action=edit → 显示编辑页
 * POST /ta/profile          → 保存档案，跳转回档案页
 *
 * @author Group39
 * @version 1.0
 */
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
        String department = request.getParameter("department");
        String major      = request.getParameter("major");
        String year       = request.getParameter("year");
        String gpaStr     = request.getParameter("gpa");
        String skillsStr  = request.getParameter("skills");
        String bio        = request.getParameter("bio");

        if (fullName   != null && !fullName.trim().isEmpty())   ta.setFullName(fullName.trim());
        if (email      != null && !email.trim().isEmpty())      ta.setEmail(email.trim());
        if (studentId  != null && !studentId.trim().isEmpty())  ta.setStudentId(studentId.trim());
        if (department != null)                                  ta.setDepartment(department.trim());
        if (major      != null)                                  ta.setMajor(major.trim());
        if (year       != null)                                  ta.setYear(year.trim());
        if (bio        != null)                                  ta.setBio(bio.trim());

        if (gpaStr != null && !gpaStr.trim().isEmpty()) {
            try {
                double gpa = Double.parseDouble(gpaStr.trim());
                if (gpa >= 0.0 && gpa <= 4.0) ta.setGpa(gpa);
            } catch (NumberFormatException ignored) { }
        }

        // 解析技能列表（逗号或换行分隔）
        if (skillsStr != null && !skillsStr.trim().isEmpty()) {
            List<String> skills = Arrays.stream(skillsStr.split("[,\n]"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
            ta.setSkills(skills);
        }

        userRepository.saveTA(ta);

        // 更新 session 中的用户信息
        request.getSession().setAttribute("currentUser", ta);

        response.sendRedirect(request.getContextPath() + "/ta/profile");
    }
}
