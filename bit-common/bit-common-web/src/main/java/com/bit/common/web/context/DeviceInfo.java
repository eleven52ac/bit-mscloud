package com.bit.common.web.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DeviceInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3803686767049843134L;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 原始UA
     */
    private String userAgent;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     *  设备
     */
    private String device;

    /**
     * 地理信息
     */
    private String region;

    /**
     * 网络类型 + ISP + 移动/宽带
     */
    private String networkInfo;
}
