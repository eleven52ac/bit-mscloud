package com.bit.cache.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.cache.dto.request.PersonRequest;
import com.bit.cache.dto.response.PersonResponse;
import com.bit.cache.entity.PersonsInfoEntity;
import com.bit.common.core.dto.response.ApiResponse;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author camel
* @description 针对表【prh_protected_persons_info(保障人员信息)】的数据库操作Service
* @createDate 2025-04-08 13:50:52
*/
@DS("mysql")
public interface PersonsInfoService extends IService<PersonsInfoEntity> {


    List<PersonResponse> getPersonsInfoList(String name, Integer pageNum, Integer pageSize, String orderBy);

    PersonResponse getPersonWithoutCache(Long id);

    PersonResponse getPersonWithCache(Long id);

    PersonResponse getPersonWithLocalCache(Long id, ConcurrentHashMap<Integer, PersonResponse> personLocalCache);

    ApiResponse updateBYCacheAsidePattern(PersonRequest request);

    long getDataCount();

    PersonResponse getPersonWithoutCacheButReadOnly(Long id);
}
