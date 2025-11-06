package common.common;

import lombok.Data;

import java.time.LocalDateTime;

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
}
