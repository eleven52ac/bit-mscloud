package com.bit.user.controller.user;

import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.web.base.BaseController;
import com.bit.user.controller.user.vo.request.RegisterRequestVo;
import com.bit.user.dispatcher.RegisterStrategyDispatcher;
import com.bit.user.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
public class UserInfoController extends BaseController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RegisterStrategyDispatcher registerStrategyDispatcher;

    /**
     * 用户注册
     * @Author: Eleven52AC
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ApiResponse<String> createUser(@RequestBody RegisterRequestVo request) {
        if(ObjectUtils.isEmpty(request)){
            return ApiResponse.badRequest("注册信息为空");
        }
        return registerStrategyDispatcher.register(request, getClientInfo());
    }

}
