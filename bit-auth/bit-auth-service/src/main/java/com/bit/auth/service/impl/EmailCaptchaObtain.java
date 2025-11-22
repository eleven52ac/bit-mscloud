package com.bit.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.bit.auth.service.CaptchaStrategy;

import com.bit.common.core.constant.redis.RedisConstants;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.core.enums.biz.CaptchaMethodEnum;
import com.bit.common.core.enums.EmailTemplateEnum;
import com.bit.common.utils.ict.EmailSendUtils;
import com.bit.common.utils.verify.RegexUtils;
import com.bit.common.web.context.ClientMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.bit.common.core.constant.redis.RedisConstants.LOCK_TTL_30;
import static com.bit.common.core.constant.redis.RedisConstants.MAX_CAPTCHA_REQUESTS;
import static com.bit.common.core.dto.response.ApiResponse.isFail;


/**
 * @Datetime: 2025年11月09日18:52
 * @Author: Eleven52AC
 * @Description: 邮箱验证码获取实现类
 */
@Slf4j
@Service
public class EmailCaptchaObtain implements CaptchaStrategy {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmailSendUtils emailSendUtils;

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
    public CaptchaMethodEnum getCaptchaMethod() {
        return CaptchaMethodEnum.EMAIL_CAPTCHA;
    }

    @Override
    public ApiResponse<String> captcha(String email, ClientMetaInfo clientInfo) {
        // 校验邮箱
        ApiResponse<String> validation = verification(email);
        if (isFail(validation)) {
            return validation;
        }
        // 防御重复请求
        validation = preventDuplicateRequest(email);
        if (isFail(validation)) {
            return validation;
        }
        // IP 限流
        validation = ipRateLimit(clientInfo);
        if (isFail(validation)) {
            return validation;
        }
        // 发送验证码
        validation = sendCaptcha(email);
        if (isFail(validation)) {
            return ApiResponse.error("系统繁忙，请稍后再试");
        }
        return validation;
    }


    /**
     *
     * @Author: Eleven52AC
     * @Description: 校验邮箱
     * @param email
     * @return
     */
    private ApiResponse<String> verification(String email) {
        // 判空校验
        if (StringUtils.isBlank(email)){
            log.error("邮箱不能为空");
            return ApiResponse.badRequest("邮箱不能为空");
        }
        // 邮箱格式校验
        if (RegexUtils.isEmailInvalid(email)){
            log.info("邮箱格式错误");
            return ApiResponse.badRequest("邮箱格式错误");
        }
        return ApiResponse.success("邮箱格式正确");
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 防御重复请求
     * @param email
     * @return
     */
    private ApiResponse<String> preventDuplicateRequest(String email) {
        try {
            String rateLimitKey = RedisConstants.CAPTCHA_RATE_LIMIT_PREFIX + email;
            // 执行 Lua 脚本（原子操作）
            Long count = stringRedisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    Collections.singletonList(rateLimitKey),
                    String.valueOf(TimeUnit.MINUTES.toSeconds(LOCK_TTL_30))
            );

            if (count != null && count > MAX_CAPTCHA_REQUESTS) {
                log.warn("邮箱 {} 请求验证码过于频繁，{}分钟内超过 {} 次", email, LOCK_TTL_30, MAX_CAPTCHA_REQUESTS);
                return ApiResponse.badRequest("请求验证码过于频繁，请稍后再试");
            }

            // 验证码是否仍有效
            String captchaKey = RedisConstants.CAPTCHA_EMAIL_PREFIX + email;
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(captchaKey))) {
                log.info("邮箱 {} 验证码仍在有效期内", email);
                return ApiResponse.badRequest("验证码仍在有效期内，请勿重复发送");
            }

            return ApiResponse.success("验证通过，可以发送验证码");

        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接异常，跳过限流校验: {}", e.getMessage());
            return ApiResponse.success("系统繁忙，暂未校验频率，请注意操作间隔");
        }
    }


    /**
     *
     * @Author: Eleven52AC
     * @Description: IP限流
     * @param clientInfo
     * @return
     */
    private ApiResponse<String> ipRateLimit(ClientMetaInfo clientInfo) {
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
     * @Description: 发送验证码
     * @param email
     * @return
     */
    private ApiResponse<String> sendCaptcha(String email) {
        // 生成验证码
        String captcha = RandomUtil.randomNumbers(6);
        log.info("短信验证码：{}", captcha);
        String captchaKey = RedisConstants.CAPTCHA_EMAIL_PREFIX + email;
        try {
            // 存储验证码（原子操作）
            stringRedisTemplate.opsForValue().set(captchaKey, captcha, RedisConstants.LOCK_TTL_60, TimeUnit.SECONDS);
            // 异步发送邮件
            CompletableFuture.runAsync(() -> {
                String subject = EmailTemplateEnum.VERIFICATION_CODE_EMAIL_HTML.getSubject();
                String content = EmailTemplateEnum.VERIFICATION_CODE_EMAIL_HTML.set(captcha);
                boolean sendSuccess = emailSendUtils.sendHtmlEmail(subject, content, email);
                // 如果发送失败，清除验证码记录.
                if (!sendSuccess) {
                    stringRedisTemplate.delete(captchaKey);
                    log.error("邮件发送失败，已清除验证码记录");
                }
                // 异步发送邮件失败，记录日志.
            }).exceptionally(ex -> {
                log.error("异步发送邮件异常", ex);
                return null;
            });
            return ApiResponse.success("验证码发送成功");
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 服务不可用", e);
            return ApiResponse.error("系统繁忙，请稍后再试");
        }
    }


}
