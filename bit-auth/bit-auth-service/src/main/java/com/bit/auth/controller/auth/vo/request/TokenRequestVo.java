package com.bit.auth.controller.auth.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Datetime: 2025年11月07日17:39
 * @Author: Eleven52AC
 * @Description: 登录请求
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 4900538051686841772L;

    /**
     * 登入类型
     */
    private String loginType;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 密码
     */
    private String password;
}

