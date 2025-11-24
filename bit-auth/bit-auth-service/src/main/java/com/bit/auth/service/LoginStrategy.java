package com.bit.auth.service;

import com.bit.auth.controller.auth.vo.request.TokenRequestVo;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.auth.enums.login.LoginTypeEnum;
import com.bit.common.web.context.ClientMetaInfo;

/**
 * 登录服务接口
 * @Datetime: 2025年11月08日14:33
 * @Author: Eleven52AC
 */
public interface LoginStrategy  {
    
    LoginTypeEnum getLoginType();

    ApiResponse<String> login(TokenRequestVo request, ClientMetaInfo info);
}
