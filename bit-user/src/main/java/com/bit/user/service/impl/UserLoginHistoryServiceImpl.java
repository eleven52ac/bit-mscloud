package com.bit.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.user.entity.UserLoginHistoryEntity;
import com.bit.user.service.UserLoginHistoryService;
import com.bit.user.mapper.UserLoginHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
* @author camel
* @description 针对表【user_login_history(用户登录历史表)】的数据库操作Service实现
* @createDate 2025-11-09 16:03:18
*/
@Service
public class UserLoginHistoryServiceImpl extends ServiceImpl<UserLoginHistoryMapper, UserLoginHistoryEntity>
    implements UserLoginHistoryService{

    @Autowired
    private UserLoginHistoryMapper userLoginHistoryMapper;

    @Override
    public List<UserLoginHistoryEntity> queryRecentLoginData(Long userId) {
        LambdaQueryWrapper<UserLoginHistoryEntity> queryWrapper = new LambdaQueryWrapper<UserLoginHistoryEntity>()
                .eq(UserLoginHistoryEntity::getUserId, userId)
                .orderByDesc(UserLoginHistoryEntity::getLoginTime)
                .last("limit 5");
        List<UserLoginHistoryEntity> list = this.list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

}




