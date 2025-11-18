package com.bit.common.core.exception.common;

import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日21:49
 * @Author: Eleven52AC
 * @Description: 限流异常 RateLimitException
 */
public class RateLimitException extends BaseException {

    public RateLimitException(String msg) {
        super(ErrorCode.RATE_LIMITED, msg);
    }

}
