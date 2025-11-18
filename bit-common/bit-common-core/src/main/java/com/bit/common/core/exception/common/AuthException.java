package com.bit.common.core.exception.common;

import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日21:48
 * @Author: Eleven52AC
 * @Description: 认证异常
 */
public class AuthException extends BaseException {

    public AuthException(String msg) {
        super(ErrorCode.UNAUTHORIZED, msg);
    }

}
