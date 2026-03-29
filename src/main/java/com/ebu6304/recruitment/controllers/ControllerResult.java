package com.ebu6304.recruitment.controllers;

/**
 * 控制器统一响应结果封装类（Controller Result）
 * 所有控制器方法均返回此类型，提供统一的成功/失败响应格式。
 *
 * <p>使用示例：
 * <pre>
 *   // 成功响应
 *   return ControllerResult.success("Job posted successfully", job);
 *
 *   // 失败响应
 *   return ControllerResult.failure("Job not found");
 * </pre>
 * </p>
 *
 * @param <T> 响应数据的类型
 * @author Group39
 * @version 1.0
 */
public class ControllerResult<T> {

    /** 操作是否成功 */
    private final boolean success;

    /** 响应消息（成功提示或错误原因） */
    private final String message;

    /** 响应数据（成功时携带，失败时为null） */
    private final T data;

    /**
     * 私有构造方法，通过静态工厂方法创建实例。
     *
     * @param success 是否成功
     * @param message 响应消息
     * @param data    响应数据
     */
    private ControllerResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应。
     *
     * @param message 成功消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功的ControllerResult
     */
    public static <T> ControllerResult<T> success(String message, T data) {
        return new ControllerResult<>(true, message, data);
    }

    /**
     * 创建失败响应。
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败的ControllerResult
     */
    public static <T> ControllerResult<T> failure(String message) {
        return new ControllerResult<>(false, message, null);
    }

    /**
     * 判断操作是否成功。
     *
     * @return 成功返回true
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取响应消息。
     *
     * @return 响应消息字符串
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取响应数据。
     *
     * @return 响应数据（失败时为null）
     */
    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ControllerResult{success=" + success +
               ", message='" + message + "'" +
               ", data=" + (data != null ? data.toString() : "null") + "}";
    }
}
