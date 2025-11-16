package com.bit.common.core.enums;

/**
 * RSA算法枚举类（加密模式与签名算法）
 */
public enum RSAAlgorithmEnum {

    /**
     * 加密/解密填充模式
     */
    PKCS1("RSA/ECB/PKCS1Padding", "PKCS#1 v1.5 填充，传统模式"),
    OAEP_SHA1("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", "OAEP 填充，SHA-1，兼容性好"),
    OAEP_SHA256("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "OAEP 填充，SHA-256，更安全"),

    /**
     * 签名算法
     */
    SIGN_SHA1("SHA1withRSA", "SHA1 哈希签名（不推荐）"),
    SIGN_SHA256("SHA256withRSA", "SHA256 哈希签名（推荐）"),
    SIGN_SHA512("SHA512withRSA", "SHA512 哈希签名"),
    SIGN_PSS("RSASSA-PSS", "PSS 签名填充（需额外配置）");

    private final String algorithm;
    private final String description;

    RSAAlgorithmEnum(String algorithm, String description) {
        this.algorithm = algorithm;
        this.description = description;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return algorithm + " - " + description;
    }
}
