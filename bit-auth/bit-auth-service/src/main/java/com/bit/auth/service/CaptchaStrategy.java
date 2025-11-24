package com.bit.auth.service;


import com.bit.common.core.dto.response.ApiResponse;
import com.bit.auth.enums.captcha.CaptchaMethodEnum;
import com.bit.common.web.context.ClientMetaInfo;

/**
 * @Datetime: 2025年11月09日18:40
 * @Author: Eleven52AC
 * @Description: 验证码策略
 */
public interface CaptchaStrategy {

    /**
     *
     * @Author: Eleven52AC
     * @Description: 获取验证码类型
     * @return
     */
    CaptchaMethodEnum getCaptchaMethod();

    /**
     *
     * @param identifier 唯一标识
     * @param clientInfo
     * @return 验证码
     * @Author: Eleven52AC
     * @Description: 获取验证码
     */
    ApiResponse<String> captcha(String identifier, ClientMetaInfo clientInfo);
}
