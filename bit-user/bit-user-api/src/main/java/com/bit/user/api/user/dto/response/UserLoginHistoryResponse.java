package com.bit.user.api.user.dto.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Datetime: 2025年11月20日14:14
 * @Author: Eleven52AC
 * @Description: 用户登录记录请求参数
 */
@Data
public class UserLoginHistoryResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 98337738233131143L;
    
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地区原始字符串
     */
    private String region;

    /**
     * 运营商
     */
    private String isp;

    /**
     * 设备信息
     */
    private String device;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 网络信息JSON
     */
    private String network;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登出时间
     */
    private LocalDateTime logoutTime;

    /**
     * 登录结果(0失败,1成功)
     */
    private Integer loginResult;

    /**
     * 是否异常(0正常,1异常)
     */
    private Integer isSuspicious;

    /**
     * 备注
     */
    private String remark;

}
