package com.bit.user.service;

import com.bit.user.entity.UserLoginHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author camel
* @description 针对表【user_login_history(用户登录历史表)】的数据库操作Service
* @createDate 2025-11-09 16:03:18
*/
public interface UserLoginHistoryService extends IService<UserLoginHistoryEntity> {

    List<UserLoginHistoryEntity> queryRecentLoginData(Long userId);
}
