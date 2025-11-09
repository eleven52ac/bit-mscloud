package common.utils.http;

import lombok.Data;

@Data
public class DeviceInfo {
    private String ip;             // IP地址
    private String userAgent;      // 原始UA
    private String os;             // 操作系统
    private String browser;        // 浏览器
    private String device;         // 品牌/型号
    private String region;         // 地理信息
    private String networkInfo;    // 网络类型 + ISP + 移动/宽带
}
