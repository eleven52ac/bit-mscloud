package common.utils.encrypt;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Datetime: 2025年01月02日23:28
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mscommon.utils.encrypt
 * @Project: camellia-mscloud
 * @Description:
 */
public class PBKDF2Util {
    // 密码哈希迭代次数，数字越大安全性越高
    private static final int ITERATIONS = 10000;
    // 密钥的长度（例如 256 比特）
    private static final int KEY_LENGTH = 256;

    /**
     * 生成PBKDF2加密密码
     *
     * @param password 用户的密码
     * @return 加密后的密码（盐值 + 密钥）
     * @throws Exception
     */
    public static String hashPassword(String password) throws Exception {
        // 生成随机盐值
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        // 使用PBKDF2对密码进行加密
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashedPassword = skf.generateSecret(spec).getEncoded();
        // 将盐值和加密后的密码一并存储
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hashedPassword);
    }

    /**
     * 验证密码是否匹配
     *
     * @param password 用户输入的密码
     * @param storedHash 存储的加密密码
     * @return 密码是否匹配
     * @throws Exception
     */
    public static boolean checkPassword(String password, String storedHash) throws Exception {
        // 从存储的哈希中提取盐值和加密后的密码
        String[] parts = storedHash.split("\\$");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
        // 使用相同的盐值和迭代次数对输入密码进行加密
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hashedPassword = skf.generateSecret(spec).getEncoded();
        // 比较加密后的密码和存储的密码
        return java.util.Arrays.equals(storedHashBytes, hashedPassword);
    }

}
