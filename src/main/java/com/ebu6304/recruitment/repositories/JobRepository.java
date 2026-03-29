package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.JobPosting;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 职位数据访问层（Repository）
 * 负责职位发布数据的持久化读写，数据存储在 data/jobs.json 文件中。
 *
 * @author Group39
 * @version 1.0
 */
public class JobRepository {

    /** 职位数据文件路径 */
    private static final String JOB_FILE = "data/jobs.json";

    /**
     * 保存职位（新增或更新）。
     * 若jobId已存在则更新，否则新增。
     *
     * @param job 要保存的职位对象
     */
    public void save(JobPosting job) {
        List<JobPosting> list = findAll();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getJobId().equals(job.getJobId())) {
                list.set(i, job);
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(job);
        }
        JsonFileUtil.writeList(JOB_FILE, list);
    }

    /**
     * 根据职位ID查找职位。
     *
     * @param jobId 职位ID
     * @return 包含职位的Optional，未找到则为空
     */
    public Optional<JobPosting> findById(String jobId) {
        return findAll().stream()
                .filter(j -> j.getJobId().equals(jobId))
                .findFirst();
    }

    /**
     * 获取所有职位列表。
     *
     * @return 职位列表
     */
    public List<JobPosting> findAll() {
        return JsonFileUtil.readList(JOB_FILE, JobPosting.class);
    }

    /**
     * 获取指定MO发布的所有职位。
     *
     * @param moId MO的用户ID
     * @return 该MO发布的职位列表
     */
    public List<JobPosting> findByMoId(String moId) {
        return findAll().stream()
                .filter(j -> moId.equals(j.getMoId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有开放状态（OPEN）的职位。
     *
     * @return 开放职位列表
     */
    public List<JobPosting> findOpenJobs() {
        return findAll().stream()
                .filter(JobPosting::isOpen)
                .collect(Collectors.toList());
    }

    /**
     * 根据模块代码查找职位。
     *
     * @param moduleCode 模块代码
     * @return 该模块的职位列表
     */
    public List<JobPosting> findByModuleCode(String moduleCode) {
        return findAll().stream()
                .filter(j -> moduleCode.equals(j.getModuleCode()))
                .collect(Collectors.toList());
    }

    /**
     * 根据状态查找职位。
     *
     * @param status 职位状态（OPEN/REVIEWING/CLOSED/CANCELLED）
     * @return 符合状态的职位列表
     */
    public List<JobPosting> findByStatus(String status) {
        return findAll().stream()
                .filter(j -> status.equals(j.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 根据学期查找职位。
     *
     * @param semester 学期（如 "2026 Spring"）
     * @return 该学期的职位列表
     */
    public List<JobPosting> findBySemester(String semester) {
        return findAll().stream()
                .filter(j -> semester.equals(j.getSemester()))
                .collect(Collectors.toList());
    }

    /**
     * 删除职位（按职位ID）。
     *
     * @param jobId 职位ID
     * @return 删除成功返回true，未找到返回false
     */
    public boolean delete(String jobId) {
        List<JobPosting> list = findAll();
        boolean removed = list.removeIf(j -> j.getJobId().equals(jobId));
        if (removed) {
            JsonFileUtil.writeList(JOB_FILE, list);
        }
        return removed;
    }

    /**
     * 统计指定MO发布的职位数量。
     *
     * @param moId MO的用户ID
     * @return 职位数量
     */
    public long countByMoId(String moId) {
        return findAll().stream()
                .filter(j -> moId.equals(j.getMoId()))
                .count();
    }
}
