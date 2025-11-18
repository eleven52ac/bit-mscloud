package com.bit.common.web.context;
import com.bit.common.web.model.UserInfo;

/**
 * 用户上下文工具类，用于在当前线程中安全地存储和获取当前认证用户信息。
 * 通常在认证过滤器或拦截器中设置，在业务逻辑中读取。
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> CURRENT_USER = new ThreadLocal<>();

    public static void setCurrentUser(UserInfo userInfo) {
        CURRENT_USER.set(userInfo);
    }

    public static UserInfo getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}