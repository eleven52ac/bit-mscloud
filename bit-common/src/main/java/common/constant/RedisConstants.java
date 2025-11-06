package common.constant;

/**
 * @Datetime: 2025年04月06日00:41
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.constant
 * @Project: camellia-singular
 * @Description:
 */
public final class RedisConstants {

    private RedisConstants(){
        // 私有构造器防止实例化
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String CAPTCHA_EMAIL_PREFIX = "captcha:email:";

    public static final String CAPTCHA_PHONE_PREFIX = "captcha:phone:";

    public static final String PERSON_INFO_PREFIX = "person:info:";

    public static final Long CAPTCHA_EMAIL_TTL = 5L;

    public static final Long USER_INFO_TTL = 30L;

    public static final String CAPTCHA_RATE_LIMIT_PREFIX = "captcha:rate:";

    public static final String USER_INFO_PREFIX = "user:info:";

    public static final String LOGIN_ATTEMPT_PREFIX = "login:attempt:";

    public static final String PERSON_LOCK_PREFIX = "person:lock:";

    public static final String SECKILL_LOCK_PREFIX = "seckill:order:";
}
