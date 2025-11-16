package com.bit.cache.controller;

import com.bit.cache.dto.request.PersonRequest;
import com.bit.cache.dto.response.PersonResponse;
import com.bit.cache.service.PersonInfoService;
import com.bit.cache.service.PersonsInfoService;
import com.bit.common.core.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Datetime: 2025年04月07日22:22
 * @Author: Camellia.xiaohua/Bitspark
 * @Package: webservice.controller
 * @Project: camellia-singular
 * @Description: Redis缓存设计思路
 */
@RestController
@RequestMapping("/cache")
public class CacheController {

    private static final Logger log = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private PersonsInfoService personsInfoService;

    @Autowired
    private PersonInfoService personInfoService;

    static final ConcurrentHashMap<Integer, PersonResponse> personLocalCache = new ConcurrentHashMap<>();


    /**
     * 获取人员信息列表，mybatis plus分页
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @PostMapping("/person/list")
    public ApiResponse getPersonsInfoList(@RequestParam(name = "name", required = false) String name,
                                          @RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                          @RequestParam(name = "orderBy", required = false) String orderBy){
        List<PersonResponse> response = personsInfoService.getPersonsInfoList(name,pageNum, pageSize, orderBy);
        return ApiResponse.success(response,response.size()+"");
    }

    /**
     * 获取人员信息，无缓存。
     * @param id
     * @return
     */
    @GetMapping("/person/nocache/id")
    public ApiResponse getPersonNoCache(@RequestParam(name = "id") Long id){
        try{
            PersonResponse response = personsInfoService.getPersonWithoutCache(id);
            return ApiResponse.success(response,"获取人员信息成功");
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"获取人员信息失败");
        }
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 根据id查询 person,无redis缓存，但标为只读事务。
     * @param id
     * @return
     */
    @GetMapping("/person/readonly/id")
    public ApiResponse getPersonNoCacheButReadOnly(@RequestParam(name = "id") Long id){
        try{
            PersonResponse response = personsInfoService.getPersonWithoutCacheButReadOnly(id);
            return ApiResponse.success(response,"获取人员信息成功");
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"获取人员信息失败");
        }
    }

    /**
     * 获取人员信息，redis缓存。
     * @param id
     * @return
     */
    @GetMapping("/person/cache/id")
    public ApiResponse getPersonCache(@RequestParam(name = "id") Long id){
        try{
            PersonResponse response = personsInfoService.getPersonWithCache(id);
            return ApiResponse.success(response,"获取人员信息成功");
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"获取人员信息失败");
        }
    }

    /**
     * 获取人员信息，本地缓存。
     * @param id
     * @return
     */
    @GetMapping("/person/local/cache/id")
    public ApiResponse getPersonLocalCache(@RequestParam(name = "id") Long id){
        try{
            PersonResponse response = personsInfoService.getPersonWithLocalCache(id, personLocalCache);
            return ApiResponse.success(response,"获取人员信息成功");
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"获取人员信息失败");
        }
    }

    /**
     * 缓存 Aside 模式
     * @param request
     * @return
     */
    @PostMapping("/person/cache/aside")
    public ApiResponse cacheAsidePattern(@RequestBody PersonRequest request){
        try{
            return personsInfoService.updateBYCacheAsidePattern(request);
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"更新人员信息失败");
        }
    }

    /**
     * Redis 缓存击穿互斥锁解决方案
     * @return
     */
    @GetMapping("/person/cache/breakdown")
    public ApiResponse cacheBreakdownPattern(@RequestParam(name = "id") Long id){
        try{
            return personInfoService.cacheBreakdownPattern(id);
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"缓存 breakdown 失败");
        }
    }


    @PostMapping("/person/count")
    public ApiResponse getDataCount(@RequestParam("count") Integer count){
        log.info("当前数据量：{}", count);
        return ApiResponse.success(count);
    }

}
