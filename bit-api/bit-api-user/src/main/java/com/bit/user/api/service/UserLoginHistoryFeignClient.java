package com.bit.user.api.service;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.api.model.UserLoginHistoryEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Datetime: 2025年11月09日16:00
 * @Author: Eleven52AC
 * @Description:
 */
@FeignClient(name = "ms-user", path = "/user/login/history", contextId = "userLoginHistoryFeignClient")
public interface UserLoginHistoryFeignClient {

    /**
     *
     * @Author: Eleven52AC
     * @Description:
     * @param userId
     * @return List<UserLoginHistoryEntity>
     */
    @GetMapping("/userId")
    List<UserLoginHistoryEntity> recentLoginData(@RequestParam("userId") Long userId);

    /**
     *
     * @Author: Eleven52AC
     * @Description:
     * @param record
     */
    @PostMapping("/save")
    ApiResponse<String> saveCurrentLoginRecord(@RequestBody UserLoginHistoryEntity record);
}
