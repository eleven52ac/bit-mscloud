package com.bit.common.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Datetime: 2025年06月16日21:24
 * @Author: Eleven也想AC
 * @Description:
 */
@Configuration
public class RedissonConfig {

    /**
     * 创建RedissonClient
     * @return
     */
    @Bean("redissonClient")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://100.97.223.54:6379").setPassword("camellia20.");
        return Redisson.create(config);
    }

    /**
     * 创建RedissonClient1
     * @return
     */
    //@Bean("redissonClient1")
    public RedissonClient redissonClient1() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://100.97.223.54:6380").setPassword("camellia20.");
        return Redisson.create(config);
    }

    /**
     * 创建RedissonClient2
     * @return
     */
    //@Bean("redissonClient2")
    public RedissonClient redissonClient2() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://100.97.223.54:6381").setPassword("camellia20.");
        return Redisson.create(config);
    }

    /**
     * 创建RedissonClient3
     * @return
     */
    //@Bean("redissonClient3")
    public RedissonClient redissonClient3() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://100.97.223.54:6382").setPassword("camellia20.");
        return Redisson.create(config);
    }

    
}
