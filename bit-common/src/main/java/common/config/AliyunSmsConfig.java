package common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Datetime: 2024/11/6上午10:27
 * @author: Camellia.xioahua
 */
@Configuration
@ConfigurationProperties(prefix = "alibaba")
@Data
public class AliyunSmsConfig {

    /**
     * 短信签名
     */
    private String signName;
    /**
     * 短信模板
     */
    private String templateCode;
    /**
     * 阿里云accessKeyId
     */
    private String accessKeyId;
    /**
     * 阿里云accessKeySecret
     */
    private String accessKeySecret;
    /**
     * 阿里云regionId
     */
    private String regionId;
    /**
     * 阿里云endpoint
     */
    private String endpoint;
    /**
     * 阿里云产品
     */
    private String domain;
    /**
     * 阿里云产品
     */
    private String product;

}
