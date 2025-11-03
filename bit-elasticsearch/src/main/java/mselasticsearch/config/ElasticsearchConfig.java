package mselasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch 配置类
 * 用于创建不同版本的 Elasticsearch 客户端。
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:localhost}")  // 从配置文件中获取主机地址
    private String elasticsearchHost;

    @Value("${elasticsearch.port:9200}")  // 从配置文件中获取端口
    private int elasticsearchPort;

    /**
     * 创建旧版 RestHighLevelClient（适用于 7.x 版本）
     * 该客户端被 Elasticsearch 8.x 弃用，未来需要迁移到新客户端。
     * @return 返回 RestHighLevelClient 实例
     */
    @Bean(name = "oldElasticsearchClient")
    public RestHighLevelClient oldElasticsearchClient() {
        try {
            return new RestHighLevelClient(
                    RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http"))
            );
        } catch (Exception e) {
            throw new RuntimeException("创建 RestHighLevelClient 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建新版同步 RestClient（适用于 8.x 版本）
     * 新版本的客户端将代替 RestHighLevelClient。
     * @return 返回 RestClient 实例
     */
    @Bean(name = "newElasticsearchClient")
    public RestClient newElasticsearchClient() {
        try {
            return RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http")).build();
        } catch (Exception e) {
            throw new RuntimeException("创建 RestClient 失败: " + e.getMessage(), e);
        }
    }

}
