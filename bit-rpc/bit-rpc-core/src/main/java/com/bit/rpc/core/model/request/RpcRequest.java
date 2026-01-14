package com.bit.rpc.core.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Datetime: 2026年01月13日17:55
 * @Author: Eleven52AC
 * @Description: RPC请求
 */
@Data
public class RpcRequest implements Serializable {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 参数列表
     */
    private Object[] args;

    public static class Builder {

        final private RpcRequest rpcRequest = new RpcRequest();

        public Builder serviceName(String serviceName) {
            rpcRequest.serviceName = serviceName;
            return this;
        }

        public Builder methodName(String methodName) {
            rpcRequest.methodName = methodName;
            return this;
        }

        public Builder paramTypes(Class<?>[] paramTypes) {
            rpcRequest.paramTypes = paramTypes;
            return this;
        }

        public Builder args(Object[] args) {
            rpcRequest.args = args;
            return this;
        }

        public RpcRequest build() {
            return rpcRequest;
        }
    }
}
