package com.bit.auth.api.auth;

import com.bit.auth.constant.auth.AuthApiConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *  认证接口
 * @Datetime: 2025年11月21日17:06
 * @Author: Eleven52AC
 */
@FeignClient(
        name = AuthApiConstants.SERVICE_ID,
        path = AuthApiConstants.API_PREFIX,
        contextId = "authApi"
)
public interface AuthApi {
}
