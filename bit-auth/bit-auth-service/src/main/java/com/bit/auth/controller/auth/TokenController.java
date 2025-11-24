package com.bit.auth.controller.auth;

import com.bit.auth.dispatcher.CaptchaStrategyDispatcher;
import com.bit.auth.controller.auth.vo.request.TokenRequestVo;
import com.bit.auth.dispatcher.LoginStrategyDispatcher;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.web.base.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 权限认证
 * @Datetime: 2025年11月07日17:33
 * @Author: Eleven52AC
 * @Description: token 控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth/token")
public class TokenController extends BaseController {

    @Autowired
    private LoginStrategyDispatcher loginStrategyDispatcher;


    @Autowired
    private CaptchaStrategyDispatcher captchaStrategyDispatcher;

    /**
     * 登录
     * @Author: Eleven52AC
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody TokenRequestVo request) {
        return loginStrategyDispatcher.login(request, getClientInfo());
    }

    /**
     * 获取验证码
     * @Author: Eleven52AC
     * @param  identifier 唯一标识符
     * @param captchaMethod 验证码方式
     * @return
     */
    @GetMapping("/captcha")
    public ApiResponse<String> captcha(@RequestParam(name = "identifier") String identifier,
                                       @RequestParam(name = "captchaMethod") String captchaMethod) {
        return captchaStrategyDispatcher.captcha(identifier, captchaMethod, getClientInfo());
    }


}
