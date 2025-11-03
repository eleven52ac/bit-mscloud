package mscommon.utils.encrypt;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @Datetime: 2025年01月02日14:05
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: mscommon.utils.encrypt
 * @Project: camellia-mscloud
 * @Description:
 */
public class BCryptUtil {

    // 设置工作因子（Cost factor），决定加密的复杂度，数字越大计算越慢，安全性越高
    private static final int WORK_FACTOR = 12;

    /**
     * 生成BCrypt加密密码
     *
     * @param password 用户的密码
     * @return 加密后的密码
     */
    public static String hashPassword(String password) {
        // 生成盐值并加密密码
        return BCrypt.hashpw(password, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * 验证密码是否匹配
     *
     * @param password 用户输入的密码
     * @param hashedPassword 存储的加密密码
     * @return 密码是否匹配
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        // 使用BCrypt提供的checkpw方法验证密码是否正确
        return BCrypt.checkpw(password, hashedPassword);
    }
}