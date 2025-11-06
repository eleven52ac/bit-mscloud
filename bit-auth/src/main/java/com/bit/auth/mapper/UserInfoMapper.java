package com.bit.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.bit.auth.entity.UserInfo;

/**
* @author camellia
* @description 针对表【user_info(用户信息表，存储所有用户的基本信息、账户状态、登录记录、角色权限、社交账号和其他相关信息。)】的数据库操作Mapper
* @createDate 2025-04-05 14:28:48
* @Entity generator.domain.UserInfo
*/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}




