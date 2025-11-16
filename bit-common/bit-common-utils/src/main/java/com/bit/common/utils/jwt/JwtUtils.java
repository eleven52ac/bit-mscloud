package com.bit.common.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 *
 * 支持：
 *  生成 Token（含过期时间）
 *  验证 Token 签名与过期
 *  提取 Claims
 *  刷新 Token
 *
 * 注意：
 *  JWT 本身不存储敏感信息；
 *  只放 userId / email 等轻量字段；
 *  详细信息建议放 Redis。
 */
public class JwtUtils {

    /** 签名密钥（必须保密） */
    private static final String SECRET_KEY = "camellia20.@^*^$";

    /** 令牌过期时间（毫秒） */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1小时

    /** 令牌前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 生成 JWT 令牌
     *
     * @param claims 自定义声明，如 userId、email 等
     * @return JWT 字符串
     */
    public static String generateToken(Map<String, String> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        var builder = JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(expiration);

        claims.forEach(builder::withClaim);

        return builder.sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * 验证 JWT 是否有效（签名 + 过期）
     *
     * @param token 原始 token，可包含 "Bearer "
     * @return 是否有效
     */
    public static boolean verifyToken(String token) {
        try {
            DecodedJWT jwt = parseToken(token);
            return jwt.getExpiresAt().after(new Date());
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 解析并返回 JWT 对象（同时验证签名）
     *
     * @param token 原始 token，可包含 "Bearer "
     */
    public static DecodedJWT parseToken(String token) {
        token = cleanToken(token);
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);
    }

    /**
     * 从 Token 提取指定 Claim
     *
     * @param token 原始 token
     * @param key   Claim 键
     * @return Claim 值（字符串）
     */
    public static String getClaim(String token, String key) {
        try {
            DecodedJWT jwt = parseToken(token);
            return jwt.getClaim(key).asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isExpired(String token) {
        try {
            DecodedJWT jwt = parseToken(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true; // 解析异常视为过期
        }
    }

    /**
     * 刷新 Token（重新设置签发时间与过期时间）
     *
     * @param oldToken 旧 token
     * @return 新 token（内容一致但时间刷新）
     */
    public static String refreshToken(String oldToken) {
        try {
            DecodedJWT jwt = parseToken(oldToken);
            Map<String, String> claims = jwt.getClaims().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().asString()));
            return generateToken(claims);
        } catch (Exception e) {
            throw new RuntimeException("Token 无法刷新", e);
        }
    }

    /**
     * 移除 token 中的 Bearer 前缀并去除多余空格
     */
    private static String cleanToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token 不能为空");
        }
        token = token.trim();
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }
        return token;
    }
}
