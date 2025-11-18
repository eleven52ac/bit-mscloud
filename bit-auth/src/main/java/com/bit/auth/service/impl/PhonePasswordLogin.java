package com.bit.auth.service.impl;

import com.bit.auth.dto.request.TokenRequest;
import com.bit.auth.service.LoginStrategy;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.biz.LoginTypeEnum;
import com.bit.common.web.context.ClientMetaInfo;

/**
 * @Datetime: 2025年11月08日15:25
 * @Author: Eleven52AC
 * @Description: 手机密码登录服务实现类
 */
public class PhonePasswordLogin implements LoginStrategy {
    @Override
    public LoginTypeEnum getLoginType() {
        return null;
    }

    @Override
    public ApiResponse<String> login(TokenRequest request, ClientMetaInfo info) {
        return null;
    }
}
