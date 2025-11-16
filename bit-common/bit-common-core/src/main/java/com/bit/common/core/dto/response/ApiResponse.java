package com.bit.common.core.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 统一返回结果, 封装返回给前端的数据。
 * @param <T>
 * @Author: Camellia.xiaohua
 * @Date: 2023/8/14 14:45
 * @Version: 1.0
 * @projectName: Camellia
 * @packageName: camellia.utilities.common
 * @className: ApiResponse
 */

@Data
public class ApiResponse<T> {

    /**
     * 状态码
     */
    private int status;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回的数据
     */
    private T data;

    /**
     * 请求时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 无参构造，自动设置时间戳
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 只传入状态码，自动设置时间戳
     * @param status
     */
    public ApiResponse(int status) {
        this(status, null, null);
    }

    /**
     * 传入状态码和数据，自动设置时间戳
     * @param status
     * @param data
     */
    public ApiResponse(int status, T data) {
        this(status, data, null);
    }

    /**
     * 传入状态码、数据和消息，自动设置时间戳
     * @param status
     * @param data
     * @param message
     */
    public ApiResponse(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 判断响应是否成功
     * @param response
     * @return
     */
    public static boolean isSuccess(ApiResponse<?> response) {
        return response != null && (response.status == ApiStatus.SUCCESS);
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 判断响应是否失败
     * @param response
     * @return
     */
    public static boolean isFail(ApiResponse<?> response) {
        return !isSuccess(response);
    }

    /**
     * 无参成功响应（用于如 login 等无返回数据的情况）
     * @Author: Eleven52AC
     * @Description:
     * @return
     * @param <T>
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ApiStatus.SUCCESS, null, "成功");
    }

    /**
     * 成功返回的工具方法，返回数据和默认消息。
     *
     * @param data 响应的数据
     * @param <T>  数据类型
     * @return 包含数据、状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiStatus.SUCCESS, data, "成功");
    }

    /**
     * 自定义消息成功返回的工具方法。
     *
     * @param data 响应的数据
     * @param message 自定义的成功消息
     * @param <T> 数据类型
     * @return 包含数据、状态码和自定义消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ApiStatus.SUCCESS, data, message);
    }

    /**
     * 无内容响应，返回默认状态码 204。
     *
     * @param <T> 数据类型
     * @return 包含无内容、状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(ApiStatus.NO_CONTENT, null, "无内容");
    }

    /**
     * 处理分页结果的成功响应，返回分页数据及总记录数。
     *
     * @param data  返回的数据列表
     * @param total 数据总数，用于分页
     * @param <T> 数据类型
     * @return 包含数据、总数、状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<List<T>> successList(List<T> data, long total) {
        String message = "请求成功，数据量: " + total;
        return new ApiResponse<>(ApiStatus.SUCCESS, data, message);
    }

    /**
     * 错误返回的工具方法，带有状态码和消息。
     *
     * @param status 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和错误消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, null, message);
    }

    /**
     * 错误返回的工具方法，默认状态码 500 和错误消息。
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和错误消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ApiStatus.INTERNAL_SERVER_ERROR, null, message);
    }

    /**
     * 错误返回的工具方法，带有状态码、数据、消息。
     * @param data 返回的数据
     * @param message 错误消息
     * @return 包含状态码和消息的 ApiResponse 对象
     * @param <T> 数据类型
     */
    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(ApiStatus.INTERNAL_SERVER_ERROR, data, message);
    }

    /**
     * 未授权响应（状态码 401）。
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(ApiStatus.UNAUTHORIZED, null, message);
    }

    /**
     * 禁止访问响应（状态码 403）。
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(ApiStatus.FORBIDDEN, null, message);
    }

    /**
     * 客户端请求无效响应（状态码 400）。
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(ApiStatus.BAD_REQUEST, null, message);
    }

    /**
     * 资源未找到响应（状态码 404）。
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(ApiStatus.NOT_FOUND, null, message);
    }

    /**
     * 服务器内部错误响应（状态码 500）。
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 包含状态码和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> internalServerError(String message) {
        return new ApiResponse<>(ApiStatus.INTERNAL_SERVER_ERROR, null, message);
    }

    /**
     * 自定义状态码响应，允许传入自定义状态码、数据和消息。
     *
     * @param status 状态码
     * @param data 响应数据
     * @param message 响应消息
     * @param <T> 数据类型
     * @return 包含自定义状态码、数据和消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> customResponse(int status, T data, String message) {
        return new ApiResponse<>(status, data, message);
    }

    /**
     * 请求成功的默认响应（状态码 200），当没有返回数据时使用。
     *
     * @param <T> 数据类型
     * @return 包含成功状态码和默认消息的 ApiResponse 对象
     */
    public static <T> ApiResponse<T> successWithoutData() {
        return new ApiResponse<>(ApiStatus.SUCCESS, null, "请求成功，无返回数据");
    }
}
