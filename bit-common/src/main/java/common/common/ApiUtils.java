package common.common;

import java.util.List;

/**
 * @Description: 提供了一系列通用的工具方法，用于构造 API 响应。这些工具方法帮助统一和简化了 HTTP 状态码的处理，并能灵活地返回带有不同状态码、消息和数据的响应。
 * @Author: Camellia.xiaohua
 * @Date: 2023/8/18 17:05
 * @Version: 1.0
 * @ProjectName: Camellia
 * @PackageName: camellia.utilities.common
 * @className: ApiUtils
 */
public class ApiUtils {

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
