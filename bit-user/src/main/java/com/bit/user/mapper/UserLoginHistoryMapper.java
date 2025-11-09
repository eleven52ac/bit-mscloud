package com.bit.user.mapper;

import com.bit.user.entity.UserLoginHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camel
* @description 针对表【user_login_history(用户登录历史表)】的数据库操作Mapper
* @createDate 2025-11-09 16:03:18
* @Entity com.bit.user.entity.UserLoginHistory
*/
@Mapper
public interface UserLoginHistoryMapper extends BaseMapper<UserLoginHistoryEntity> {

}




