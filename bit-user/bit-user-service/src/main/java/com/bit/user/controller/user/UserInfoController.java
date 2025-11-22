package com.bit.user.controller.user;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.repository.dataobject.user.UserInfoDo;
import com.bit.user.service.user.UserInfoService;
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
    public ApiResponse<String> createUser(@RequestBody UserInfoDo userInfo) {
        boolean save = userInfoService.save(userInfo);
        return save ? ApiResponse.success("创建用户成功") : ApiResponse.error("创建用户失败");
    }

    @GetMapping("/email")
    public UserInfoDo getUserInfoByEmail(@RequestParam(name = "email") String email) {
        return userInfoService.getUserInfoByEmail(email);
    }

    @GetMapping("/username")
    public UserInfoDo getUserInfoByUsername(@RequestParam(name = "username") String username) {
        return userInfoService.getUserInfoByUsername(username);
    }
}
