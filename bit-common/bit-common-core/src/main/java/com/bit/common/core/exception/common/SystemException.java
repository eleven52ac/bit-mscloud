package com.bit.common.core.exception.common;

import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日21:51
 * @Author: Eleven52AC
 * @Description: 系统异常 SystemException
 */
public class SystemException extends BaseException {

    public SystemException(String msg) {
        super(ErrorCode.SYSTEM_ERROR, msg);
    }

}
