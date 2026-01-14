package com.bit.rpc.core.config;

import lombok.Data;

/**
 * @Datetime: 2026年01月14日14:43
 * @Author: Eleven52AC
 * @Description: RPC配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name;

    /**
     * 版本号
     */
    private String version;

    /**
     * 服务器主机名
     */
    private String serverHost;

    /**
     * 服务器端口号
     */
    private int serverPort;
}
