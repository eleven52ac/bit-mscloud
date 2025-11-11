//package com.bit.user.api.config;
//
//import feign.Client;
//import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
//import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
//import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.*;
//import java.security.cert.X509Certificate;
//
///**
// * Feign HTTPS + Nacos 负载均衡支持
// * 适配 Spring Cloud 2023.x 及以上版本
// */
//@Configuration
//public class FeignSslConfig {
//
//    @Bean
//    public Client feignClient(LoadBalancerClient loadBalancerClient,
//                              LoadBalancerClientFactory loadBalancerClientFactory) throws Exception {
//
//        // ✅ 信任所有证书
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, new TrustManager[]{
//                new X509TrustManager() {
//                    @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
//                    @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
//                    @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
//                }
//        }, new java.security.SecureRandom());
//
//        Client.Default baseClient = new Client.Default(
//                sslContext.getSocketFactory(),
//                (hostname, session) -> true
//        );
//
//        // ✅ 正确构造方式（新版 Spring Cloud 要传两个 Bean）
//        return new FeignBlockingLoadBalancerClient(baseClient, loadBalancerClient, loadBalancerClientFactory);
//    }
//}
