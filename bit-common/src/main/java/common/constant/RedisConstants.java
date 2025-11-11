package common.constant;

/**
 * @Datetime: 2025年04月06日00:41
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.constant
 * @Project: camellia-singular
 * @Description:
 */
public final class RedisConstants {

    /**
     * 用户token前缀
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    private RedisConstants(){
        // 私有构造器防止实例化
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 验证码失败次数
     */
    public static final String CAPTCHA_FAILURE_ATTEMPTS_PREFIX =  "captcha:failure:";

    public static final String CAPTCHA_ERROR_ATTEMPTS_PREFIX = "captcha:error:";

    public static final String CAPTCHA_EMAIL_PREFIX = "captcha:email:";

    public static final String CAPTCHA_PHONE_PREFIX = "captcha:phone:";

    public static final String PERSON_INFO_PREFIX = "person:info:";

    public static final Long CAPTCHA_EMAIL_TTL = 5L;

    public static final Long USER_INFO_TTL = 30L;

    public static final String CAPTCHA_RATE_LIMIT_PREFIX = "captcha:rate:";

    public static final String REGISTER_RATE_LIMIT_PREFIX = "register:rate:";

    public static final String CAPTCHA_IP_RATE_LIMIT_PREFIX = "captcha:ip:rate:";

    public static final String USER_INFO_PREFIX = "user:info:";

    // token
    public static final String TOKEN_PREFIX = "token:";

    public static final String LOGIN_ATTEMPT_PREFIX = "login:attempt:";

    public static final String PERSON_LOCK_PREFIX = "person:lock:";

    public static final String SECKILL_LOCK_PREFIX = "seckill:order:";

    /**
     * 锁的过期时间（自定义单位：秒、分钟、小时、天）
     */
    public static final Long LOCK_TTL_30 = 30L;

    public static final Long LOCK_TTL_60 = 60L;

    /**
     * 频率限制
     */
    public static final long MAX_CAPTCHA_REQUESTS = 5L;

    public static final long MAX_REGISTER_REQUESTS = 5L;
}
