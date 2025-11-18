package com.bit.common.core.exception.common;

import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日21:46
 * @Author: Eleven52AC
 * @Description: 参数异常
 */
public class ParamException extends BaseException {

    public ParamException(String msg) {
        super(ErrorCode.PARAM_ERROR, msg);
    }

}
