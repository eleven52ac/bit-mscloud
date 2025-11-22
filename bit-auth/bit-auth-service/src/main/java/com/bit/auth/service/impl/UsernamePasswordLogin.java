package com.bit.auth.service.impl;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.bit.auth.controller.auth.vo.request.TokenRequestVo;
import com.bit.auth.event.UserLoginEvent;
import com.bit.auth.service.LoginStrategy;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.biz.LoginTypeEnum;
import com.bit.common.utils.crypto.BCryptUtils;
import com.bit.common.web.context.ClientMetaInfo;
import com.bit.user.api.model.UserInfoEntity;
import com.bit.user.api.service.UserInfoFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.bit.common.core.constant.redis.RedisConstants.LOGIN_ATTEMPT_PREFIX;
import static com.bit.common.core.constant.redis.RedisConstants.USER_INFO_PREFIX;
import static com.bit.common.core.dto.response.ApiResponse.isFail;

/**
 * @Datetime: 2025年11月08日14:35
 * @Author: Eleven52AC
 * @Description: 用户名密码登录服务实现类
 */
@Service
@Slf4j
public class UsernamePasswordLogin implements LoginStrategy {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     *
     * @Author: Eleven52AC
     * @Description: 获取登录类型
     * @return 登录类型
     */
    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.USERNAME_PASSWORD;
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 登入
     * @param request
     * @return
     */
    @Override
    public ApiResponse<String> login(TokenRequestVo request, ClientMetaInfo info) {
        // 参数格式校验
        ApiResponse<String> validation = verification(request);
        if (isFail(validation)) {
            return validation;
        }
        // 用户名 密码
        String username = StringUtils.trimToEmpty(request.getUsername());
        String password = StringUtils.trimToEmpty(request.getPassword());
        // 防暴力破解
        validation = antiBruteForceCracking(username,  password);
        if (isFail(validation)) {
            return validation;
        }
        // 登入密码校验
        UserInfoEntity userInfo = userInfoFeignClient.getUserInfoByUsername(username);
        validation = validatePassword(userInfo, password);
        if (isFail(validation)) {
            return validation;
        }
        // 登录成功后重置计数器
        stringRedisTemplate.delete(LOGIN_ATTEMPT_PREFIX + username);
        // 登录成功，将用户信息存入 redis 中。
        String token =  JWT.create().setPayload("userKey",username).setSigner(JWTSignerUtil.none()).sign();
        stringRedisTemplate.opsForValue().set(USER_INFO_PREFIX + token, JSONUtil.toJsonStr(userInfo), Duration.ofHours(1));
        // 开启另一个线程、取检查是否新地址或者新设备，如果是就推动消息。
        eventPublisher.publishEvent(new UserLoginEvent(userInfo, info));
        return ApiResponse.success(token, "登录成功");
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 登入密码校验
     * @param userInfo
     * @param password
     * @return
     */
    private ApiResponse<String> validatePassword(UserInfoEntity userInfo, String password) {
        if (userInfo == null){
            log.info("用户不存在");
            return ApiResponse.notFound("用户不存在，是否使用创建新用户？");
        }
        if (StringUtils.isBlank(userInfo.getPassword())){
            log.info("用户密码为空");
            return ApiResponse.error("用户密码为空");
        }
        boolean matchesResult = BCryptUtils.matches(password, userInfo.getPassword());
        if (!matchesResult){
            log.info("密码错误");
            return ApiResponse.error("密码错误");
        }
        return ApiResponse.success("密码校验通过");
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 防暴力破解
     * @param username
     * @param password
     * @return
     */
    private ApiResponse<String> antiBruteForceCracking(String username, String password) {
        String attemptKey = LOGIN_ATTEMPT_PREFIX + username;
        Long attempts = stringRedisTemplate.opsForValue().increment(attemptKey);
        if (attempts > 5) {
            stringRedisTemplate.expire(attemptKey, 1, TimeUnit.HOURS);
            log.error("密码错误次数过多，账户已锁定。");
            return ApiResponse.error("密码错误次数过多，账户已锁定，请1小时后重试");
        }
        log.info("防暴力破解检测通过");
        return ApiResponse.success("防暴力破解检测通过");
    }

    /**
     * 验证用户名密码
     * @Author: Eleven52AC
     * @Description:
     * @param request
     * @return
     */
    private ApiResponse<String> verification(TokenRequestVo request) {
        if (request == null) {
            log.error("请求参数不能为空");
            return ApiResponse.badRequest("请求参数不能为空");
        }
        String username = StringUtils.trimToEmpty(request.getUsername());
        String password = StringUtils.trimToEmpty(request.getPassword());
        // 判空校验
        if (StringUtils.isBlank(username)) {
            log.error("用户名不能为空");
            return ApiResponse.badRequest("用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            log.error("密码不能为空");
            return ApiResponse.badRequest("密码不能为空");
        }
        // 用户名长度校验
        if (username.length() < 3 || username.length() > 32) {
            log.error("用户名长度应在3到32个字符之间");
            return ApiResponse.badRequest("用户名长度应在3到32个字符之间");
        }
        // 用户名格式校验（防止SQL注入/XSS/非法字符）
        if (!username.matches("^[a-zA-Z0-9_\\-@.]+$")) {
            log.error("用户名格式非法");
            return ApiResponse.badRequest("用户名格式非法");
        }
        // 密码长度校验（仅防止溢出/攻击，不强制复杂度）
        if (password.length() > 128) {
            log.error("密码过长");
            return ApiResponse.badRequest("密码过长");
        }
        // 防止明显注入攻击
        String lower = username.toLowerCase();
        if (lower.contains("select ") || lower.contains("insert ") || lower.contains("update ") || lower.contains("delete ")) {
            log.error("用户名格式非法");
            return ApiResponse.badRequest("用户名格式非法");
        }
        log.info("用户名密码格式校验通过");
        return ApiResponse.success("验证通过");
    }

}
