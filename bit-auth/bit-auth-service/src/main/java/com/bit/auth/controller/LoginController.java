package com.bit.auth.controller;

import cn.hutool.core.util.NumberUtil;
import com.bit.auth.service.CaptchaService;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.security.annotation.CheckLogin;
import com.bit.common.web.context.UserContext;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Datetime: 2025年04月01日17:21
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.controller
 * @Project: camellia
 * @Description: Redis短信验证实现思路
 */
@RestController
@RequestMapping("/singular")
@Slf4j
public class LoginController {

    static final ConcurrentHashMap<String, CaptchaService> strategyMap = new ConcurrentHashMap<>();

    public LoginController() {}

    /**
     * 构造函数，配置策略组。
     * @param captchaService
     * @param emailCaptchaService
     */
    @Autowired
    private LoginController(@Qualifier("smsCaptchaServiceImpl") CaptchaService captchaService,
                            @Qualifier("emailCaptchaServiceImpl") CaptchaService emailCaptchaService) {
        strategyMap.put("sms", captchaService);
        strategyMap.put("email", emailCaptchaService);
    }

    /**
     * 发送验证码
     * @param phoneOrEmail
     * @param session
     * @return
     */
    @PostMapping("/sms/verification")
    public ApiResponse<String> smsVerification(@RequestParam("phoneOrEmail") String phoneOrEmail, HttpSession session) {
        try{
            // 1. 字符串空校验
            if (phoneOrEmail == null) return ApiResponse.badRequest("验证方式不能为空！");
            // 2. 判断是号码还是邮箱
            Boolean result;
            if(phoneOrEmail.contains("@")){
                result = strategyMap.get("email").sendCaptcha(phoneOrEmail, session);
            }else if(NumberUtil.isNumber(phoneOrEmail)){
                result = strategyMap.get("sms").sendCaptcha(phoneOrEmail, session);
            }else return ApiResponse.badRequest("验证方式不支持！");
            return result ? ApiResponse.success("验证码发送成功！") : ApiResponse.error("验证码发送失败！");
        }catch (Exception e){
            log.error("发送失败！", e);
            return ApiResponse.error("验证码发送失败！");
        }
    }


    /**
     * 用户登入
     * @param phoneOrEmail
     * @param password
     * @param captcha
     * @param session
     * @return
     */
    @PostMapping("/user/login")
    public ApiResponse userLogin(@RequestParam("phoneOrEmail") String phoneOrEmail,
                                 @RequestParam("password") String password,
                                 @RequestParam("captcha") String captcha,
                                 HttpSession session){
        try{
            // 1. 字符串空校验
            if (phoneOrEmail == null) return ApiResponse.badRequest("验证方式不能为空！");
            if (password == null) return ApiResponse.badRequest("密码不能为空！");
            if (captcha == null) return ApiResponse.badRequest("验证码不能为空！");
            // 2. 判断是号码还是邮箱
            if (phoneOrEmail.contains("@")){
                return strategyMap.get("email").loginOrRegister(phoneOrEmail, password, captcha, session);
            }else if(NumberUtil.isNumber(phoneOrEmail)){
                return strategyMap.get("sms").loginOrRegister(phoneOrEmail, password, captcha, session);
            } else return ApiResponse.badRequest("验证方式不支持！");
        }catch (Exception e){
            log.error("登录失败！", e);
            return ApiResponse.error("登录失败！");
        }
    }

    @GetMapping("user/threadInfo")
    @CheckLogin
    public ApiResponse userThreadInfo(){
        return ApiResponse.success(UserContext.getCurrentUser());
    }
}
