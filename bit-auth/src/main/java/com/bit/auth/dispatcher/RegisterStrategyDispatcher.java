package com.bit.auth.dispatcher;

import com.bit.auth.dto.request.TokenRequest;
import com.bit.auth.service.RegisterStrategy;
import com.bit.common.core.context.ClientMetaInfo;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.RegisterTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年11月09日17:16
 * @Author: Eleven52AC
 * @Description: 登录策略分发器
 */
@Service
public class RegisterStrategyDispatcher {

    private final Map<RegisterTypeEnum, RegisterStrategy> strategyMap = new HashMap<>();

    /**
     * 构造方法
     * @param services
     */
    public RegisterStrategyDispatcher(List<RegisterStrategy > services) {
        services.forEach(service -> strategyMap.put(service.getRegisterType(), service));
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 注册
     * @param request
     * @param info
     */
    public ApiResponse<String> register (TokenRequest request, ClientMetaInfo info){
        if (request == null){
            return ApiResponse.badRequest("注册请求不能为空");
        }
        String code = request.getRegisterType();
        if (StringUtils.isBlank(request.getRegisterType())){
            return ApiResponse.badRequest("注册类型不能为空");
        }
        RegisterTypeEnum registerType = RegisterTypeEnum.fromCode(code);
        RegisterStrategy strategy = strategyMap.get(registerType);
        if (strategy == null) {
            return ApiResponse.badRequest("未找到对应注册策略: " + code);
        }
        return strategy.register(request, info);
    }

}
