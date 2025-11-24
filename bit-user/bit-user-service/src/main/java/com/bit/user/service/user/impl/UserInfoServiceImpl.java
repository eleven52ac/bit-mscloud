package com.bit.user.service.user.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.core.IdGenerator;
import com.bit.common.utils.crypto.BCryptUtils;
import com.bit.common.utils.jwt.JwtUtils;
import com.bit.common.utils.verify.RegexUtils;
import com.bit.common.web.context.ClientMetaInfo;
import com.bit.user.controller.user.vo.request.RegisterRequestVo;
import com.bit.user.repository.dataobject.user.UserInfoDo;
import com.bit.user.service.user.UserInfoService;
import com.bit.user.repository.mysql.user.UserInfoMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.bit.common.core.constant.redis.RedisConstants.*;
import static com.bit.common.core.constant.redis.RedisConstants.CAPTCHA_EMAIL_PREFIX;
import static com.bit.common.core.constant.redis.RedisConstants.CAPTCHA_ERROR_ATTEMPTS_PREFIX;
import static com.bit.common.core.constant.redis.RedisConstants.CAPTCHA_IP_RATE_LIMIT_PREFIX;
import static com.bit.common.core.constant.redis.RedisConstants.LOCK_TTL_30;
import static com.bit.common.core.constant.redis.RedisConstants.MAX_CAPTCHA_REQUESTS;
import static com.bit.common.core.constant.redis.RedisConstants.MAX_REGISTER_REQUESTS;
import static com.bit.common.core.constant.redis.RedisConstants.USER_TOKEN_PREFIX;
import static com.bit.common.core.dto.response.ApiResponse.*;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2025-11-08 16:38:58
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoDo>
    implements UserInfoService{

    Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfoDo getUserInfoByEmail(String email) {
        LambdaQueryWrapper<UserInfoDo> queryWrapper = new LambdaQueryWrapper<UserInfoDo>()
                .eq(UserInfoDo::getEmail, email)
                .eq(UserInfoDo::getIsDeleted, "0");
        return userInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public UserInfoDo getUserInfoByUsername(String username) {
        LambdaQueryWrapper<UserInfoDo> queryWrapper = new LambdaQueryWrapper<UserInfoDo>()
                .eq(UserInfoDo::getUsername, username)
                .eq(UserInfoDo::getIsDeleted, "0");
        return userInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public ApiResponse<String> createUser(UserInfoDo userInfo) {
        boolean result = this.save(userInfo);
        return result ? success("创建用户成功") : error("创建用户失败");
    }
}




