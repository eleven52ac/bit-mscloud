package com.bit.user.service.register;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.enums.register.RegisterTypeEnum;
import com.bit.common.web.context.ClientMetaInfo;
import com.bit.user.controller.user.vo.request.RegisterRequestVo;

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
    ApiResponse<String> register(RegisterRequestVo request, ClientMetaInfo info);
}
