package com.ebu6304.recruitment.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON文件读写工具类
 * 负责将Java对象序列化为JSON文件，以及从JSON文件反序列化为Java对象。
 * 所有数据持久化均通过此工具类完成，无需数据库。
 *
 * <p>使用Google Gson库进行JSON处理，支持LocalDateTime等Java 8时间类型。</p>
 *
 * @author Group39
 * @version 1.0
 */
public class JsonFileUtil {

    /** Gson实例，配置了美化输出和null值序列化 */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    /**
     * 将对象列表写入JSON文件。
     * 如果文件不存在，则自动创建；如果存在，则覆盖。
     *
     * @param <T>      列表元素类型
     * @param filePath JSON文件路径
     * @param data     要写入的对象列表
     * @throws RuntimeException 如果写入失败
     */
    public static <T> void writeList(String filePath, List<T> data) {
        try {
            // 确保父目录存在
            Path path = Paths.get(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            // 将列表序列化为JSON并写入文件
            String json = GSON.toJson(data);
            Files.writeString(path, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("写入JSON文件失败: " + filePath, e);
        }
    }

    /**
     * 从JSON文件读取对象列表。
     * 如果文件不存在，返回空列表。
     *
     * @param <T>      列表元素类型
     * @param filePath JSON文件路径
     * @param type     列表元素的Class类型
     * @return 反序列化后的对象列表，文件不存在时返回空列表
     */
    public static <T> List<T> readList(String filePath, Class<T> type) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            if (json == null || json.trim().isEmpty()) {
                return new ArrayList<>();
            }
            // 构造泛型List类型用于Gson反序列化
            Type listType = TypeToken.getParameterized(List.class, type).getType();
            List<T> result = GSON.fromJson(json, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("读取JSON文件失败: " + filePath, e);
        }
    }

    /**
     * 将单个对象写入JSON文件。
     *
     * @param <T>      对象类型
     * @param filePath JSON文件路径
     * @param data     要写入的对象
     */
    public static <T> void writeObject(String filePath, T data) {
        try {
            Path path = Paths.get(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            String json = GSON.toJson(data);
            Files.writeString(path, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("写入JSON文件失败: " + filePath, e);
        }
    }

    /**
     * 从JSON文件读取单个对象。
     *
     * @param <T>      对象类型
     * @param filePath JSON文件路径
     * @param type     对象的Class类型
     * @return 反序列化后的对象，文件不存在时返回null
     */
    public static <T> T readObject(String filePath, Class<T> type) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return null;
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return GSON.fromJson(json, type);
        } catch (IOException e) {
            throw new RuntimeException("读取JSON文件失败: " + filePath, e);
        }
    }

    /**
     * 检查文件是否存在。
     *
     * @param filePath 文件路径
     * @return 文件存在返回true，否则返回false
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 获取Gson实例（供外部使用）。
     *
     * @return Gson实例
     */
    public static Gson getGson() {
        return GSON;
    }
}
