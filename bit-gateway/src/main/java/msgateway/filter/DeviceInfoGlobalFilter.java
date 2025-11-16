package msgateway.filter;

import com.bit.common.utils.http.WebFluxDeviceInfoUtils;
import com.bit.common.utils.http.context.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.bit.common.utils.log.AccessLogUtils;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Datetime: 2025年11月08日23:27
 * @Author: Eleven52AC
 * @Description: 设备信息过滤器
 */
@Component
public class DeviceInfoGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("ACCESS_LOG");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        return WebFluxDeviceInfoUtils.getDeviceInfo(request)
                .flatMap(info -> {
                    ServerHttpRequest newRequest = request.mutate()
                            .header("X-Client-IP", info.getIp())
                            .header("X-Client-OS", info.getOs())
                            .header("X-Client-Device", info.getDevice())
                            .header("X-Client-Region", Base64.getEncoder()
                                    .encodeToString(info.getRegion().getBytes(StandardCharsets.UTF_8)))
                            .header("X-Client-Network", info.getNetworkInfo())
                            .build();

                    ACCESS_LOG.info(AccessLogUtils.formatDeviceInfo(
                            info.getIp(), info.getDevice(), info.getOs(),
                            info.getRegion(), info.getNetworkInfo()));

                    return chain.filter(exchange.mutate().request(newRequest).build())
                            .contextWrite(ctx -> ctx.put(DeviceInfo.class, info));
                });
    }

    @Override
    public int getOrder() {
        return -100;
    }

}
