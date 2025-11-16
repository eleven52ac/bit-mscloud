package com.bit.auth.service;

import com.bit.auth.dto.request.TokenRequest;
import com.bit.common.core.context.ClientMetaInfo;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.LoginTypeEnum;

/**
 * @Datetime: 2025年11月08日14:33
 * @Author: Eleven52AC
 * @Description: 登录服务接口
 */
public interface LoginStrategy  {
    
    LoginTypeEnum getLoginType();

    ApiResponse<String> login(TokenRequest request, ClientMetaInfo info);
}
