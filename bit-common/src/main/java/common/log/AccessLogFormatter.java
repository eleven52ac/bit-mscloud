package common.log;

public class AccessLogFormatter {

    public static String formatDeviceInfo(String ip, String device, String os, String region, String network) {
        return String.format("""
                📱 设备信息
                IP        : %s
                设备      : %s
                系统      : %s
                地区      : %s
                网络      : %s
                """, ip, device, os, region, network);
    }

    public static String formatRequestLog(
            String method, String uri, String query, String body,
            String routeId, String target, String status,
            String response, long cost) {

        if (response != null && response.length() > 1000) {
            response = response.substring(0, 1000) + " ...[省略]";
        }
        if (body != null && body.length() > 1000) {
            body = body.substring(0, 1000) + " ...[省略]";
        }

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
                method, uri, query, body, routeId, target, status, response, cost);
    }
}
