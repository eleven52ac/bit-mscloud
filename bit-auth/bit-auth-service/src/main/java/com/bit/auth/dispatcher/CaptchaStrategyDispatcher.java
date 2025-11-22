package com.bit.auth.dispatcher;

import com.bit.auth.service.CaptchaStrategy;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.biz.CaptchaMethodEnum;
import com.bit.common.web.context.ClientMetaInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Datetime: 2025年11月09日17:51
 * @Author: Eleven52AC
 * @Description: 验证码策略分发器
 */
@Service
public class CaptchaStrategyDispatcher {

    private final Map<CaptchaMethodEnum, CaptchaStrategy> strategyMap = new HashMap<>();

    /**
     * 构造方法
     * @param services
     */
    public CaptchaStrategyDispatcher(List<CaptchaStrategy > services) {
        services.forEach(service -> strategyMap.put(service.getCaptchaMethod(), service));
    }

    /**
     *
     * @param identifier
     * @param captchaMethod
     * @param clientInfo
     * @Author: Eleven52AC
     * @Description: 注册
     */
    public ApiResponse<String> captcha (String identifier, String captchaMethod, ClientMetaInfo clientInfo){
        if (StringUtils.isBlank(captchaMethod)){
            return ApiResponse.badRequest("请求验证方式不能为空！");
        }
        CaptchaMethodEnum captchaMethodEnum = CaptchaMethodEnum.fromCode(captchaMethod);
        CaptchaStrategy strategy = strategyMap.get(captchaMethodEnum);
        if (strategy == null) {
            return ApiResponse.badRequest("未找到对应获取验证码策略: " + captchaMethod);
        }
        return strategy.captcha(identifier, clientInfo);
    }

}
