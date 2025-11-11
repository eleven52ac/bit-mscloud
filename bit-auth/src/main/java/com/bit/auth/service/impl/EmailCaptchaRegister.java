package com.bit.auth.service.impl;

import cn.hutool.json.JSONUtil;
import com.bit.auth.dto.request.TokenRequest;
import com.bit.auth.service.RegisterStrategy;
import com.bit.user.api.model.UserInfoEntity;
import com.bit.user.api.service.UserInfoFeignClient;
import common.constant.RedisConstants;
import common.dto.response.ApiResponse;
import common.dto.reuqest.ClientMetaInfo;
import common.enums.RegisterTypeEnum;
import common.utils.BCryptUtil;
import common.utils.RegexUtils;
import common.utils.core.IdGenerator;
import common.utils.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static common.constant.RedisConstants.*;
import static common.dto.response.ApiResponse.isFail;

/**
 * @Datetime: 2025年11月09日17:25
 * @Author: Eleven52AC
 * @Description: 邮箱验证码注册服务实现类
 */
@Slf4j
@Service
public class EmailCaptchaRegister implements RegisterStrategy {

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT;

    static {
        RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();
        RATE_LIMIT_SCRIPT.setResultType(Long.class);
        RATE_LIMIT_SCRIPT.setScriptText(
                "local current = redis.call('INCR', KEYS[1]) " +
                        "if tonumber(current) == 1 then " +
                        "  redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
                        "end " +
                        "return current"
        );
    }



    @Override
    public RegisterTypeEnum getRegisterType() {
        return RegisterTypeEnum.EMAIL_CAPTCHA;
    }

    @Override
    public ApiResponse<String> register(TokenRequest request, ClientMetaInfo info) {
        // 参数校验
        ApiResponse<String> validation = validateParams(request);
        if (isFail(validation)){
            return validation;
        }
        // 标准化输入
        normalizedInput(request);
        // 邮箱限流
        validation = mailboxRateLimiting(request.getEmail());
        if (isFail(validation)){
            return validation;
        }
        // IP限流
        validation = ipRateLimiting(info);
        if (isFail(validation)){
            return validation;
        }
        // 防重注册、业务层预检查。
        validation = validateDuplicateRegister(request);
        if (isFail(validation)){
            return validation;
        }
        // 验证码校验
        validation = validateCaptcha(request.getEmail(), request.getCaptcha());
        if (isFail(validation)){
            return validation;
        }
        // 创建用户
        validation = createUser(request);
        if (isFail(validation)){
            return validation;
        }
        // 返回结果
        return ApiResponse.success("注册成功");
    }


    /**
     *
     * @Author: Eleven52AC
     * @Description: 标准化输入
     * @param request
     */
    private void normalizedInput(TokenRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase());
    }


    /**
     *
     * @Author: Eleven52AC
     * @Description: 参数校验
     * @param request
     * @return
     */
    private ApiResponse<String> validateParams(TokenRequest request) {
        if (StringUtils.isBlank(request.getEmail())){
            return ApiResponse.badRequest("邮箱不能为空");
        }
        if (StringUtils.isBlank(request.getPassword())){
            return ApiResponse.badRequest("密码不能为空");
        }
        if (StringUtils.isBlank(request.getUsername())){
            return ApiResponse.badRequest("用户名不能为空");
        }
        if (StringUtils.isBlank(request.getCaptcha())){
            return ApiResponse.badRequest("验证码不能为空");
        }
        if (RegexUtils.isEmailInvalid(request.getEmail())) {
            return ApiResponse.badRequest("邮箱格式不正确");
        }
        if (!RegexUtils.isPasswordStrong(request.getPassword())) {
            return ApiResponse.badRequest("密码强度不足，请包含大小写字母和数字");
        }
        return ApiResponse.success("参数验证通过");
    }


    /**
     * @Author: Eleven52AC
     * @Description: 邮箱限流
     * @param email
     * @return
     */
    private ApiResponse<String> mailboxRateLimiting(String email) {
        // 标准化输入
        String normalizedEmail = email.trim().toLowerCase();
        String rateLimitKey = REGISTER_RATE_LIMIT_PREFIX + normalizedEmail;
        try {
            // 执行 Lua 脚本（原子操作）
            Long count = stringRedisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    Collections.singletonList(rateLimitKey),
                    String.valueOf(TimeUnit.MINUTES.toSeconds(LOCK_TTL_30))
            );

            if (count == null) {
                log.warn("邮箱 {} 限流计数结果为空", normalizedEmail);
                return ApiResponse.error("系统繁忙，请稍后再试。");
            }

            if (count > MAX_REGISTER_REQUESTS) {
                log.warn("邮箱 {} 注册过于频繁，{}分钟内超过 {} 次", normalizedEmail, LOCK_TTL_30, MAX_REGISTER_REQUESTS);
                return ApiResponse.badRequest("请求注册过于频繁，请稍后再试。");
            }

            return ApiResponse.success("邮箱限流检查通过");

        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接异常，跳过限流校验: {}", e.getMessage());
            return ApiResponse.error("系统繁忙，请稍后再试。");
        }
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: IP限流
     * @param clientInfo
     * @return
     */
    private ApiResponse<String> ipRateLimiting(ClientMetaInfo clientInfo) {
        if (clientInfo == null || StringUtils.isBlank(clientInfo.getIp())) {
            log.warn("客户端IP信息缺失，跳过IP限流");
            return ApiResponse.success("IP信息缺失，跳过限流");
        }
        String ip = clientInfo.getIp();
        String ipRateLimitKey = RedisConstants.CAPTCHA_IP_RATE_LIMIT_PREFIX + ip;
        try {
            Long count = stringRedisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    Collections.singletonList(ipRateLimitKey),
                    String.valueOf(TimeUnit.MINUTES.toSeconds(LOCK_TTL_30))
            );

            if (count != null && count > MAX_CAPTCHA_REQUESTS) {
                log.warn("IP {} 请求验证码过于频繁，{}分钟内超过 {} 次", ip, LOCK_TTL_30, MAX_CAPTCHA_REQUESTS);
                return ApiResponse.badRequest("操作过于频繁，请稍后再试");
            }

            return ApiResponse.success("IP限流检查通过");

        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接异常，跳过IP限流: {}", e.getMessage());
            // 降级：允许请求，但记录风险
            return ApiResponse.success("系统繁忙，暂未校验IP频率");
        }
    }


    /**
     *
     * @Author: Eleven52AC
     * @Description: 防重注册、业务层预检查。
     * @param request
     * @return
     */
    private ApiResponse<String> validateDuplicateRegister(TokenRequest request) {
        // 标准化输入（理论上可以区分大小写，但几乎所有主流邮箱服务商（Gmail、Outlook、QQ、163 等）都将其视为不区分大小写。）
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        String normalizedUsername = Optional.ofNullable(request.getUsername()).map(String::trim).orElse(null);
        try {
            // 查询用户信息
            UserInfoEntity userInfo = userInfoFeignClient.getUserInfoByEmail(normalizedEmail);
            if (ObjectUtils.isNotEmpty(userInfo)) {
                String existingUsername = userInfo.getUsername();
                // 重复提交注册（邮箱 & 用户名一致）
                if (existingUsername != null && existingUsername.equalsIgnoreCase(normalizedUsername)) {
                    log.info("用户 {} 重复注册尝试", normalizedEmail);
                    return ApiResponse.badRequest("该邮箱已注册，请勿重复提交。");
                } else {
                    log.warn("邮箱 {} 已存在（不同用户），可能存在撞号注册行为", normalizedEmail);
                    return ApiResponse.success(null,"该邮箱已被用于注册。");
                }
            }
            log.info("邮箱 {} 未注册，正常注册流程", normalizedEmail);
            return ApiResponse.success(null,"验证码已发送至您的邮箱，请查收。");

        } catch (Exception e) {
            log.error("查询用户信息异常，email={}", normalizedEmail, e);
            // 安全降级，不暴露异常信息。
            return ApiResponse.error("系统繁忙，请稍后再试。");
        }
    }


    /**
     *
     * @Author: Eleven52AC
     * @Description: 验证验证码
     * @param email
     * @param captcha
     * @return
     */
    private ApiResponse<String> validateCaptcha(String email, String captcha) {
        String key = RedisConstants.CAPTCHA_EMAIL_PREFIX + email;
        try{
            String rightCaptcha = stringRedisTemplate.opsForValue().get(key);
            if (ObjectUtils.isEmpty(rightCaptcha)){
                return ApiResponse.error("验证码已过期");
            }
            // 比较验证码（不区分大小写）
            if (!captcha.equalsIgnoreCase(rightCaptcha)) {
                // 累计错误次数
                String errorKey = RedisConstants.CAPTCHA_ERROR_ATTEMPTS_PREFIX + email;
                Long errorCount = stringRedisTemplate.opsForValue().increment(errorKey);
                if (errorCount != null && errorCount.equals(1L)) {
                    stringRedisTemplate.expire(errorKey, 10, TimeUnit.MINUTES); // 错误计数10分钟过期
                }
                if (errorCount >= 3L) {
                    stringRedisTemplate.delete(key);
                    stringRedisTemplate.delete(RedisConstants.CAPTCHA_ERROR_ATTEMPTS_PREFIX + email);
                    return ApiResponse.badRequest("验证码错误次数过多，请重新获取验证码。");
                }
                return ApiResponse.error("验证码错误");
            }
            // 验证成功，删除验证码。
            stringRedisTemplate.delete(key);
            // 同时删除错误计数
            stringRedisTemplate.delete(RedisConstants.CAPTCHA_ERROR_ATTEMPTS_PREFIX + email);
            return ApiResponse.success("验证码验证成功");
        }catch (RedisConnectionFailureException e) {
            log.error("Redis连接异常，验证码校验失败: {}", e.getMessage());
            return ApiResponse.error("系统繁忙，请稍后再试");
        }
    }

    /**
     *
     * @param request
     * @param userId
     * @return
     * @Author: Eleven52AC
     * @Description: 创建用户
     */
    private ApiResponse<String> createUser(TokenRequest request) {
        try {
            Long userId = IdGenerator.nextId();
            LocalDateTime now = LocalDateTime.now();
            UserInfoEntity userInfo = new UserInfoEntity()
                    .setUserId(userId)
                    .setUsername(request.getUsername())
                    .setPassword(BCryptUtil.encode(request.getPassword()))
                    .setEmail(request.getEmail())
                    .setCreatedAt(now)
                    .setLastLogin(now)
                    .setLoginCount(1);
            // 远程调用用户中心创建
            ApiResponse<String> response = userInfoFeignClient.createUser(userInfo);
            if (isFail(response)) {
                log.error("创建用户失败: {}", response.getMessage());
                return ApiResponse.error("系统繁忙，请稍后再试");
            }

            // 生成 JWT Token
            Map<String, String> claims = Map.of(
                    "userId", userId.toString(),
                    "email", request.getEmail()
            );
            String token = JwtUtil.generateToken(claims);

            // 存入 Redis（按用户ID维度，方便踢人/注销）
            String redisKey = USER_TOKEN_PREFIX + userId;
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    JSONUtil.toJsonStr(Map.of(
                            "token", token,
                            "email", request.getEmail(),
                            "createdAt", now.toString()
                    )),
                    Duration.ofHours(1)
            );

            log.info("用户注册成功: userId={}, email={}", userId, request.getEmail());
            return ApiResponse.success(token, "注册成功，已自动登录");

        } catch (Exception e) {
            log.error("注册创建用户异常: {}", e.getMessage(), e);
            return ApiResponse.error("注册失败，请稍后再试");
        }
    }

}
