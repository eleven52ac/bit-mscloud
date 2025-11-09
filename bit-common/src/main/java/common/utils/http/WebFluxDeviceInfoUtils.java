package common.utils.http;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * WebFlux 版本的设备信息工具类
 * 可直接用于 Spring Cloud Gateway 全局过滤器中
 */
public class WebFluxDeviceInfoUtils {

    private static final Parser UA_PARSER = new Parser();
    private static Searcher SEARCHER;

    static {
        try {
            // ip2region.xdb 文件必须放在 resources 目录下
            URL resource = WebFluxDeviceInfoUtils.class.getClassLoader().getResource("ip2region.xdb");
            if (resource == null) {
                throw new IllegalStateException("ip2region.xdb not found in resources");
            }
            String dbPath = new File(resource.toURI()).getPath();
            SEARCHER = Searcher.newWithFileOnly(dbPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从 WebFlux Request 获取设备信息（异步）
     */
    public static Mono<DeviceInfo> getDeviceInfo(ServerHttpRequest request) {
        DeviceInfo info = new DeviceInfo();

        String ip = extractClientIp(request);
        String ua = request.getHeaders().getFirst("User-Agent");

        info.setIp(ip);
        info.setUserAgent(ua);

        // UA 解析
        try {
            Client client = UA_PARSER.parse(ua);
            info.setOs(client.os.family);
            info.setBrowser(client.userAgent.family);
            info.setDevice(client.device.family);
        } catch (Exception e) {
            info.setDevice("Unknown");
        }

        // 离线地理信息
        try {
            String region = SEARCHER.search(ip);
            info.setRegion(region == null ? "unknown" : region);
        } catch (Exception e) {
            info.setRegion("unknown");
        }

        // 联网增强信息（非阻塞异步版本）
        return Mono.fromCallable(() -> {
            try {
                String apiUrl = "http://ip-api.com/json/" + URLEncoder.encode(ip, StandardCharsets.UTF_8)
                        + "?fields=status,country,regionName,city,isp,org,mobile,proxy";
                String json = new String(new URL(apiUrl).openStream().readAllBytes(), StandardCharsets.UTF_8);
                info.setNetworkInfo(json);
            } catch (Exception e) {
                info.setNetworkInfo("N/A");
            }
            return info;
        });
    }

    /**
     * 提取客户端 IP（支持代理头）
     */
    private static String extractClientIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0];
        }
        InetSocketAddress address = request.getRemoteAddress();
        return address != null ? address.getAddress().getHostAddress() : "unknown";
    }
}
