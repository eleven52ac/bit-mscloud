package com.bit.common.utils.redis;

import cn.hutool.core.util.BooleanUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Datetime: 2025年04月22日21:32
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.utils
 * @Project: camellia
 * @Description: Redis工具类
 */
public class RedisUtils {


    /**
     * 尝试获取分布式锁
     * @param stringRedisTemplate Redis模板，用于操作Redis数据库
     * @param key 锁的键值，用于标识不同的锁
     * @return 返回是否成功获取锁
     *
     * 本方法通过Redis的setIfAbsent功能来实现分布式锁的机制
     * 它尝试在Redis中设置一个键值对，如果键不存在，则设置成功，表示获取了锁；
     * 如果键已经存在，则表示锁已经被其他线程或进程获取，当前线程需要等待
     *
     * 使用StringRedisTemplate作为参数，是因为它提供了操作String类型数据的便捷方法
     * 这里的"1"作为值并不重要，只是表示锁的状态，而10秒是锁的自动过期时间，防止死锁的发生
     *
     * 最后，使用BooleanUtil.isTrue(flag)来判断是否成功获取锁，是为了处理可能的null值情况，
     * 确保在任何情况下都能安全地返回boolean值
     */
    public static boolean tryLock(StringRedisTemplate stringRedisTemplate, String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }


    /**
     * 解锁方法，用于释放Redis中的锁
     * 此方法通过删除Redis中的键来实现解锁操作
     *
     * @param stringRedisTemplate Redis模板，用于执行Redis操作
     * @param key 锁的键值，即锁的唯一标识
     */
    public static void unLock(StringRedisTemplate stringRedisTemplate, String key){
        stringRedisTemplate.delete(key);
    }

    /**
     * 尝试使用给定的key通过Redis进行加锁，带有重试机制
     * 此方法简化了调用者的工作，通过提供默认的重试次数和延迟，使得加锁过程更为简便
     *
     * @param redisTemplate 用于操作Redis的模板对象，这里使用StringRedisTemplate
     * @param key           加锁的唯一键值，用于标识锁
     * @return boolean      表示是否成功获取锁
     * @throws InterruptedException 如果在等待锁时线程被中断，会抛出此异常
     */
    public static boolean tryLockWithRetry(StringRedisTemplate redisTemplate, String key) throws InterruptedException {
        // 调用重载方法，使用默认的重试次数（3次）和重试延迟（100毫秒）
        return tryLockWithRetry(redisTemplate, key, 3, 100);
    }


    /**
     * 尝试加锁，直到成功或达到最大重试次数
     * 此方法用于在分布式环境中尝试获取锁，以防止并发执行同一段代码
     * 它会尝试多次加锁，每次失败后都会等待一段时间再重试
     *
     * @param redisTemplate Redis模板，用于操作Redis
     * @param key 锁的键，用于标识锁
     * @param maxRetry 最大重试次数，表示尝试加锁的最大次数
     * @param intervalMillis 重试间隔时间，表示每次重试之间的等待时间
     * @return 如果成功获取锁，则返回true；否则返回false
     * @throws InterruptedException 如果线程在等待期间被中断
     */
    public static boolean tryLockWithRetry(StringRedisTemplate redisTemplate, String key, int maxRetry, long intervalMillis) throws InterruptedException {
        for (int i = 0; i < maxRetry; i++) {
            if (tryLock(redisTemplate, key)) {
                return true;
            }
            Thread.sleep(intervalMillis* (long)Math.pow(2, i));
        }
        return false;
    }

}
