package com.bit.other.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.other.entity.SnowflakeRecord;
import com.bit.other.mapper.SnowflakeRecordMapper;
import com.bit.other.service.SnowflakeRecordService;
import org.springframework.stereotype.Service;

/**
* @author camel
* @description 针对表【snowflake_record(Snowflake ID 生成日志表)】的数据库操作Service实现
* @createDate 2025-05-19 21:10:57
*/
@Service
public class SnowflakeRecordServiceImpl extends ServiceImpl<SnowflakeRecordMapper, SnowflakeRecord>
    implements SnowflakeRecordService {

}




