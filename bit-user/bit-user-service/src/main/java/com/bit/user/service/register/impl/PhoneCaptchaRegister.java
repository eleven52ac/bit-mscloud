package com.bit.user.service.register.impl;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.enums.register.RegisterTypeEnum;
import com.bit.common.web.context.ClientMetaInfo;
import com.bit.user.controller.user.vo.request.RegisterRequestVo;
import com.bit.user.service.register.RegisterStrategy;
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
    public ApiResponse<String> register(RegisterRequestVo request, ClientMetaInfo info) {
        return null;
    }

}
