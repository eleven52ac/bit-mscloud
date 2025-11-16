package com.bit.auth.service.impl;

import com.bit.auth.service.CaptchaService;
import com.bit.auth.service.UserInfoService;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.crypto.BCryptUtils;
import com.bit.common.utils.ict.EmailSendUtils;
import com.bit.common.utils.verify.RegexUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.bit.auth.entity.UserInfo;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.bit.common.core.constant.redis.RedisConstants.*;
import static com.bit.common.core.enums.EmailTemplateEnum.VERIFICATION_CODE_EMAIL_HTML;

/**
 * @Datetime: 2025年04月02日10:35
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.service.impl
 * @Project: camellia
 * @Description:
 */
@Slf4j
@Service("emailCaptchaServiceImpl")
public class EmailCaptchaServiceImpl implements CaptchaService {

    @Autowired
    private EmailSendUtils emailSendUtils;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送邮箱验证码
     *
     * @param email   验证码
     * @param session
     * @return
     */
    @Override
    public Boolean sendCaptcha(String email, HttpSession session) {
        // 3. 校验邮箱是否合法
        if (RegexUtils.isEmailInvalid(email)){
            log.info("邮箱格式错误");
            return false;
        }

        // 4. 频率限制校验
        String rateLimitKey = CAPTCHA_RATE_LIMIT_PREFIX + email;
        Long requestCount = stringRedisTemplate.opsForValue().increment(rateLimitKey);
        stringRedisTemplate.expire(rateLimitKey, 1, TimeUnit.HOURS);
        if (requestCount != null && requestCount > 10) {
            log.warn("邮箱 {} 请求验证码过于频繁", email);
            return false;
        }

        // 5. 校验是否短时间内重复请求验证码,,使用 exists 命令直接判断 key 是否存在.
        String captchaKey = CAPTCHA_EMAIL_PREFIX + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(captchaKey))) {
            log.info("邮箱 {} 验证码仍在有效期内", email);
            return false;
        }

        // 6. 生成验证码
        String captcha = RandomUtil.randomNumbers(6);
        log.info("短信验证码：{}", captcha);

        try {
            // 7. 存储验证码（原子操作）
            stringRedisTemplate.opsForValue().set(captchaKey, captcha, CAPTCHA_EMAIL_TTL, TimeUnit.MINUTES);

            // 8. 异步发送邮件
            CompletableFuture.runAsync(() -> {
                String subject = VERIFICATION_CODE_EMAIL_HTML.getSubject();
                String content = VERIFICATION_CODE_EMAIL_HTML.set(captcha);
                boolean sendSuccess = emailSendUtils.sendHtmlEmail(subject, content, email);
                // 9. 如果发送失败，清除验证码记录.
                if (!sendSuccess) {
                    stringRedisTemplate.delete(captchaKey);
                    log.error("邮件发送失败，已清除验证码记录");
                }
                // 10. 异步发送邮件失败，记录日志.
            }).exceptionally(ex -> {
                log.error("异步发送邮件异常", ex);
                return null;
            });
            return true;
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 服务不可用", e);
            return false;
        }
    }

    @Override
    public ApiResponse<String> loginOrRegister(String email, String password, String captcha, HttpSession session) {
        // 3. 校验邮箱是否合法
        if (RegexUtils.isEmailInvalid(email)){
            return ApiResponse.error("邮箱格式错误");
        }
        // 4. 从redis中获取验证码,并判断非空且验证码正确.
        String rightCaptcha = stringRedisTemplate.opsForValue().get(CAPTCHA_EMAIL_PREFIX + email);
        if (rightCaptcha == null){
            return ApiResponse.error("验证码已过期");
        }
        if (!captcha.equals(rightCaptcha)){
            return ApiResponse.error("验证码错误");
        }
        // 5. 检查用户是否存在。
        QueryWrapper <UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        // 6. 用户不存在直接新建
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUsername(RandomUtil.randomString(10));
            userInfo.setEmail(email);
            userInfo.setPassword(BCryptUtils.encode(password));
            userInfo.setLoginCount(1);
            userInfo.setCreatedAt(new Date());
            userInfoService.save(userInfo);
        } else{
            // 7. 在密码校验前添加防暴力破解
            String attemptKey = LOGIN_ATTEMPT_PREFIX + email;
            Long attempts = stringRedisTemplate.opsForValue().increment(attemptKey);
            if (attempts > 5) {
                stringRedisTemplate.expire(attemptKey, 1, TimeUnit.HOURS);
                return ApiResponse.error("密码错误次数过多，账户已锁定，请1小时后重试");
            }
            // 8. 用户存在校验密码
            boolean matchesResult = BCryptUtils.matches(password, userInfo.getPassword());
            if (!matchesResult){
                return ApiResponse.error("密码错误");
            }
            // 9. 登录成功后重置计数器
            stringRedisTemplate.delete(attemptKey);
        }
        // 10. 登录成功，将用户信息存入 redis 中.
        String token =  JWT.create().setPayload("userKey",email).setSigner(JWTSignerUtil.none()).sign();
        stringRedisTemplate.opsForValue().set(USER_INFO_PREFIX+token, JSONUtil.toJsonStr(userInfo), Duration.ofHours(1));
        stringRedisTemplate.delete(CAPTCHA_EMAIL_PREFIX + email);
        return ApiResponse.success(token,"登录成功");
    }

}
