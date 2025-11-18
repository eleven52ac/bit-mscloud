package com.bit.common.core.exception.common;

import com.bit.common.core.exception.base.BaseException;
import com.bit.common.core.enums.exception.ErrorCode;

/**
 * @Datetime: 2025年11月17日21:50
 * @Author: Eleven52AC
 * @Description: 微服务调用异常 RemoteException
 */
public class RemoteException extends BaseException {

    public RemoteException(String msg) {
        super(ErrorCode.REMOTE_ERROR, msg);
    }

}
