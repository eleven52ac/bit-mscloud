package com.bit.user.api.service;

import com.bit.user.api.model.UserInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Datetime: 2025年11月08日16:54
 * @Author: Eleven52AC
 * @Description: 用户信息服务 API
 */
@FeignClient(name = "ms-user", path = "api/user", contextId = "userInfoFeignClient")
public interface UserInfoFeignClient {

    @GetMapping("/username")
    UserInfoEntity getUserInfoByUsername(@RequestParam("username") String username);

    boolean existsByEmail(String email);

    @GetMapping("/email")
    UserInfoEntity getUserInfoByEmail(@RequestParam ("email") String email);
}
