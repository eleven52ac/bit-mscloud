package com.bit.user.service.user;

import    com.baomidou.mybatisplus.extension.service.IService;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.user.controller.user.vo.request.RegisterRequestVo;
import com.bit.user.repository.dataobject.user.UserInfoDo;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2025-11-08 16:38:58
*/
public interface UserInfoService extends IService<UserInfoDo> {

    /**
     * 根据邮箱查询用户信息
     * @Author: Eleven52AC
     * @param email
     * @return
     */
    UserInfoDo getUserInfoByEmail(String email);

    /**
     * 根据用户名查询用户信息
     * @Author: Eleven52AC
     * @param username
     * @return
     */
    UserInfoDo getUserInfoByUsername(String username);

    /**
     * 创建用户
     * @Author: Eleven52AC
     * @param userInfo
     * @return
     */
    ApiResponse<String> createUser(UserInfoDo userInfo);
}
