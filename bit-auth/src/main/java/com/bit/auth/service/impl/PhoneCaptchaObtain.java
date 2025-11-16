package com.bit.auth.service.impl;

import com.bit.auth.service.CaptchaStrategy;
import com.bit.common.core.context.ClientMetaInfo;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.CaptchaMethodEnum;
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
