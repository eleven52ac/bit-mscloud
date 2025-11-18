package com.bit.common.core.exception.common;

import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日22:01
 * @Author: Eleven52AC
 * @Description: 外部服务异常 ExternalServiceException
 */
public class ExternalServiceException extends BaseException {

    public ExternalServiceException(String msg) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR, msg);
    }

}
