package com.ebu6304.recruitment.web.servlet;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.repositories.JobRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminJobsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JobRepository jobRepository =
                (JobRepository) getServletContext().getAttribute("jobRepository");
        String status = trim(request.getParameter("status"));
        String module = trim(request.getParameter("module"));
        String semester = trim(request.getParameter("semester"));

        List<JobPosting> allJobs = jobRepository.findAll();
        List<JobPosting> jobs = allJobs.stream()
                .filter(j -> isBlank(status) || status.equalsIgnoreCase(j.getStatus()))
                .filter(j -> isBlank(module) || module.equalsIgnoreCase(j.getModuleCode()))
                .filter(j -> isBlank(semester) || semester.equalsIgnoreCase(j.getSemester()))
                .sorted(Comparator.comparing(JobPosting::getPostedAt,
                        Comparator.nullsLast(String::compareTo)).reversed())
                .collect(Collectors.toList());

        if ("csv".equalsIgnoreCase(request.getParameter("export"))) {
            exportCsv(response, jobs);
            return;
        }

        request.setAttribute("jobs", jobs);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedModule", module);
        request.setAttribute("selectedSemester", semester);
        request.setAttribute("moduleOptions", collectModules(allJobs));
        request.setAttribute("semesterOptions", collectSemesters(allJobs));
        request.getRequestDispatcher("/WEB-INF/jsp/admin/jobs.jsp").forward(request, response);
    }

    private void exportCsv(HttpServletResponse response, List<JobPosting> jobs) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=admin-job-postings.csv");
        StringBuilder csv = new StringBuilder();
        csv.append("Job ID,Title,Owner,Module,Semester,Deadline,Hours,Vacancies,Filled,Status\n");
        for (JobPosting job : jobs) {
            csv.append(csv(job.getJobId())).append(',')
                    .append(csv(job.getTitle())).append(',')
                    .append(csv(job.getMoName())).append(',')
                    .append(csv(job.getModuleCode())).append(',')
                    .append(csv(job.getSemester())).append(',')
                    .append(csv(job.getDeadline())).append(',')
                    .append(job.getHoursPerWeek()).append(',')
                    .append(job.getVacancies()).append(',')
                    .append(job.getFilledCount()).append(',')
                    .append(csv(job.getStatus())).append('\n');
        }
        response.getOutputStream().write(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Set<String> collectModules(List<JobPosting> jobs) {
        return jobs.stream().map(JobPosting::getModuleCode)
                .filter(v -> !isBlank(v)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> collectSemesters(List<JobPosting> jobs) {
        return jobs.stream().map(JobPosting::getSemester)
                .filter(v -> !isBlank(v)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String csv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ") + "\"";
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
