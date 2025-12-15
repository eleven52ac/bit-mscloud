package com.bit.user.api.user;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.api.user.dto.request.UserLoginHistoryRequest;
import com.bit.user.api.user.dto.response.UserLoginHistoryResponse;
import com.bit.user.constant.user.UserLoginHistoryApiConstans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Datetime: 2025年11月21日14:03
 * @Author: Eleven52AC
 * @Description:
 */
@FeignClient(
        name = UserLoginHistoryApiConstans.SERVICE_ID,
        path = UserLoginHistoryApiConstans.API_PREFIX,
        contextId = "userLoginHistoryApi"
)
public interface UserLoginHistoryApi {

    @GetMapping({"/userId"})
    List<UserLoginHistoryResponse> recentLoginData(@RequestParam("userId") Long userId);

    @PostMapping({"/save"})
    ApiResponse<String> saveCurrentLoginRecord(@RequestBody UserLoginHistoryRequest request);

}
