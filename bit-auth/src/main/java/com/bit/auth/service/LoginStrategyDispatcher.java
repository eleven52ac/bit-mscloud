package com.bit.auth.service;

import common.enums.LoginTypeEnum;
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

    private final Map<LoginTypeEnum, LoginService> strategyMap = new HashMap<>();

    /**
     * 构造方法
     * @param services
     */
    public LoginStrategyDispatcher(List<LoginService> services) {
        services.forEach(service -> strategyMap.put(service.getLoginType(), service));
    }

    public void login (String code){
        LoginTypeEnum loginType = LoginTypeEnum.fromCode(code);
        LoginService strategy = strategyMap.get(loginType);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到对应登入策略: " + code);
        }
        strategy.login();
    }
}
