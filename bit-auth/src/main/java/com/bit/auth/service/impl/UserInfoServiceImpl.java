package com.bit.auth.service.impl;

import com.bit.auth.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.auth.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;
import com.bit.auth.entity.UserInfo;

/**
* @author camellia
* @description 针对表【user_info(用户信息表，存储所有用户的基本信息、账户状态、登录记录、角色权限、社交账号和其他相关信息。)】的数据库操作Service实现
* @createDate 2025-04-05 14:28:48
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService {

}




