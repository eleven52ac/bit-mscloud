package com.bit.common.utils.log;

/**
 * 访问日志格式化工具类，用于生成结构化的设备与请求/响应日志。
 * @author： Eleven52AC
 * <p>此类为工具类，不可实例化。</p>
 */
public final class AccessLogUtils {

    private AccessLogUtils() {
        throw new UnsupportedOperationException("Utility class 'AccessLogUtils' cannot be instantiated");
    }

    /**
     * 格式化设备信息为可读的日志字符串。
     *
     * @param ip      客户端 IP 地址，可为 null
     * @param device  设备型号，可为 null
     * @param os      操作系统，可为 null
     * @param region  地理区域，可为 null
     * @param network 网络类型（如 4G、Wi-Fi），可为 null
     * @return 格式化后的设备信息字符串
     */
    public static String formatDeviceInfo(String ip, String device, String os, String region, String network) {
        return String.format("""
                📱 设备信息
                IP        : %s
                设备      : %s
                系统      : %s
                地区      : %s
                网络      : %s
                """,
                safeToString(ip),
                safeToString(device),
                safeToString(os),
                safeToString(region),
                safeToString(network));
    }

    /**
     * 格式化完整的请求-响应日志。
     *
     * @param method   HTTP 方法（如 GET、POST）
     * @param uri      请求 URI
     * @param query    查询参数（Query String）
     * @param body     请求体（可能被截断）
     * @param routeId  网关路由 ID（如使用 Spring Cloud Gateway）
     * @param target   目标微服务
     * @param status   HTTP 响应状态码
     * @param response 响应体（可能被截断）
     * @param cost     请求处理耗时（毫秒）
     * @return 格式化后的请求日志字符串
     */
    public static String formatRequestLog(
            String method, String uri, String query, String body,
            String routeId, String target, String status,
            String response, long cost) {

        String safeBody = truncateIfTooLong(body, 1000);
        String safeResponse = truncateIfTooLong(response, 1000);

        return String.format("""
                🌐 请求信息
                方法      : %s
                URI       : %s
                Query参数 : %s
                请求体    : %s
                路由ID    : %s
                目标服务  : %s
                📦 响应信息
                状态码    : %s
                响应体    : %s
                耗时      : %d ms
                """,
                safeToString(method),
                safeToString(uri),
                safeToString(query),
                safeBody,
                safeToString(routeId),
                safeToString(target),
                safeToString(status),
                safeResponse,
                cost);
    }

    private static String safeToString(String str) {
        return str != null ? str : "null";
    }

    private static String truncateIfTooLong(String str, int maxLength) {
        if (str == null) {
            return "null";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + " ...[省略]";
    }
}