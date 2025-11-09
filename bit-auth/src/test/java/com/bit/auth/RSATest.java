package com.bit.auth;

import commons.enums.RSAAlgorithmEnum;
import common.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static commons.enums.RSAAlgorithmEnum.*;
import static commons.enums.RSAKeySizeEnum.*;
import static commons.enums.RSASecretKey.*;

/**
 * @Datetime: 2025年06月05日11:11
 * @Author: Eleven也想AC
 * @Description: RSA加密解密测试
 */
@Slf4j
public class RSATest {


    public void testRSA() throws Exception {
        try {
            // 1. 生成新的密钥对
            log.info("生成新的RSA-2048密钥对...");
            RSAUtils rsa = new RSAUtils(RSA_4096.getKeySize());
            log.info("密钥长度: " + rsa.getKeySizeBits() + "位");

            // 2. 使用现有私钥初始化（模拟解密场景）
            log.info("使用现有私钥初始化解密器...");
            String privateKeyPEM = rsa.getPrivateKeyPEM();
            RSAUtils decryptor = new RSAUtils(privateKeyPEM);
            log.info("加载私钥长度: " + decryptor.getKeySizeBits() + "位");

            // 3. 模拟加密过程
            String originalText = "清风徐来，水波不兴。";
            log.info("原始文本: " + originalText);

            // 使用公钥加密
            String transformation = OAEP_SHA256.getAlgorithm();
            System.out.println("使用算法进行加密: " + transformation);

            Cipher encryptCipher = Cipher.getInstance(transformation);
            encryptCipher.init(Cipher.ENCRYPT_MODE, rsa.getPublicKey());
            byte[] encryptedBytes = encryptCipher.doFinal(originalText.getBytes(StandardCharsets.UTF_8));
            log.info("加密后字节长度: " + encryptedBytes.length + "字节");
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

            // 4. 解密过程
            log.info("开始解密...");
            String decryptedText = decryptor.decryptToString(encryptedBase64);
            log.info("解密结果: " + decryptedText);

            // 验证解密
            if (originalText.equals(decryptedText)) {
                log.info("✓ 解密成功，文本匹配！");
                System.out.println("✓ 解密成功，文本匹配！");
            } else {
                log.error("✗ 解密失败，文本不匹配！");
            }

        } catch (Exception e) {
            log.error("发生错误:");
            e.printStackTrace();
            log.error("环境信息:");
            log.error("Java版本: " + System.getProperty("java.version"));
            log.error("Java供应商: " + System.getProperty("java.vendor"));
        }
    }
    
    public void testRSA2() throws Exception {
        try {
            RSAUtils decryptor = new RSAUtils(RSA_SECRET_KEY_1.getPrivateKey(), RSA_SECRET_KEY_1.getPublicKey());
            String originalText = "wOKeWuep9EWam/WgNjjOdlIiIBYxY+orzkOlyKPGx5Y=";
            log.info("原始文本: " + originalText);

            // 使用公钥加密
            String transformation = RSAAlgorithmEnum.OAEP_SHA256.getAlgorithm();
            log.info("使用算法进行加密: " + transformation);

            Cipher encryptCipher = Cipher.getInstance(transformation);
            encryptCipher.init(Cipher.ENCRYPT_MODE, decryptor.getPublicKey());
            byte[] encryptedBytes = encryptCipher.doFinal(originalText.getBytes(StandardCharsets.UTF_8));
            log.info("加密后字节长度: " + encryptedBytes.length + "字节");
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
            log.info("加密后Base64: " + encryptedBase64);

            // 4. 解密过程
            log.info("开始解密...");
            String decryptedText = decryptor.decryptToString(encryptedBase64);
            log.info("解密结果: " + decryptedText);

            // 验证解密
            if (originalText.equals(decryptedText)) {
                System.out.println("✓ 解密成功，文本匹配！");
            } else {
                System.out.println("✗ 解密失败，文本不匹配！");
            }

        } catch (Exception e) {
            System.err.println("发生错误:");
            e.printStackTrace();
            System.out.println("\n环境信息:");
            System.out.println("Java版本: " + System.getProperty("java.version"));
            System.out.println("Java供应商: " + System.getProperty("java.vendor"));
        }
    }
}
