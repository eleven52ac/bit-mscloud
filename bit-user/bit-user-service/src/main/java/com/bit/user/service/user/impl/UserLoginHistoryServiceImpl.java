package com.bit.user.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.user.repository.dataobject.user.UserLoginHistoryDo;
import com.bit.user.service.user.UserLoginHistoryService;
import com.bit.user.repository.mysql.user.UserLoginHistoryMapper;
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
public class UserLoginHistoryServiceImpl extends ServiceImpl<UserLoginHistoryMapper, UserLoginHistoryDo>
    implements UserLoginHistoryService{

    @Autowired
    private UserLoginHistoryMapper userLoginHistoryMapper;

    @Override
    public List<UserLoginHistoryDo> queryRecentLoginData(Long userId) {
        LambdaQueryWrapper<UserLoginHistoryDo> queryWrapper = new LambdaQueryWrapper<UserLoginHistoryDo>()
                .eq(UserLoginHistoryDo::getUserId, userId)
                .orderByDesc(UserLoginHistoryDo::getLoginTime)
                .last("limit 5");
        List<UserLoginHistoryDo> list = this.list(queryWrapper);
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public Page<UserLoginHistoryDo> getUserLoginHistory(Page<UserLoginHistoryDo> page, Long userId) {
        LambdaQueryWrapper<UserLoginHistoryDo> queryWrapper = new LambdaQueryWrapper<UserLoginHistoryDo>()
                .eq(UserLoginHistoryDo::getUserId, userId)
                .orderByDesc(UserLoginHistoryDo::getLoginTime);
        return userLoginHistoryMapper.selectPage(page, queryWrapper);
    }

}




