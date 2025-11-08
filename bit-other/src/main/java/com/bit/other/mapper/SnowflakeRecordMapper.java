package com.bit.other.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bit.other.entity.SnowflakeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
* @author camel
* @description 针对表【snowflake_record(Snowflake ID 生成日志表)】的数据库操作Mapper
* @createDate 2025-05-19 21:10:57
* @Entity com.bit.other.entity.SnowflakeRecord
*/
@Mapper
public interface SnowflakeRecordMapper extends BaseMapper<SnowflakeRecord> {

}




