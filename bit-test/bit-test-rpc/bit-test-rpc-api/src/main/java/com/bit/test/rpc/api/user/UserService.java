package com.bit.test.rpc.api.user;

import com.bit.test.rpc.api.user.model.User;

/**
 * 用户服务接口
 * @Datetime: 2026年02月02日02:56
 * @Author: Eleven52AC
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 用于测试 mock 接口返回值
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }

}