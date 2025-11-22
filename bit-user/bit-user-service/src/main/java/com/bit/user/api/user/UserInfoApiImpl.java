package com.bit.user.api.user;

import bit.com.user.api.user.UserInfoApi;
import bit.com.user.api.user.dto.response.UserInfoResponse;
import bit.com.user.constant.user.UserApiConstants;
import cn.hutool.core.bean.BeanUtil;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.repository.dataobject.user.UserInfoDo;
import com.bit.user.service.user.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.bit.common.core.dto.response.ApiResponse.success;

/**
 * @Datetime: 2025年11月21日15:36
 * @Author: Eleven52AC
 * @Description: 用户信息接口实现类
 */
@RestController
@RequestMapping(UserApiConstants.API_PREFIX) // RPC 前缀
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 根据用户名查询用户信息
     * @Author: Eleven52AC
     * @param username
     */
    @Override
    public ApiResponse<UserInfoResponse> getUserInfoByUsername(String username) {
        UserInfoDo userInfoDo = userInfoService.getUserInfoByUsername(username);
        return success(BeanUtil.toBean(userInfoDo, UserInfoResponse.class));
    }

    /**
     * 根据邮箱查询用户是否存在
     * @Author: Eleven52AC
     * @param email
     */
    @Override
    public ApiResponse<Boolean> existsByEmail(String email) {
        UserInfoDo userInfoDo = userInfoService.getUserInfoByEmail(email);
        return success(userInfoDo != null);
    }

    /**
     * 根据邮箱查询用户信息
     * @Author: Eleven52AC
     * @param email
     */
    @Override
    public ApiResponse<UserInfoResponse> getUserInfoByEmail(String email) {
        UserInfoDo userInfoDo = userInfoService.getUserInfoByEmail(email);
        return success(BeanUtil.toBean(userInfoDo, UserInfoResponse.class));
    }

}
