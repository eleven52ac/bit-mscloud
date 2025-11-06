package common.utils;


import common.pojo.UserInfo;

/**
 * @Datetime: 2025年04月05日15:44
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.utils
 * @Project: camellia
 * @Description:
 */
public class UserThreadLocal {
    private static final ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();

    public static void setUserInfo(UserInfo userInfo) {
        userInfoThreadLocal.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return userInfoThreadLocal.get();
    }

    public static void removeUserInfo() {
        userInfoThreadLocal.remove();
    }
}
