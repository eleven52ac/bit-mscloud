package com.bit.auth.service.impl;

import com.bit.auth.service.CaptchaStrategy;
import common.dto.response.ApiResponse;
import common.dto.reuqest.ClientMetaInfo;
import common.enums.CaptchaMethodEnum;
import org.springframework.stereotype.Service;

/**
 * @Datetime: 2025年11月09日18:54
 * @Author: Eleven52AC
 * @Description: 手机验证码获取服务实现类
 */
@Service
public class PhoneCaptchaObtain implements CaptchaStrategy {
    @Override
    public CaptchaMethodEnum getCaptchaMethod() {
        return CaptchaMethodEnum.PHONE_CAPTCHA;
    }

    @Override
    public ApiResponse<String> captcha(String identifier, ClientMetaInfo clientInfo) {
        return null;
    }
}
