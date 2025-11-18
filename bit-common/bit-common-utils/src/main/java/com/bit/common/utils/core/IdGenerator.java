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
 * @author Eleven52AC
 * @Date: 2023/10/23 16:05
 * @Version: 1.0
 */
@Slf4j
public final class IdGenerator {

    /** 单例雪花实例 */
    private static volatile SnowflakeIdGenerator SNOWFLAKE_INSTANCE;

    /** Redis 自增 Fallback Key */
    private static final String REDIS_FALLBACK_KEY = "ID_GENERATOR_FALLBACK:";

    /** Redis 过期时间（秒） */
    private static final long REDIS_KEY_TTL = 60;

    /**
     * 禁止外部实例化
     */
    private IdGenerator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 生成分布式唯一ID（带Redis容灾）
     * @Author: Eleven52AC
     * @Description:
     * @param redisTemplate
     * @return
     */
    public static long nextId(StringRedisTemplate redisTemplate) {
        try {
            // 使用 Snowflake 方式生成 ID（主模式）
            return getSnowflakeInstance().nextId();
        }
        catch (Exception e) {
            // Snowflake 生成失败，进入 Redis 容灾模式
            log.error("Snowflake 生成ID失败，自动切换Redis fallback，原因: {}", e.getMessage());
            try {
                // 毫秒级时间戳，用作 fallback ID 高位
                long timestampPart = System.currentTimeMillis();
                // 使用“秒”为粒度的 key，获取当前秒的自增序号
                Long seq = redisTemplate.opsForValue().increment(REDIS_FALLBACK_KEY + (timestampPart / 1000)
                );
                // 设置该秒 key 的过期时间，避免积累过多 key
                redisTemplate.expire(
                        REDIS_FALLBACK_KEY + (timestampPart / 1000),
                        REDIS_KEY_TTL,
                        TimeUnit.SECONDS
                );
                // fallback ID 生成方式：高位-当前时间戳（毫秒）、低位-序列号（取 20 bit，避免过大）
                long fallbackId = (timestampPart << 20) | (seq % (1 << 20));
                log.warn("Redis fallback 生成 ID = {}", fallbackId);
                return fallbackId;
            } catch (RedisConnectionFailureException ex) {
                // Redis 也不可用，则无法生成 ID
                log.error("Redis fallback 失败，系统不可用: {}", ex.getMessage());
                throw new RuntimeException("全局ID生成失败：Snowflake 与 Redis 均不可用", ex);
            }
        }
    }


    /**
     * 生成分布式唯一ID（无Redis容灾）
     *
     * <p>该方法基于Snowflake算法生成全局唯一的64位ID，适用于分布式系统环境。
     * ID结构包含时间戳、工作节点ID和序列号，确保在不同节点上生成的ID不会冲突。</p>
     *
     * @return long 分布式唯一ID
     * @see IdGenerator#getSnowflakeInstance()
     * @see IdGenerator#nextId()
     */
    public static long nextId() {
        // 获取Snowflake实例并生成下一个唯一ID
        return getSnowflakeInstance().nextId();
    }


    /**
     * 获取 Snowflake ID 生成器的单例实例（懒加载 + 线程安全）。
     * <p>
     * 采用双重检查锁定（Double-Checked Locking, DCL）模式实现延迟初始化：
     * <ul>
     *   <li>首次调用时，按需创建 {@link SnowflakeIdGenerator} 实例；</li>
     *   <li>后续调用直接返回已初始化的单例，避免同步开销；</li>
     *   <li>通过 {@code synchronized} 块和 volatile 语义（假设 {@code SNOWFLAKE_INSTANCE} 为 volatile）保证线程安全。</li>
     * </ul>
     * <p>
     * 实例初始化时会自动获取数据中心 ID 和工作节点 ID（通过 {@link #getDatacenterId()} 和 {@link #getWorkerId()}），
     * 并记录初始化日志。
     *
     * @return 全局唯一的 {@link SnowflakeIdGenerator} 实例
     * @implNote 必须确保 {@code SNOWFLAKE_INSTANCE} 字段使用 {@code volatile} 修饰，
     *           以防止指令重排序导致其他线程访问到未完全初始化的对象。
     */
    private static SnowflakeIdGenerator getSnowflakeInstance() {
        // 第一次检查：避免不必要的同步开销
        if (SNOWFLAKE_INSTANCE == null) {
            // 同步锁，确保只有一个线程初始化实例
            synchronized (IdGenerator.class) {
                // 第二次检查：防止并发情况下重复创建实例
                if (SNOWFLAKE_INSTANCE == null) {
                    // 使用数据中心ID和工作节点ID创建雪花算法生成器
                    SNOWFLAKE_INSTANCE = new SnowflakeIdGenerator(getDatacenterId(), getWorkerId());
                    log.info("Snowflake 初始化成功 datacenterId={} workerId={}", getDatacenterId(), getWorkerId());
                }
            }
        }
        // 返回单例实例
        return SNOWFLAKE_INSTANCE;
    }

    /**
     * 自动生成 workerId（基于本机 IP 的哈希值）
     * Snowflake 算法中 workerId 范围通常为 0~31（5 bit，2^5 = 32）
     */
    private static long getWorkerId() {
        try {
            // 获取当前机器的 IP 地址
            String ip = InetAddress.getLocalHost().getHostAddress();
            // 取 IP 字符串的 hash 并通过 0x1F（即 31）进行按位与，确保结果落在 0~31 范围
            return (ip.hashCode() & 0x1F);
        } catch (Exception e) {
            // 若获取 IP 失败，则随机生成一个 workerId（0~31），保证服务可用性
            return new Random().nextInt(32);
        }
    }

    /**
     * 自动生成 datacenterId（基于本机 Hostname 的哈希值）
     * datacenterId 范围通常为 0~31（5 bit）
     */
    private static long getDatacenterId() {
        try {
            // 获取当前机器的主机名
            String hostname = InetAddress.getLocalHost().getHostName();
            // 主机名 hash 后，与 0x1F 做 AND，限制在 0~31 范围内
            return (hostname.hashCode() & 0x1F);
        } catch (Exception e) {
            // 若出现异常（如无 hostname），给一个默认的数据中心 ID：1
            return 1L;
        }
    }

}
