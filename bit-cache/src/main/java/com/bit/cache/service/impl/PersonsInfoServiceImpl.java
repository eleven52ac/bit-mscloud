package com.bit.cache.service.impl;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.cache.dto.request.PersonRequest;
import com.bit.cache.dto.response.PersonResponse;
import com.bit.cache.entity.PersonInfoEntity;
import com.bit.cache.entity.PersonsInfoEntity;
import com.bit.cache.mapper.PersonInfoMapper;
import com.bit.cache.mapper.PersonsInfoMapper;
import com.bit.cache.service.PersonsInfoService;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.identity.IdCardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bit.common.core.constant.redis.RedisConstants.PERSON_INFO_PREFIX;

/**
* @author camel
* @description 针对表【prh_protected_persons_info(保障人员信息)】的数据库操作Service实现
* @createDate 2025-04-08 13:50:52
*/
@Service
public class PersonsInfoServiceImpl extends ServiceImpl<PersonsInfoMapper, PersonsInfoEntity>
    implements PersonsInfoService {

    @Autowired
    private PersonsInfoMapper personsInfoMapper;

    @Autowired
    private PersonInfoMapper personInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<PersonResponse> getPersonsInfoList(String name, Integer pageNum, Integer pageSize, String orderBy) {
        Page<PersonsInfoEntity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PersonsInfoEntity> queryWrapper = new QueryWrapper<>();
        if (orderBy != null && !orderBy.isEmpty()) {
            queryWrapper.orderBy(true, true, orderBy);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("person_name", name);
        }
        Page<PersonsInfoEntity> personsInfoPage = personsInfoMapper.selectPage(page, queryWrapper);
        List<PersonResponse> response = desensitization(personsInfoPage.getRecords());
        return response;
    }

    /**
     * 根据id查询 person,无redis缓存。
     * @param id
     * @return
     */
    @Override
    public PersonResponse getPersonWithoutCache(Long id) {
        PersonsInfoEntity entity = personsInfoMapper.selectById(id);
        return desensitization(entity);
    }

    /**
     * 根据id查询 person,无redis缓存，但标为只读事务。
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PersonResponse getPersonWithoutCacheButReadOnly(Long id) {
        PersonsInfoEntity entity = personsInfoMapper.selectById(id);
        return desensitization(entity);
    }

    /**
     * 根据id查询 person,有redis缓存。
     * @param id
     * @return
     */
    @Override
    public PersonResponse getPersonWithCache(Long id) {
        String key = PERSON_INFO_PREFIX + id;
        String personJson = stringRedisTemplate.opsForValue().get(key);
        // 1. 命中缓存
        if (StrUtil.isNotBlank(personJson)) {
            return JSONUtil.toBean(personJson, PersonResponse.class);
        }
        // 2. 缓存的是空值
        if ("".equals(personJson)) {
            return null;
        }
        // 3. 未命中缓存，查数据库
        PersonsInfoEntity entity = personsInfoMapper.selectById(id);
        if (entity == null) {
            // 缓存空对象防止穿透
            stringRedisTemplate.opsForValue().set(key, "", 5, TimeUnit.MINUTES);
            return null;
        }
        PersonResponse response = desensitization(entity);
        // 写入缓存，设置合理 TTL
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(response), 30, TimeUnit.MINUTES);
        return response;
    }

    /**
     *
     * @Author: Eleven52AC
     * @Description: 获取人员信息，本地缓存。
     * @param id
     * @param personLocalCache
     * @return
     */
    @Override
    public PersonResponse getPersonWithLocalCache(Long id, ConcurrentHashMap<Integer, PersonResponse> personLocalCache) {
        PersonResponse response = personLocalCache.get(id.intValue());
        // 1. 命中缓存
        if (response != null) {
            return response;
        }
        // 2. 缓存的是空值
        if("".equals(response)){
            return null;
        }
        // 3. 未命中缓存，查数据库
        PersonsInfoEntity entity = personsInfoMapper.selectById(id);
        if (entity == null) {
            // 缓存空对象防止穿透
            personLocalCache.put(id.intValue(), null);
            return null;
        }
        response = desensitization(entity);
        // 写入缓存
        personLocalCache.put(id.intValue(), response);
        return response;
    }

    /**
     * Aside Pattern: 先更新数据库中的数据，然后立即把相关缓存删掉。
     * @param request
     * @return
     */
    @Override
    @Transactional
    public ApiResponse updateBYCacheAsidePattern(PersonRequest request) {
        try{
            // 1. 先更新数据库中的数据
            PersonInfoEntity entity = disposeData(request);
            boolean result = personInfoMapper.insertOrUpdate(entity);
            if (result) {
                // 2. 然后立即把相关缓存删掉
                stringRedisTemplate.delete(PERSON_INFO_PREFIX + request.getId());
                // 3. 延迟一段时间后再次删除
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.schedule(() -> {
                    stringRedisTemplate.delete(PERSON_INFO_PREFIX + request.getId());
                }, 500, TimeUnit.MILLISECONDS);
                return ApiResponse.success(request,"更新成功");
            }else return ApiResponse.error("更新失败");
        }catch (Exception e){
            return ApiResponse.error( e.getMessage(),"更新失败");
        }
    }

    @Override
    public long getDataCount() {
        this.count();
        return this.count();
    }


    private List<PersonResponse> desensitization(List<PersonsInfoEntity> records){
        return records.stream().map(record -> {
            PersonResponse personDto = new PersonResponse.Builder()
                    .id(record.getPersonId())
                    .name(record.getPersonName())
                    .age(IdCardUtils.getAge(record.getCardNumber()))
                    .birthday(IdCardUtils.getBirthday(record.getCardNumber()).toString())
                    .gender(IdCardUtils.getGender(record.getCardNumber()))
                    .register(IdCardUtils.getAddress(record.getCardNumber()))
                    .build();
            return personDto;
        }).collect(Collectors.toList());
    }

    private PersonResponse desensitization(PersonsInfoEntity entity){
        return new PersonResponse(
                entity.getPersonId(),
                entity.getPersonName(),
                IdCardUtils.getAge(entity.getCardNumber()),
                IdCardUtils.getBirthday(entity.getCardNumber()).toString(),
                IdCardUtils.getGender(entity.getCardNumber()),
                IdCardUtils.getAddress(entity.getCardNumber()));
    }

    private PersonInfoEntity disposeData(PersonRequest request){
        if (request == null) {
            return null;
        }
        return new PersonInfoEntity()
                .setId(request.getId())
                .setName(request.getName())
                .setAge(request.getAge())
                .setBirthday(request.getBirthday())
                .setGender(request.getGender())
                .setRegister(request.getRegister());
    }


}




