package com.ebu6304.recruitment.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成工具类
 * 为系统中各类实体生成唯一标识符。
 * 格式：{前缀}{日期时间}{序号}，例如 JOB20260329001
 *
 * <p>使用原子计数器保证同一JVM进程内的唯一性。</p>
 *
 * @author Group39
 * @version 1.0
 */
public class IdGenerator {

    /** 日期时间格式，用于ID生成 */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 原子计数器，保证并发安全 */
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    /** 各实体ID前缀 */
    public static final String PREFIX_JOB = "JOB";
    public static final String PREFIX_APP = "APP";
    public static final String PREFIX_USER = "USR";
    public static final String PREFIX_MO = "MO";
    public static final String PREFIX_TA = "TA";
    public static final String PREFIX_ADMIN = "ADM";
    public static final String PREFIX_WORKLOAD = "WL";
    public static final String PREFIX_TEMPLATE = "TPL";

    /**
     * 生成带指定前缀的唯一ID。
     * 格式：{前缀}{yyyyMMddHHmmss}{3位序号}
     *
     * @param prefix ID前缀（如 "JOB", "APP"）
     * @return 唯一ID字符串
     */
    public static String generate(String prefix) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int seq = COUNTER.incrementAndGet() % 1000;
        return String.format("%s%s%03d", prefix, timestamp, seq);
    }

    /**
     * 生成职位ID。
     *
     * @return 职位唯一ID，格式：JOB{timestamp}{seq}
     */
    public static String generateJobId() {
        return generate(PREFIX_JOB);
    }

    /**
     * 生成申请ID。
     *
     * @return 申请唯一ID，格式：APP{timestamp}{seq}
     */
    public static String generateApplicationId() {
        return generate(PREFIX_APP);
    }

    /**
     * 生成MO用户ID。
     *
     * @return MO唯一ID，格式：MO{timestamp}{seq}
     */
    public static String generateMOId() {
        return generate(PREFIX_MO);
    }

    /**
     * 生成TA用户ID。
     *
     * @return TA唯一ID，格式：TA{timestamp}{seq}
     */
    public static String generateTAId() {
        return generate(PREFIX_TA);
    }

    /**
     * 生成管理员用户ID。
     *
     * @return 管理员唯一ID，格式：ADM{timestamp}{seq}
     */
    public static String generateAdminId() {
        return generate(PREFIX_ADMIN);
    }

    /**
     * 生成工作量记录ID。
     *
     * @return 工作量记录唯一ID，格式：WL{timestamp}{seq}
     */
    public static String generateWorkloadId() {
        return generate(PREFIX_WORKLOAD);
    }

    /**
     * 生成模板ID。
     *
     * @return 模板唯一ID，格式：TPL{timestamp}{seq}
     */
    public static String generateTemplateId() {
        return generate(PREFIX_TEMPLATE);
    }
}
