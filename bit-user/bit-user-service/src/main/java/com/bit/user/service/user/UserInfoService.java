package com.bit.user.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.user.repository.dataobject.user.UserInfoDo;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2025-11-08 16:38:58
*/
public interface UserInfoService extends IService<UserInfoDo> {

    UserInfoDo getUserInfoByEmail(String email);

    UserInfoDo getUserInfoByUsername(String username);
}
