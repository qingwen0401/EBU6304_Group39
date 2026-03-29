package com.ebu6304.recruitment.repositories;

import com.ebu6304.recruitment.models.WorkloadRecord;
import com.ebu6304.recruitment.utils.JsonFileUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工作量记录数据访问层（Repository）
 * 负责工作量记录的持久化读写，数据存储在 data/workload_records.json 文件中。
 *
 * @author Group39
 * @version 1.0
 */
public class WorkloadRepository {

    /** 工作量记录数据文件路径 */
    private static final String WORKLOAD_FILE = "data/workload_records.json";

    /**
     * 保存工作量记录（新增或更新）。
     *
     * @param record 要保存的工作量记录
     */
    public void save(WorkloadRecord record) {
        List<WorkloadRecord> list = findAll();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRecordId().equals(record.getRecordId())) {
                list.set(i, record);
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(record);
        }
        JsonFileUtil.writeList(WORKLOAD_FILE, list);
    }

    /**
     * 根据记录ID查找工作量记录。
     *
     * @param recordId 记录ID
     * @return 包含记录的Optional
     */
    public Optional<WorkloadRecord> findById(String recordId) {
        return findAll().stream()
                .filter(r -> r.getRecordId().equals(recordId))
                .findFirst();
    }

    /**
     * 获取所有工作量记录。
     *
     * @return 工作量记录列表
     */
    public List<WorkloadRecord> findAll() {
        return JsonFileUtil.readList(WORKLOAD_FILE, WorkloadRecord.class);
    }

    /**
     * 获取指定TA的所有工作量记录。
     *
     * @param taId TA用户ID
     * @return 该TA的工作量记录列表
     */
    public List<WorkloadRecord> findByTaId(String taId) {
        return findAll().stream()
                .filter(r -> taId.equals(r.getTaId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定TA在指定学期的工作量记录。
     *
     * @param taId     TA用户ID
     * @param semester 学期
     * @return 工作量记录列表
     */
    public List<WorkloadRecord> findByTaIdAndSemester(String taId, String semester) {
        return findAll().stream()
                .filter(r -> taId.equals(r.getTaId()) && semester.equals(r.getSemester()))
                .collect(Collectors.toList());
    }

    /**
     * 计算指定TA在指定学期的总工作小时数。
     *
     * @param taId     TA用户ID
     * @param semester 学期
     * @return 总工作小时数
     */
    public int calculateTotalHours(String taId, String semester) {
        return findByTaIdAndSemester(taId, semester).stream()
                .filter(r -> "ACTIVE".equals(r.getStatus()))
                .mapToInt(WorkloadRecord::getWeeklyHours)
                .sum();
    }

    /**
     * 获取指定MO管理的所有工作量记录。
     *
     * @param moId MO用户ID
     * @return 该MO管理的工作量记录列表
     */
    public List<WorkloadRecord> findByMoId(String moId) {
        return findAll().stream()
                .filter(r -> moId.equals(r.getMoId()))
                .collect(Collectors.toList());
    }

    /**
     * 删除工作量记录（按记录ID）。
     *
     * @param recordId 记录ID
     * @return 删除成功返回true
     */
    public boolean delete(String recordId) {
        List<WorkloadRecord> list = findAll();
        boolean removed = list.removeIf(r -> r.getRecordId().equals(recordId));
        if (removed) {
            JsonFileUtil.writeList(WORKLOAD_FILE, list);
        }
        return removed;
    }
}
