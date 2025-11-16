package com.bit.common.core.constant.redis;

/**
 * @Datetime: 2025年04月06日00:41
 * @Author: Camellia.xiaohua/Bitspark
 * @Description: Redis常量类
 */
public final class RedisConstants {

    /**
     *  私有构造器防止实例化
     */
    private RedisConstants(){
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 用户token前缀
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 验证码失败次数
     */
    public static final String CAPTCHA_FAILURE_ATTEMPTS_PREFIX =  "captcha:failure:";

    /**
     * 验证码错误次数
     */
    public static final String CAPTCHA_ERROR_ATTEMPTS_PREFIX = "captcha:error:";

    /**
     * 邮箱验证码
     */
    public static final String CAPTCHA_EMAIL_PREFIX = "captcha:email:";

    /**
     * 手机验证码
     */
    public static final String CAPTCHA_PHONE_PREFIX = "captcha:phone:";

    /**
     * 用户信息
     */
    public static final String PERSON_INFO_PREFIX = "person:info:";

    /**
     * 邮箱验证码的过期时间
     */
    public static final Long CAPTCHA_EMAIL_TTL = 5L;

    /**
     * 用户信息过期时间
     */
    public static final Long USER_INFO_TTL = 30L;

    /**
     * 验证码频率限制
     */
    public static final String CAPTCHA_RATE_LIMIT_PREFIX = "captcha:rate:";

    /**
     * 注册频率限制
     */
    public static final String REGISTER_RATE_LIMIT_PREFIX = "register:rate:";

    /**
     * 验证码IP频率限制
     */
    public static final String CAPTCHA_IP_RATE_LIMIT_PREFIX = "captcha:ip:rate:";

    /**
     * 用户信息前缀
     */
    public static final String USER_INFO_PREFIX = "user:info:";

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "token:";

    /**
     * 登录失败次数
     */
    public static final String LOGIN_ATTEMPT_PREFIX = "login:attempt:";

    /**
     * 锁前缀
     */
    public static final String PERSON_LOCK_PREFIX = "person:lock:";

    /**
     * 秒杀锁前缀
     */
    public static final String SECKILL_LOCK_PREFIX = "seckill:order:";

    /**
     * 锁的过期时间（自定义单位：秒、分钟、小时、天）
     */
    public static final Long LOCK_TTL_30 = 30L;

    /**
     * 锁的过期时间（自定义单位：秒、分钟、小时、天）
     */
    public static final Long LOCK_TTL_60 = 60L;

    /**
     * 频率限制
     */
    public static final long MAX_CAPTCHA_REQUESTS = 5L;

    /**
     * 频率限制
     **/
    public static final long MAX_REGISTER_REQUESTS = 5L;
}
