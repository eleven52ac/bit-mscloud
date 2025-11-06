package com.bit.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.auth.entity.UserInfo;

/**
* @author camellia
* @description 针对表【user_info(用户信息表，存储所有用户的基本信息、账户状态、登录记录、角色权限、社交账号和其他相关信息。)】的数据库操作Service
* @createDate 2025-04-05 14:28:48
*/
public interface UserInfoService extends IService<UserInfo> {

}
