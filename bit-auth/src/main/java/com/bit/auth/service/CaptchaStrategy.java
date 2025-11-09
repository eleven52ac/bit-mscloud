package com.bit.auth.service;

import common.dto.response.ApiResponse;
import common.dto.reuqest.ClientMetaInfo;
import common.enums.CaptchaMethodEnum;

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
