package com.bit.auth.service;

import com.bit.auth.dto.request.TokenRequest;
import common.dto.response.ApiResponse;
import common.dto.reuqest.ClientMetaInfo;
import common.enums.RegisterTypeEnum;

/**
 * @Datetime: 2025年11月09日17:24
 * @Author: Eleven52AC
 * @Description: 注册策略
 */
public interface RegisterStrategy {

    /**
     * 获取注册方式
     * @Author: Eleven52AC
     * @Description:
     * @return
     */
    RegisterTypeEnum getRegisterType();

    /**
     *
     * @Author: Eleven52AC
     * @Description: 注册
     * @param request
     * @param info
     * @return
     */
    ApiResponse<String> register(TokenRequest request, ClientMetaInfo info);
}
