package com.bit.user.controller;

import com.bit.user.entity.UserInfoEntity;
import com.bit.user.service.UserInfoService;
import common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Datetime: 2025年11月08日16:41
 * @Author: Eleven52AC
 * @Description: 用户信息控制类
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/create")
    public ApiResponse<String> createUser(@RequestBody UserInfoEntity userInfo) {
        boolean save = userInfoService.save(userInfo);
        return save ? ApiResponse.success("创建用户成功") : ApiResponse.error("创建用户失败");
    }

    @GetMapping("/email")
    public UserInfoEntity getUserInfoByEmail(@RequestParam(name = "email") String email) {
        return userInfoService.getUserInfoByEmail(email);
    }

    @GetMapping("/username")
    public UserInfoEntity getUserInfoByUsername(@RequestParam(name = "username") String username) {
        return userInfoService.getUserInfoByUsername(username);
    }
}
