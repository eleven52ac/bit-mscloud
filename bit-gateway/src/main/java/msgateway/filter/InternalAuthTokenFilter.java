package msgateway.filter;

import common.constant.SaltConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Datetime: 2025年11月06日17:22
 * @Author: Eleven52AC
 * @Description: 用于网关转发时，后端微服务验证令牌。
 */
@Component
public class InternalAuthTokenFilter implements GlobalFilter, Ordered {

    // 令牌密钥
    private static final String SECRET = SaltConstants.INTERNAL_AUTH_TOKEN_SECRET;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long timestamp = System.currentTimeMillis();
        String payload = exchange.getRequest().getURI().getPath() + "|" + timestamp;
        String signature = hmacSha256(payload, SECRET);
        String token = Base64.getEncoder().encodeToString((payload + "|" + signature).getBytes(StandardCharsets.UTF_8));

        exchange = exchange.mutate()
                .request(r -> r.headers(h -> {
                    h.add("X-Internal-Token", token);
                    h.add("X-Caller-Service", "ms-gateway");
                }))
                .build();

        return chain.filter(exchange);
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }

    @Override
    public int getOrder() {
        return -100; // 在请求转发前执行
    }
}
