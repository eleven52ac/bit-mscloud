package msgateway.filter;

import com.bit.common.utils.log.AccessLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("ACCESS_LOG");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        String method = Optional.ofNullable(request.getMethod()).map(HttpMethod::name).orElse("UNKNOWN");
        String uri = request.getURI().toString();
        String query = request.getQueryParams().toString();

        return DataBufferUtils.join(request.getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    String requestBody = new String(bytes, StandardCharsets.UTF_8);

                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                        DataBuffer copy = exchange.getResponse().bufferFactory().wrap(bytes);
                        return Mono.just(copy);
                    });

                    ServerHttpRequest mutated = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };

                    ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(exchange.getResponse()) {
                        @Override
                        public Mono<Void> writeWith(Publisher<? extends DataBuffer> bodyPublisher) {
                            if (bodyPublisher instanceof Flux) {
                                Flux<? extends DataBuffer> flux = Flux.from(bodyPublisher);
                                return super.writeWith(flux.map(buffer -> {
                                    byte[] content = new byte[buffer.readableByteCount()];
                                    buffer.read(content);
                                    DataBufferUtils.release(buffer);

                                    String responseBody = new String(content, StandardCharsets.UTF_8);
                                    long cost = System.currentTimeMillis() - start;
                                    HttpStatusCode status = getStatusCode();
                                    Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                                    URI targetUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);

                                    ACCESS_LOG.info(AccessLogUtils.formatRequestLog(
                                            method, uri, query, requestBody,
                                            route != null ? route.getId() : "unknown",
                                            targetUri != null ? targetUri.toString() : "unknown",
                                            String.valueOf(status), responseBody, cost));

                                    return exchange.getResponse().bufferFactory().wrap(content);
                                }));
                            }
                            return super.writeWith(bodyPublisher);
                        }
                    };

                    return chain.filter(exchange.mutate()
                            .request(mutated)
                            .response(response)
                            .build());
                });
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
