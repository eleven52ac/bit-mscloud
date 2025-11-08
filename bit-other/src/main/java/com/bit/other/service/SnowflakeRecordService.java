package com.bit.other.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.other.entity.SnowflakeRecord;

/**
* @author camel
* @description 针对表【snowflake_record(Snowflake ID 生成日志表)】的数据库操作Service
* @createDate 2025-05-19 21:10:57
*/
@DS("mysql2")
public interface SnowflakeRecordService extends IService<SnowflakeRecord> {

}
