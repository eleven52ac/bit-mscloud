package com.bit.auth.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @Datetime: 2025年04月01日22:18
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.service
 * @Project: camellia
 * @Description:
 */
public interface CaptchaService {

    /**
     * 发送验证码
     *
     * @param phoneOrEmail 手机号
     * @param session
     * @return
     */
    @DS("postgres")
    Boolean sendCaptcha(String phoneOrEmail, HttpSession session);

    @DS("postgres")
    ApiResponse<String> loginOrRegister(String phoneOrEmail, String password, String captcha, HttpSession session);
}
