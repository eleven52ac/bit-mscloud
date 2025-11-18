package com.bit.common.core.exception.base;

import com.bit.common.core.enums.exception.IErrorCode;

/**
 * @Datetime: 2025年11月17日21:35
 * @Author: Eleven52AC
 * @Description:  基础异常类
 */
public class BaseException extends RuntimeException {

    private final int code;

    public BaseException(IErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public BaseException(IErrorCode errorCode, String customMsg) {
        super(customMsg);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return getMessage();
    }
}
