package com.bit.job.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.job.entity.IdPrefixRegion;

/**
* @author camellia
* @description 针对表【ID_PREFIX_REGION】的数据库操作Service
* @createDate 2025-04-12 16:07:02
*/
@DS("oracle")
public interface IdPrefixRegionService extends IService<IdPrefixRegion> {

}
