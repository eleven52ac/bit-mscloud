package com.bit.auth.service.impl;

import com.bit.auth.dto.request.TokenRequest;
import com.bit.auth.service.RegisterStrategy;
import com.bit.common.core.context.ClientMetaInfo;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.RegisterTypeEnum;
import org.springframework.stereotype.Service;

/**
 * @Datetime: 2025年11月09日17:25
 * @Author: Eleven52AC
 * @Description: 手机验证码注册服务实现类
 */
@Service
public class PhoneCaptchaRegister implements RegisterStrategy {
    @Override
    public RegisterTypeEnum getRegisterType() {
        return RegisterTypeEnum.PHONE_CAPTCHA;
    }

    @Override
    public ApiResponse<String> register(TokenRequest request, ClientMetaInfo info) {
        return null;
    }

}
