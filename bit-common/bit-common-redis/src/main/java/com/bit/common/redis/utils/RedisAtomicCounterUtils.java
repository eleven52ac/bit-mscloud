package com.bit.common.redis.utils;

import org.springframework.data.redis.core.script.DefaultRedisScript;

import static com.bit.common.redis.constant.RedisAtomicCounterConstants.*;

/**
 * @Description:
 *  Redis 原子计数器脚本工具类。
 *
 *  该类负责将 RedisAtomicCounterConstants 中的 Lua 脚本文本
 *  包装成 DefaultRedisScript 对象，使其可以直接被 Spring Data Redis 执行。
 *
 *  提供三类常用原子计数脚本：
 *  1. INCR_AND_EXPIRE_ON_FIRST：原子 +1，首次写入时设置过期（常用于限流）
 *  2. INCR_BY：原子增加指定值（不带过期）
 *  3. INCR_BY_AND_EXPIRE_ALWAYS：原子增加指定值并刷新 TTL
 *
 *  使用示例：
 *
 *      Long count = redisTemplate.execute(
 *          RedisAtomicCounterUtils.INCR_AND_EXPIRE_ON_FIRST,
 *          List.of("counter:login:uid123"),
 *          "60"  // 过期时间（秒）
 *      );
 *
 */
public class RedisAtomicCounterUtils {

    /**
     * 原子 +1，如果是第一次（值从 0 变成 1），设置过期时间。
     *
     * 对应脚本：LUA_INCR_AND_EXPIRE_ON_FIRST
     *
     * KEYS[1]：计数器 key
     * ARGV[1]：过期时间（秒）
     */
    public static final DefaultRedisScript<Long> INCR_AND_EXPIRE_ON_FIRST;

    /**
     * 原子增加指定值，不设置过期时间。
     *
     * 对应脚本：LUA_INCR_BY
     *
     * KEYS[1]：计数器 key
     * ARGV[1]：增量（正数为增加，负数为减少）
     */
    public static final DefaultRedisScript<Long> INCR_BY;

    /**
     * 原子增加指定值，并始终设置过期时间。
     *
     * 对应脚本：LUA_INCR_BY_AND_EXPIRE_ALWAYS
     *
     * KEYS[1]：计数器 key
     * ARGV[1]：增量值
     * ARGV[2]：过期时间（秒）
     */
    public static final DefaultRedisScript<Long> INCR_BY_AND_EXPIRE_ALWAYS;

    /**
     * 静态初始化脚本。
     *
     * DefaultRedisScript 需要设置：
     * - resultType：脚本返回值类型
     * - scriptText：Lua 脚本文本
     *
     * 由于脚本为静态且线程安全，因此一次构建后即可全局复用。
     */
    static {

        // ============================ 1. INCR + EXPIRE_FIRST ============================
        INCR_AND_EXPIRE_ON_FIRST = new DefaultRedisScript<>();
        INCR_AND_EXPIRE_ON_FIRST.setResultType(Long.class);
        INCR_AND_EXPIRE_ON_FIRST.setScriptText(LUA_INCR_AND_EXPIRE_ON_FIRST);

        // ============================ 2. INCRBY =========================================
        INCR_BY = new DefaultRedisScript<>();
        INCR_BY.setResultType(Long.class);
        INCR_BY.setScriptText(LUA_INCR_BY);

        // ============================ 3. INCRBY + EXPIRE_ALWAYS ==========================
        INCR_BY_AND_EXPIRE_ALWAYS = new DefaultRedisScript<>();
        INCR_BY_AND_EXPIRE_ALWAYS.setResultType(Long.class);
        INCR_BY_AND_EXPIRE_ALWAYS.setScriptText(LUA_INCR_BY_AND_EXPIRE_ALWAYS);
    }

}
