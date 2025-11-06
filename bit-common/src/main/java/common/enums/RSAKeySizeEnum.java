package commons.enums;

/**
 * RSA密钥长度枚举类
 */
public enum RSAKeySizeEnum {
    RSA_1024(1024),
    RSA_2048(2048),
    RSA_3072(3072),
    RSA_4096(4096);

    private final int keySize;

    RSAKeySizeEnum(int keySize) {
        this.keySize = keySize;
    }

    public int getKeySize() {
        return keySize;
    }
}
