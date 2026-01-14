package com.bit.rpc.core;

import com.bit.rpc.core.config.RpcConfig;
import com.bit.rpc.core.constant.RpcConstant;
import com.bit.rpc.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * @Datetime: 2026年01月14日14:53
 * @Author: Eleven52AC
 * @Description: RPC 框架应用,相当于 holder，存放了项目全局用到的变量。双检锁单例模式实现.
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;


    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    private static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
    }

    /**
     * 初始化
     */
    private static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_FILE_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
