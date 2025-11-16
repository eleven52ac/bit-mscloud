package com.bit.common.utils.crypto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt密码工具类（基于Spring Security实现）
 * 
 * <p>提供安全的密码加密与验证功能，使用BCrypt强哈希算法，自动生成随机盐值。</p>
 * 
 * <p>特性说明：
 * <ul>
 *   <li>默认加密强度10（推荐生产环境使用）</li>
 *   <li>每次加密生成不同的哈希结果（内置随机盐）</li>
 *   <li>自动版本标识（$2a）</li>
 *   <li>线程安全实现</li>
 * </ul>
 */
public class BCryptUtils {
    // 推荐强度范围说明
    private static final int MIN_STRENGTH = 4;
    private static final int MAX_STRENGTH = 31;
    private static final int DEFAULT_STRENGTH = 10;

    // 主加密器（默认强度）
    private static final BCryptPasswordEncoder defaultEncoder = createEncoder(DEFAULT_STRENGTH);

    // 私有构造防止实例化
    private BCryptUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * 使用默认强度加密密码
     * 
     * @param rawPassword 明文密码（需非空且长度≥1）
     * @return 带版本信息的BCrypt哈希值（60字符）
     * @throws IllegalArgumentException 如果密码为空或空白
     */
    public static String encode(String rawPassword) {
        validatePassword(rawPassword);
        return defaultEncoder.encode(rawPassword);
    }

    /**
     * 使用指定强度加密密码（适用于特殊安全需求）
     * 
     * @param rawPassword 明文密码（需非空且长度≥1）
     * @param strength 加密强度（4-31）
     * @return 带版本信息的BCrypt哈希值（60字符）
     * @throws IllegalArgumentException 如果密码无效或强度超范围
     */
    public static String encode(String rawPassword, int strength) {
        validatePassword(rawPassword);
        validateStrength(strength);
        return createEncoder(strength).encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     * 
     * @param rawPassword 待验证的明文密码（需非空）
     * @param encodedPassword 存储的哈希值（需完整BCrypt格式）
     * @return 匹配返回true，否则false
     * @throws IllegalArgumentException 如果任一参数为空或格式无效
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            throw new IllegalArgumentException("Encoded password cannot be null or empty");
        }
        return defaultEncoder.matches(rawPassword, encodedPassword);
    }

    // 参数校验方法
    private static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password must contain non-whitespace characters");
        }
    }

    private static void validateStrength(int strength) {
        if (strength < MIN_STRENGTH || strength > MAX_STRENGTH) {
            throw new IllegalArgumentException(
                String.format("Strength must be between %d and %d", MIN_STRENGTH, MAX_STRENGTH)
            );
        }
    }

    // 创建加密器实例
    private static BCryptPasswordEncoder createEncoder(int strength) {
        return new BCryptPasswordEncoder(strength);
    }
}