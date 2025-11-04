package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;

@Configuration
public class GatewayHttpClientConfig {

    @Autowired
    private HttpClient sslHttpClient;

    @Bean
    public HttpClientCustomizer gatewayHttpClientCustomizer() {
        return (client) -> {
            System.out.println("==== [Gateway SSL] Customizing Gateway HttpClient with SSL ====");
            return sslHttpClient;
        };
    }
}
