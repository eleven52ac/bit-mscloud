package com.bit.rpc.core.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Datetime: 2026年01月13日17:59
 * @Author: Eleven52AC
 * @Description: RPC响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private Exception exception;

}
