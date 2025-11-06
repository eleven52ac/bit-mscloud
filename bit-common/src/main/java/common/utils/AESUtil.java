package common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String AES = "AES";
    private static final int GCM_IV_LENGTH = 12; // 推荐GCM IV长度
    private static final int GCM_TAG_LENGTH = 128; // GCM认证标签长度（位）
    private static final int SALT_LENGTH = 16; // PBKDF2盐值长度
    private static final int ITERATION_COUNT = 65536; // PBKDF2迭代次数
    private static final int KEY_LENGTH = 256; // 密钥长度

    // 支持的加密模式
    public enum Mode {
        CBC, ECB, GCM
    }

    /**
     * 生成AES密钥（随机）
     */
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(AES);
        keyGenerator.init(KEY_LENGTH);
        return keyGenerator.generateKey();
    }

    /**
     * 从密码派生AES密钥（PBKDF2WithHmacSHA256）
     */
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(), 
                salt.getBytes(StandardCharsets.UTF_8),
                ITERATION_COUNT, 
                KEY_LENGTH
        );
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AES);
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密数据
     *
     * @param input 明文数据
     * @param key   密钥
     * @param mode  加密模式
     * @return Base64编码的加密结果（包含IV/盐值）
     */
    public static String encrypt(String input, SecretKey key, Mode mode) throws Exception {
        // 初始化加密器
        Cipher cipher = Cipher.getInstance(getTransformation(mode));
        
        // 生成IV/Nonce
        byte[] iv = new byte[0];
        if (mode != Mode.ECB) {
            iv = new SecureRandom().generateSeed(
                mode == Mode.GCM ? GCM_IV_LENGTH : 16
            );
        }

        // 配置加密参数
        switch (mode) {
            case CBC:
                cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
                break;
            case GCM:
                cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
                break;
            default: // ECB
                cipher.init(Cipher.ENCRYPT_MODE, key);
        }

        // 执行加密
        byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // 组合IV和密文
        byte[] encryptedData;
        if (mode == Mode.ECB) {
            encryptedData = cipherText;
        } else {
            encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
        }

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 解密数据
     *
     * @param cipherText Base64编码的密文（包含IV/盐值）
     * @param key        密钥
     * @param mode       加密模式
     * @return 解密后的原始字符串
     */
    public static String decrypt(String cipherText, SecretKey key, Mode mode) throws Exception {
        // 解码Base64
        byte[] encryptedData = Base64.getDecoder().decode(cipherText);
        
        // 初始化解密器
        Cipher cipher = Cipher.getInstance(getTransformation(mode));
        
        // 分离IV和密文
        byte[] iv = new byte[0];
        byte[] payload = encryptedData;
        
        if (mode != Mode.ECB) {
            int ivLength = (mode == Mode.GCM) ? GCM_IV_LENGTH : 16;
            iv = new byte[ivLength];
            payload = new byte[encryptedData.length - ivLength];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            System.arraycopy(encryptedData, iv.length, payload, 0, payload.length);
        }

        // 配置解密参数
        switch (mode) {
            case CBC:
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
                break;
            case GCM:
                cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
                break;
            default: // ECB
                cipher.init(Cipher.DECRYPT_MODE, key);
        }

        // 执行解密
        byte[] plainText = cipher.doFinal(payload);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    // 获取完整的算法字符串
    private static String getTransformation(Mode mode) {
        switch (mode) {
            case CBC: return "AES/CBC/PKCS5Padding";
            case GCM: return "AES/GCM/NoPadding";
            default: return "AES/ECB/PKCS5Padding"; // ECB
        }
    }

    public static SecretKey restoreKeyFromBase64(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
    }
}