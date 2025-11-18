package com.bit.common.web.utils.http;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // X-Forwarded-For 可能包含多个 IP，取第一个有效的
            return ip.split(",")[0];
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) return ip;
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) return ip;
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) return ip;
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) return ip;
        return request.getRemoteAddr();
    }

    private static boolean isValidIp(String ip) {
        return ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip);
    }
}
