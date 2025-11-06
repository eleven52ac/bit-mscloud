package com.bit.cache.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.bit.cache.entity.PersonInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import common.dto.response.ApiResponse;

/**
* @author camellia
* @description 针对表【person_info(人员信息表)】的数据库操作Service
* @createDate 2025-04-12 23:53:25
*/
@DS("mysql2")
public interface PersonInfoService extends IService<PersonInfoEntity> {

    ApiResponse cacheBreakdownPattern(Long id);
}
