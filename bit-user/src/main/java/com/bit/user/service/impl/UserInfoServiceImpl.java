package com.bit.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.user.entity.UserInfoEntity;
import com.bit.user.service.UserInfoService;
import com.bit.user.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2025-11-08 16:38:58
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity>
    implements UserInfoService{

}




