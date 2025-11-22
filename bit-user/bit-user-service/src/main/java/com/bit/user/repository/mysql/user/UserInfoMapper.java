package com.bit.user.repository.mysql.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.user.repository.dataobject.user.UserInfoDo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camel
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2025-11-08 16:38:58
* @Entity com.bit.user.entity.UserInfo
*/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoDo> {

}




