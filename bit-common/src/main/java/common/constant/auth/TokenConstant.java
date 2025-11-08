package common.constant.auth;

/**
 * @Datetime: 2025年11月08日14:26
 * @Author: Eleven52AC
 * @Description: token常量
 */
public final class TokenConstant {

    private TokenConstant(){
        // 私有构造器防止实例化
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String LOGIN_TYPE = "token";
}
