package config;

import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class HttpClientSslConfig {

    @Value("${spring.cloud.gateway.httpclient.ssl.key-store}")
    private String keyStorePath;

    @Value("${spring.cloud.gateway.httpclient.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${spring.cloud.gateway.httpclient.ssl.trust-store}")
    private String trustStorePath;

    @Value("${spring.cloud.gateway.httpclient.ssl.trust-store-password}")
    private String trustStorePassword;

    @Bean
    @Primary
    public HttpClient sslHttpClient() throws Exception {
        System.out.println("==== [Gateway SSL] Initializing SSL HttpClient... ====");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream keyStream = getClass().getResourceAsStream(keyStorePath.replace("classpath:", "/"))) {
            keyStore.load(keyStream, keyStorePassword.toCharArray());
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (InputStream trustStream = getClass().getResourceAsStream(trustStorePath.replace("classpath:", "/"))) {
            trustStore.load(trustStream, trustStorePassword.toCharArray());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        var sslContext = SslContextBuilder.forClient()
                .keyManager(kmf)
                .trustManager(tmf)
                .build();

        System.out.println("✅ Loaded key store: " + keyStorePath);
        System.out.println("✅ Loaded trust store: " + trustStorePath);

        return HttpClient.create().secure(ssl -> ssl.sslContext(sslContext));
    }
}
