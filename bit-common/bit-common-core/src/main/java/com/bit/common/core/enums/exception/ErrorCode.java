package com.bit.common.core.enums.exception;

/**
 * @Datetime: 2025年11月17日21:33
 * @Author: Eleven52AC
 * @Description: 错误码枚举类
 */
public enum ErrorCode implements IErrorCode {

    SUCCESS(0, "success"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或 token 失效"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "接口不存在"),
    SERVER_ERROR(500, "系统异常"),
    BIZ_ERROR(400, "业务异常"),
    RATE_LIMITED(429, "请求过于频繁"),
    BAD_REQUEST(400, "请求参数错误"),
    SYSTEM_ERROR(500, "系统异常"),
    EXTERNAL_SERVICE_ERROR(500, "外部服务异常"),
    REMOTE_ERROR(500, "远程服务异常");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
