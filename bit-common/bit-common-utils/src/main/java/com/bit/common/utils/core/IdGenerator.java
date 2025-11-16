package com.bit.common.utils.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 全局唯一 ID 生成器（支持分布式雪花算法 + Redis 容灾）
 *
 * 特性：
 *  1. 雪花算法生成有序唯一 ID；
 *  2. 自动分配 workerId / datacenterId；
 *  3. Redis 容灾兜底；
 *  4. 线程安全、性能高；
 */
@Slf4j
public class IdGenerator {

    /** 单例雪花实例 */
    private static volatile SnowflakeIdGenerator SNOWFLAKE_INSTANCE;

    /** Redis 自增 Fallback Key */
    private static final String REDIS_FALLBACK_KEY = "ID_GENERATOR_FALLBACK:";

    /** Redis 过期时间（秒） */
    private static final long REDIS_KEY_TTL = 60;

    // 禁止外部实例化
    private IdGenerator() {
    }

    /**
     * 生成分布式唯一ID（带Redis容灾）
     */
    public static long nextId(StringRedisTemplate redisTemplate) {
        try {
            return getSnowflakeInstance().nextId();
        } catch (Exception e) {
            log.error("❌ Snowflake 生成ID失败，自动切换Redis fallback，原因: {}", e.getMessage());

            // Redis 容灾模式
            try {
                long timestampPart = System.currentTimeMillis();
                Long seq = redisTemplate.opsForValue().increment(REDIS_FALLBACK_KEY + (timestampPart / 1000));
                redisTemplate.expire(REDIS_FALLBACK_KEY + (timestampPart / 1000), REDIS_KEY_TTL, TimeUnit.SECONDS);

                long fallbackId = (timestampPart << 20) | (seq % (1 << 20));
                log.warn("⚙️ Redis fallback 生成 ID = {}", fallbackId);
                return fallbackId;
            } catch (RedisConnectionFailureException ex) {
                log.error("❌ Redis fallback 失败，系统不可用: {}", ex.getMessage());
                throw new RuntimeException("全局ID生成失败：Snowflake 与 Redis 均不可用", ex);
            }
        }
    }

    /**
     * 生成分布式唯一ID（无Redis容灾）
     */
    public static long nextId() {
        return getSnowflakeInstance().nextId();
    }

    /**
     * 懒加载初始化 Snowflake 实例
     */
    private static SnowflakeIdGenerator getSnowflakeInstance() {
        if (SNOWFLAKE_INSTANCE == null) {
            synchronized (IdGenerator.class) {
                if (SNOWFLAKE_INSTANCE == null) {
                    SNOWFLAKE_INSTANCE = new SnowflakeIdGenerator(getDatacenterId(), getWorkerId());
                    log.info("✅ Snowflake 初始化成功 datacenterId={} workerId={}",
                            getDatacenterId(), getWorkerId());
                }
            }
        }
        return SNOWFLAKE_INSTANCE;
    }

    /**
     * 自动生成 workerId（基于IP Hash）
     */
    private static long getWorkerId() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return (ip.hashCode() & 0x1F);
        } catch (Exception e) {
            return new Random().nextInt(32);
        }
    }

    /**
     * 自动生成 datacenterId（基于主机名 Hash）
     */
    private static long getDatacenterId() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return (hostname.hashCode() & 0x1F);
        } catch (Exception e) {
            return 1L;
        }
    }
}
