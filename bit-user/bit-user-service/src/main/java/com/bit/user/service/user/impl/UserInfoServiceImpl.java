package com.bit.user.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.user.repository.dataobject.user.UserInfoDo;
import com.bit.user.service.user.UserInfoService;
import com.bit.user.repository.mysql.user.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2025-11-08 16:38:58
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoDo>
    implements UserInfoService{

    @Override
    public UserInfoDo getUserInfoByEmail(String email) {
        LambdaQueryWrapper<UserInfoDo> queryWrapper = new LambdaQueryWrapper<UserInfoDo>()
                .eq(UserInfoDo::getEmail, email)
                .eq(UserInfoDo::getIsDeleted, false);
        return this.getOne(queryWrapper);
    }

    @Override
    public UserInfoDo getUserInfoByUsername(String username) {
        LambdaQueryWrapper<UserInfoDo> queryWrapper = new LambdaQueryWrapper<UserInfoDo>()
                .eq(UserInfoDo::getUsername, username)
                .eq(UserInfoDo::getIsDeleted, false);
                return this.getOne(queryWrapper);
    }
}




