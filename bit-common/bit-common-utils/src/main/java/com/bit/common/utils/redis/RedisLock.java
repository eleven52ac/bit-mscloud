package com.bit.common.utils.redis;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Datetime: 2025年06月08日17:56
 * @Author: Eleven也想AC
 * @Description: Redis分布式锁
 */
public class RedisLock{

    private StringRedisTemplate stringRedisTemplate;

    private String lockKey;

    private static final String KEY_PREFIX = "lock:";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true)+"-";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    /**
     * 释放锁的Lua脚本
     */
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private RedisLock(){}

    public RedisLock(StringRedisTemplate stringRedisTemplate, String lockKey) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockKey = lockKey;
    }

    /**
     * 尝试获取锁
     * @param timeout
     * @return
     */
    public boolean tryLock(long timeout){
        // 获取线程id
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + lockKey, threadId, timeout, TimeUnit.SECONDS);
        // 若直接返回success自动拆箱可能存在空指针风险，需用BooleanUtil.isTrue()。
        return BooleanUtil.isTrue(success);
    }


    /**
     * 释放锁
     */
    public void unLock() {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + lockKey),
                threadId
        );
    }

}
