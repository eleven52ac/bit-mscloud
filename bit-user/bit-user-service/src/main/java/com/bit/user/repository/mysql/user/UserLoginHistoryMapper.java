package com.bit.user.repository.mysql.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.user.repository.dataobject.user.UserLoginHistoryDo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camel
* @description 针对表【user_login_history(用户登录历史表)】的数据库操作Mapper
* @createDate 2025-11-09 16:03:18
* @Entity com.bit.user.entity.UserLoginHistory
*/
@Mapper
public interface UserLoginHistoryMapper extends BaseMapper<UserLoginHistoryDo> {

}
