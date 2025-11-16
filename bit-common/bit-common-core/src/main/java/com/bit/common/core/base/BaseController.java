package com.bit.common.core.base;

import com.bit.common.core.context.ClientMetaInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 所有 Controller 的父类，统一提供请求元数据能力
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseController {

    @Autowired
    protected HttpServletRequest request;

    /**
     * 获取当前请求的客户端设备信息（封装了 IP、系统、设备、地区、网络）
     */
    protected ClientMetaInfo getClientInfo() {
        return ClientMetaInfo.from(request);
    }

    /**
     * 获取当前请求 IP（便捷方法）
     */
    protected String getClientIp() {
        return getClientInfo().getIp();
    }
}
