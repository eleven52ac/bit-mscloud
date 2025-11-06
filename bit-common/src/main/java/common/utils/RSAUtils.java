package common.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

@Slf4j
public class RSAUtils {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final int keySizeBits; // 存储密钥位数

    /**
     * 生成新的RSA密钥对
     * @param keySizeBits 密钥长度 (推荐2048或4096)
     */
    public RSAUtils(int keySizeBits) throws NoSuchAlgorithmException {
        this.keySizeBits = keySizeBits;
        generateKeyPair();
    }

    /**
     * 从PEM格式的私钥字符串初始化
     * @param privateKeyStr PEM格式的私钥字符串
     */
    public RSAUtils(String privateKeyStr) throws Exception {
        this.privateKey = parsePrivateKey(privateKeyStr);
        this.publicKey = null;

        // 正确获取密钥位数
        if (this.privateKey instanceof RSAPrivateKey) {
            this.keySizeBits = ((RSAPrivateKey) this.privateKey).getModulus().bitLength();
        } else {
            throw new IllegalArgumentException("无法确定密钥长度");
        }
    }

    public RSAUtils(String privateKeyStr, String publicKeyStr) throws Exception {
        this.privateKey = parsePrivateKey(privateKeyStr);
        this.publicKey = parsePublicKey(publicKeyStr);

        // 正确获取密钥位数
        if (this.privateKey instanceof RSAPrivateKey) {
            this.keySizeBits = ((RSAPrivateKey) this.privateKey).getModulus().bitLength();
        } else {
            throw new IllegalArgumentException("无法确定密钥长度");
        }
    }


    /**
     * 从PEM文件初始化
     * @param privateKeyPath 私钥文件路径
     */
    public RSAUtils(Path privateKeyPath) throws Exception {
        this(Files.readString(privateKeyPath, StandardCharsets.UTF_8));
    }

    /**
     * 生成RSA密钥对
     */
    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySizeBits);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    /**
     * 解析PEM格式的私钥
     */
    private PrivateKey parsePrivateKey(String pemKey) throws Exception {
        // 移除PEM格式的头部和尾部标记
        String base64Key = pemKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // 移除所有空白字符

        // Base64解码
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        // 尝试PKCS#8格式
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的私钥格式", e);
        }
    }

    private static PublicKey parsePublicKey(String pemKey) throws Exception {
        // 移除PEM格式的头部和尾部标记
        String base64Key = pemKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 移除所有空白字符

        // Base64解码
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        // 使用X.509编码解析公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }


    /**
     * 获取Base64编码的PEM格式私钥
     */
    public String getPrivateKeyPEM() {
        String encoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" +
                formatAsPEM(encoded) +
                "\n-----END PRIVATE KEY-----";
    }

    /**
     * 获取Base64编码的PEM格式公钥
     */
    public String getPublicKeyPEM() {
        if (publicKey == null) {
            throw new IllegalStateException("公钥未初始化");
        }
        String encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" +
                formatAsPEM(encoded) +
                "\n-----END PUBLIC KEY-----";
    }

    /**
     * 将Base64字符串格式化为PEM格式 (每64字符一行)
     */
    private String formatAsPEM(String base64) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < base64.length(); i += 64) {
            int end = Math.min(i + 64, base64.length());
            result.append(base64, i, end).append("\n");
        }
        return result.toString();
    }

    /**
     * 保存私钥到文件
     * @param filePath 文件路径
     */
    public void savePrivateKey(Path filePath) throws IOException {
        Files.writeString(filePath, getPrivateKeyPEM(), StandardCharsets.UTF_8);
    }

    /**
     * 保存公钥到文件
     * @param filePath 文件路径
     */
    public void savePublicKey(Path filePath) throws IOException {
        Files.writeString(filePath, getPublicKeyPEM(), StandardCharsets.UTF_8);
    }

    /**
     * RSA解密方法
     * @param encryptedDataBase64 Base64编码的加密数据
     * @param paddingMode 填充模式 (PKCS1v15 或 OAEP)
     * @param hashAlgorithm OAEP填充时使用的哈希算法 (SHA1, SHA256等)
     * @return 解密后的原始字节数据
     */
    public byte[] decrypt(String encryptedDataBase64, String paddingMode, String hashAlgorithm) throws Exception {
        if (privateKey == null) {
            throw new IllegalStateException("私钥未初始化");
        }

        // Base64解码
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataBase64);

        // 正确计算期望的密文长度（字节）
        int expectedSizeBytes = keySizeBits / 8;

        // 验证加密数据长度
        if (encryptedData.length != expectedSizeBytes) {
            throw new IllegalArgumentException(
                    "密文长度无效。应为" + expectedSizeBytes + "字节（对应" + keySizeBits + "位密钥），" +
                            "实际为" + encryptedData.length + "字节。\n" +
                            "可能原因：\n" +
                            "1. 加密使用的公钥与当前私钥不匹配\n" +
                            "2. 加密时使用了不同的密钥长度\n" +
                            "3. 数据已被损坏或格式错误"
            );
        }

        // 配置填充方案
        String transformation;
        if ("PKCS1v15".equalsIgnoreCase(paddingMode)) {
            transformation = "RSA/ECB/PKCS1Padding";
        } else if ("OAEP".equalsIgnoreCase(paddingMode)) {
            transformation = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
            // 检查算法是否支持
            if (!isAlgorithmSupported(transformation)) {
                System.err.println("警告：当前环境不支持SHA-256 OAEP，尝试使用SHA-1");
                transformation = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
            }
        } else {
            throw new IllegalArgumentException("不支持的填充模式: " + paddingMode);
        }
        // 执行解密
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查算法是否支持
     */
    private boolean isAlgorithmSupported(String algorithm) {
        try {
            Cipher.getInstance(algorithm);
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            return false;
        }
    }

    /**
     * 使用默认参数解密
     */
    public byte[] decrypt(String encryptedDataBase64) throws Exception {
        return decrypt(encryptedDataBase64, "OAEP", "SHA256");
    }

    /**
     * 解密为UTF-8字符串
     */
    public String decryptToString(String encryptedDataBase64, String paddingMode, String hashAlgorithm) throws Exception {
        return new String(decrypt(encryptedDataBase64, paddingMode, hashAlgorithm), StandardCharsets.UTF_8);
    }

    /**
     * 使用默认参数解密为UTF-8字符串
     */
    public String decryptToString(String encryptedDataBase64) throws Exception {
        return new String(decrypt(encryptedDataBase64), StandardCharsets.UTF_8);
    }

    /**
     * 获取公钥（用于外部加密）
     * @return
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }


    /**
     * 获取私钥（谨慎使用）
     * @return
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取密钥位数
     * @return
     */
    public int getKeySizeBits() {
        return keySizeBits;
    }

}