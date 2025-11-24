package com.bit.common.redis.constant;

/**
 * @Datetime: 2025年11月23日17:06
 * @Author: Eleven52AC
 * @Description:
 *  Redis 原子计数器 Lua 脚本文本常量类。
 *
 *  主要用于集中管理 Redis 中常用的“原子计数”相关脚本，例如：
 *  - 原子自增（INCR）
 *  - 原子自增并带有过期策略（INCR + EXPIRE）
 *  - 原子增加指定值（INCRBY）
 *
 *  与普通 Redis 操作不同，Lua 脚本能够保证整个逻辑在 Redis 内部
 *  以原子操作的方式执行，不会被其他命令打断，因此非常适合计数、
 *  限流、并发统计等业务场景。
 *
 *  此类仅提供脚本文本，不负责构建 DefaultRedisScript 对象。
 */
public class RedisAtomicCounterConstants {

    /**
     * Lua 脚本：原子 +1，并在首次（值从 0 变成 1）时设置过期时间。
     *
     * 使用场景：
     *   - 固定窗口限流计数器
     *   - 用户某行为计数并限制窗口期内次数
     *
     * KEYS[1]：计数器的 Redis key
     * ARGV[1]：过期时间（秒）
     *
     * 执行逻辑：
     *   1. 对 key 执行 INCR 操作，使其自增 1。
     *   2. 若当前值为 1（说明第一次访问），则设置过期时间。
     *   3. 返回当前计数值。
     */
    public static final String LUA_INCR_AND_EXPIRE_ON_FIRST = """
        local current = redis.call('INCR', KEYS[1])
        if tonumber(current) == 1 then
            redis.call('EXPIRE', KEYS[1], ARGV[1])
        end
        return current
        """;

    /**
     * Lua 脚本：原子增加指定值，不设置过期时间。
     *
     * 使用场景：
     *   - 多线程统计计数
     *   - PV / UV 增量计数
     *   - 活动参与度统计
     *
     * KEYS[1]：计数器 key
     * ARGV[1]：增量（正数或负数均可）
     */
    public static final String LUA_INCR_BY = """
        local increment = tonumber(ARGV[1])
        return redis.call('INCRBY', KEYS[1], increment)
        """;

    /**
     * Lua 脚本：原子增加指定值，并且每次都设置过期时间。
     *
     * 使用场景：
     *   - 滑动 TTL 计数
     *   - 活跃度记录（用户最近行为时间）
     *   - 动态窗口计数
     *
     * KEYS[1]：计数器 key
     * ARGV[1]：增量
     * ARGV[2]：过期秒数（每次都会刷新）
     */
    public static final String LUA_INCR_BY_AND_EXPIRE_ALWAYS = """
        local newVal = redis.call('INCRBY', KEYS[1], tonumber(ARGV[1]))
        redis.call('EXPIRE', KEYS[1], ARGV[2])
        return newVal
        """;

}
