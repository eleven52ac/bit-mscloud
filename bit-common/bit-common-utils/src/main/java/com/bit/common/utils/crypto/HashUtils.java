package com.bit.common.utils.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {

    /**
     * 计算给定字符串的哈希值（使用 SHA-256 算法）。
     *
     * @param input 要计算哈希值的字符串
     * @return 计算后的哈希值（十六进制字符串）
     */
    public static String calculateSHA256(String input) {
        return calculateHash(input, "SHA-256");
    }

    /**
     * 计算给定字符串的哈希值（使用指定的算法）。
     *
     * @param input    要计算哈希值的字符串
     * @param algorithm 哈希算法（例如 "SHA-256", "MD5" 等）
     * @return 计算后的哈希值（十六进制字符串）
     */
    public static String calculateHash(String input, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = messageDigest.digest(input.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported hashing algorithm: " + algorithm, e);
        }
    }


    /**
     * 计算给定字符串的 Base64 编码哈希值（SHA-256）。
     *
     * @param input 要计算哈希值的字符串
     * @return 计算后的哈希值的 Base64 编码
     */
    public static String calculateBase64SHA256(String input) {
        return calculateBase64Hash(input, "SHA-256");
    }

    /**
     * 计算给定字符串的 Base64 编码哈希值（使用指定的算法）。
     *
     * @param input    要计算哈希值的字符串
     * @param algorithm 哈希算法（例如 "SHA-256", "MD5" 等）
     * @return 计算后的哈希值的 Base64 编码
     */
    public static String calculateBase64Hash(String input, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = messageDigest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported hashing algorithm: " + algorithm, e);
        }
    }


    /**
     * 计算 HMAC（基于 SHA-256 算法的 HMAC）
     *
     * @param data 要计算 HMAC 的数据
     * @param key  用于 HMAC 的密钥
     * @return HMAC 结果（十六进制字符串）
     */
    public static String calculateHMACSHA256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating HMAC: " + e.getMessage(), e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // 小写十六进制
        }
        return sb.toString();
    }
}
