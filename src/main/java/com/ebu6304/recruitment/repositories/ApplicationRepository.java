package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.Application;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 申请数据访问层（Repository）
 * 负责申请数据的持久化读写，数据存储在 data/applications.json 文件中。
 *
 * @author Group39
 * @version 1.0
 */
public class ApplicationRepository {

    /** 申请数据文件路径 */
    private static final String APP_FILE = "data/applications.json";

    /**
     * 保存申请（新增或更新）。
     *
     * @param application 要保存的申请对象
     */
    public void save(Application application) {
        List<Application> list = findAll();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getApplicationId().equals(application.getApplicationId())) {
                list.set(i, application);
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(application);
        }
        JsonFileUtil.writeList(APP_FILE, list);
    }

    /**
     * 根据申请ID查找申请。
     *
     * @param applicationId 申请ID
     * @return 包含申请的Optional
     */
    public Optional<Application> findById(String applicationId) {
        return findAll().stream()
                .filter(a -> a.getApplicationId().equals(applicationId))
                .findFirst();
    }

    /**
     * 获取所有申请列表。
     *
     * @return 申请列表
     */
    public List<Application> findAll() {
        return JsonFileUtil.readList(APP_FILE, Application.class);
    }

    /**
     * 获取指定职位的所有申请。
     *
     * @param jobId 职位ID
     * @return 该职位的申请列表
     */
    public List<Application> findByJobId(String jobId) {
        return findAll().stream()
                .filter(a -> jobId.equals(a.getJobId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定TA提交的所有申请。
     *
     * @param taId TA用户ID
     * @return 该TA的申请列表
     */
    public List<Application> findByTaId(String taId) {
        return findAll().stream()
                .filter(a -> taId.equals(a.getTaId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定职位中待审核的申请。
     *
     * @param jobId 职位ID
     * @return 待审核申请列表
     */
    public List<Application> findPendingByJobId(String jobId) {
        return findAll().stream()
                .filter(a -> jobId.equals(a.getJobId())
                        && Application.STATUS_PENDING.equals(a.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定职位中已录用的申请。
     *
     * @param jobId 职位ID
     * @return 已录用申请列表
     */
    public List<Application> findAcceptedByJobId(String jobId) {
        return findAll().stream()
                .filter(a -> jobId.equals(a.getJobId())
                        && Application.STATUS_ACCEPTED.equals(a.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 检查TA是否已申请某职位（防止重复申请）。
     *
     * @param taId  TA用户ID
     * @param jobId 职位ID
     * @return 已申请返回true
     */
    public boolean existsByTaIdAndJobId(String taId, String jobId) {
        return findAll().stream()
                .anyMatch(a -> taId.equals(a.getTaId())
                        && jobId.equals(a.getJobId())
                        && !Application.STATUS_WITHDRAWN.equals(a.getStatus()));
    }

    /**
     * 统计指定职位的申请数量。
     *
     * @param jobId 职位ID
     * @return 申请数量
     */
    public long countByJobId(String jobId) {
        return findAll().stream()
                .filter(a -> jobId.equals(a.getJobId()))
                .count();
    }

    /**
     * 删除申请（按申请ID）。
     *
     * @param applicationId 申请ID
     * @return 删除成功返回true
     */
    public boolean delete(String applicationId) {
        List<Application> list = findAll();
        boolean removed = list.removeIf(a -> a.getApplicationId().equals(applicationId));
        if (removed) {
            JsonFileUtil.writeList(APP_FILE, list);
        }
        return removed;
    }
}
