package com.bit.user.mapper;

import com.bit.user.entity.UserInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2025-11-08 16:38:58
* @Entity com.bit.user.entity.UserInfo
*/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoEntity> {

}




