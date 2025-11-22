package com.bit.auth.service;

import com.bit.auth.controller.auth.vo.request.TokenRequestVo;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.biz.LoginTypeEnum;
import com.bit.common.web.context.ClientMetaInfo;

/**
 * @Datetime: 2025年11月08日14:33
 * @Author: Eleven52AC
 * @Description: 登录服务接口
 */
public interface LoginStrategy  {
    
    LoginTypeEnum getLoginType();

    ApiResponse<String> login(TokenRequestVo request, ClientMetaInfo info);
}
