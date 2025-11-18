package com.bit.common.web.context;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class ClientMetaInfo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 客户端IP
     */
    private String ip;

    /**
     * 操作系统
     */
    private String os;

    /**
     *  设备
     */
    private String device;

    /**
     * 地域
     */
    private String region;

    /**
     *  网络
     */
    private String network;

    /**
     * 内部令牌
     */
    private String internalToken;

    public static ClientMetaInfo from(HttpServletRequest req) {
        ClientMetaInfo info = new ClientMetaInfo();
        info.setIp(req.getHeader("X-Client-IP"));
        info.setOs(req.getHeader("X-Client-OS"));
        info.setDevice(req.getHeader("X-Client-Device"));
        String regionEncoded = req.getHeader("X-Client-Region");
        String region = new String(Base64.getDecoder().decode(regionEncoded), StandardCharsets.UTF_8);
        info.setRegion(region);
        info.setNetwork(req.getHeader("X-Client-Network"));
        info.setInternalToken(req.getHeader("X-Internal-Token"));
        return info;
    }
}
