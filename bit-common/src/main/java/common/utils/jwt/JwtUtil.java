package common.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 密钥
     */
    private static final String SECRET_KEY = "camellia20.@^*^$";

    /**
     * 令牌过期时间（毫秒）
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * 生成JWT令牌
     *
     * @param claims 自定义的声明（键值对）
     * @return 生成的JWT令牌
     */
    public static String generateToken(Map<String, String> claims) {
        // 设置令牌的签发时间和过期时间
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        // 使用HS256算法和密钥生成JWT
        var builder = JWT.create()
                .withIssuedAt(now) // 签发时间
                .withExpiresAt(expiration); // 过期时间
        // 遍历Map，将每个claim单独添加
        for (Map.Entry<String, String> entry : claims.entrySet()) {
            builder.withClaim(entry.getKey(), entry.getValue());
        }
        return builder.sign(Algorithm.HMAC256(SECRET_KEY)); // 使用HS256算法签名
    }

    /**
     * 验证JWT令牌
     *
     * @param token 需要验证的令牌
     * @return 解析后的令牌内容
     */
    public static boolean verifyToken(String token) {
        token = token.replace("Bearer ", "");
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            return true; // 验证成功，返回 true
        } catch (JWTVerificationException exception) {
            // 验证失败，返回 false
            return false;
        }
    }


}
