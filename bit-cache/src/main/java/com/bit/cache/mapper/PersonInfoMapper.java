package com.bit.cache.mapper;

import com.bit.cache.entity.PersonInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
* @author camellia
* @description 针对表【person_info(人员信息表)】的数据库操作Mapper
* @createDate 2025-04-12 23:53:25
* @Entity com.bit.cache.entity.PersonInfo
*/
@Mapper
public interface PersonInfoMapper extends BaseMapper<PersonInfoEntity> {

}




