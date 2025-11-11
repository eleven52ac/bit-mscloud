package com.bit.user.service;

import com.bit.user.entity.UserInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2025-11-08 16:38:58
*/
public interface UserInfoService extends IService<UserInfoEntity> {

    UserInfoEntity getUserInfoByEmail(String email);

    UserInfoEntity getUserInfoByUsername(String username);
}
