package com.bit.common.core.exception.base;

import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日21:36
 * @Author: Eleven52AC
 * @Description: 自定义业务异常
 */
public class BizException extends BaseException {

    public BizException(String msg) {
        super(ErrorCode.BIZ_ERROR, msg);
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode);
    }

}
