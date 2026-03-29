package com.ebu6304.recruitment.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 * 提供密码哈希和验证功能，使用SHA-256算法加盐哈希。
 * 不依赖任何外部框架，适合独立Java应用。
 *
 * <p>安全策略：每个密码使用随机盐值，防止彩虹表攻击。
 * 存储格式：{base64(salt)}:{base64(hash)}</p>
 *
 * @author Group39
 * @version 1.0
 */
public class PasswordUtil {

    /** 盐值长度（字节） */
    private static final int SALT_LENGTH = 16;

    /** 哈希算法 */
    private static final String ALGORITHM = "SHA-256";

    /** 存储格式分隔符 */
    private static final String SEPARATOR = ":";

    /**
     * 对明文密码进行哈希处理。
     * 自动生成随机盐值，返回格式为 {base64(salt)}:{base64(hash)}。
     *
     * @param plainPassword 明文密码
     * @return 哈希后的密码字符串（含盐值）
     * @throws RuntimeException 如果哈希算法不可用
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        // 生成随机盐值
        byte[] salt = generateSalt();
        // 计算哈希
        byte[] hash = computeHash(plainPassword, salt);
        // 编码为Base64并拼接
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        return saltBase64 + SEPARATOR + hashBase64;
    }

    /**
     * 验证明文密码与存储的哈希是否匹配。
     *
     * @param plainPassword  明文密码
     * @param storedPassword 存储的哈希密码（格式：{base64(salt)}:{base64(hash)}）
     * @return 密码匹配返回true，否则返回false
     */
    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        // 解析存储的密码
        String[] parts = storedPassword.split(SEPARATOR);
        if (parts.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            // 用相同盐值计算输入密码的哈希
            byte[] actualHash = computeHash(plainPassword, salt);
            // 常量时间比较，防止时序攻击
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 生成随机盐值。
     *
     * @return 随机盐值字节数组
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 使用SHA-256计算密码哈希。
     *
     * @param password 明文密码
     * @param salt     盐值
     * @return 哈希字节数组
     */
    private static byte[] computeHash(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }
}
