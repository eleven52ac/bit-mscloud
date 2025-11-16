package com.bit.common.core.dto.response;

/**
 * @Description: 提供常用的 HTTP 状态码常量。这些状态码涵盖了请求成功、客户端错误、服务器错误以及自定义状态码，便于统一管理和使用。
 * @Author: Camellia.xiaohua
 * @Date: 2023/8/14 14:06
 * @Version: 1.0
 * @projectName: Camellia
 * @packageName: camellia.utilities.common
 * @className: ApiStatus
 */
public class ApiStatus {

    // 成功状态码
    /**
     * 状态码：200 - 请求成功
     * 用于表示请求已经成功处理并且返回了相应的结果。
     */
    public static final int SUCCESS = 200; // 请求成功

    /**
     * 状态码：201 - 资源创建成功
     * 用于表示资源已经成功创建，例如用户创建、数据创建等。
     */
    public static final int CREATED = 201; // 资源创建成功

    /**
     * 状态码：204 - 无内容
     * 用于表示请求已成功处理，但没有返回任何数据。常用于删除操作或更新操作。
     */
    public static final int NO_CONTENT = 204; // 无内容

    // 客户端错误状态码
    /**
     * 状态码：400 - 请求无效
     * 用于表示请求的格式错误或请求参数无效。
     */
    public static final int BAD_REQUEST = 400; // 请求无效

    /**
     * 状态码：401 - 未授权
     * 用于表示请求需要身份验证，用户未提供有效的认证信息。
     */
    public static final int UNAUTHORIZED = 401; // 未授权

    /**
     * 状态码：403 - 禁止访问
     * 用于表示服务器理解请求，但拒绝执行请求。例如，权限不足的情况。
     */
    public static final int FORBIDDEN = 403; // 禁止访问

    /**
     * 状态码：404 - 资源未找到
     * 用于表示请求的资源不存在或无法找到。
     */
    public static final int NOT_FOUND = 404; // 资源未找到

    // 服务器错误状态码
    /**
     * 状态码：500 - 服务器内部错误
     * 用于表示服务器发生了未预料的错误，导致无法完成请求。
     */
    public static final int INTERNAL_SERVER_ERROR = 500; // 服务器内部错误

    /**
     * 状态码：503 - 服务不可用
     * 用于表示服务器当前无法处理请求，通常是由于过载或维护原因。
     */
    public static final int SERVICE_UNAVAILABLE = 503; // 服务不可用

    // 自定义状态码
    /**
     * 状态码：422 - 验证失败
     * 用于表示请求中的数据验证失败，通常发生在表单提交或数据验证中。
     */
    public static final int VALIDATION_FAILED = 422; // 验证失败

    /**
     * 状态码：409 - 冲突
     * 用于表示请求导致资源冲突，常见于重复数据或并发写入的场景。
     */
    public static final int CONFLICT = 409; // 冲突，例如重复数据

    // 其他常见状态码
    /**
     * 状态码：202 - 请求已接受，但尚未处理
     * 用于表示请求已被接受，但处理尚未完成。常用于异步处理的场景。
     */
    public static final int ACCEPTED = 202; // 请求已接受，但尚未处理

    /**
     * 状态码：402 - 需要支付
     * 用于表示请求需要支付相关费用才能继续。
     */
    public static final int PAYMENT_REQUIRED = 402; // 需要支付
}
