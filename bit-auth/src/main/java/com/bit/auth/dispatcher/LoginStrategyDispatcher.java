package com.bit.auth.dispatcher;

import com.bit.auth.dto.request.TokenRequest;
import com.bit.auth.service.LoginStrategy;
import common.dto.response.ApiResponse;
import common.dto.reuqest.ClientMetaInfo;
import common.enums.LoginTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年11月08日14:43
 * @Author: Eleven52AC
 * @Description: 登录策略分发器
 */
@Service
public class LoginStrategyDispatcher {

    private final Map<LoginTypeEnum, LoginStrategy> strategyMap = new HashMap<>();

    /**
     * 构造方法
     * @param services
     */
    public LoginStrategyDispatcher(List<LoginStrategy > services) {
        services.forEach(service -> strategyMap.put(service.getLoginType(), service));
    }

    public ApiResponse<String> login (TokenRequest request, ClientMetaInfo info){
        if (request == null){
            return ApiResponse.badRequest("登录请求不能为空");
        }
        String code = request.getLoginType();
        if (StringUtils.isBlank(request.getLoginType())){
            return ApiResponse.badRequest("登录类型不能为空");
        }
        LoginTypeEnum loginType = LoginTypeEnum.fromCode(code);
        LoginStrategy  strategy = strategyMap.get(loginType);
        if (strategy == null) {
            return ApiResponse.badRequest("未找到对应登入策略");
        }
        return strategy.login(request, info);
    }
}
