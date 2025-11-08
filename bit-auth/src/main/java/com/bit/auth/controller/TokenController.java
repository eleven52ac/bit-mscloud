package com.bit.auth.controller;

import com.bit.auth.dto.request.LoginRequest;
import com.bit.auth.service.LoginStrategyDispatcher;
import common.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Datetime: 2025年11月07日17:33
 * @Author: Eleven52AC
 * @Description: token 控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth/token")
public class TokenController {

    @Autowired
    private LoginStrategyDispatcher  loginStrategyDispatcher;

    /**
     *  登录
     * @Author: Eleven52AC
     * @Description:
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {
        loginStrategyDispatcher.login(request);
        return ApiResponse.success();
    }

}
