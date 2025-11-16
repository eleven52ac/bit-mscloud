package com.bit.cache.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.cache.entity.PersonInfoEntity;
import com.bit.cache.mapper.PersonInfoMapper;
import com.bit.cache.service.PersonInfoService;
import com.bit.common.core.dto.response.ApiResponse;
import com.bit.common.utils.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.bit.common.core.constant.redis.RedisConstants.PERSON_INFO_PREFIX;
import static com.bit.common.core.constant.redis.RedisConstants.PERSON_LOCK_PREFIX;

/**
* @author camellia
* @description 针对表【person_info(人员信息表)】的数据库操作Service实现
* @createDate 2025-04-12 23:53:25
*/
@Service
public class PersonInfoServiceImpl extends ServiceImpl<PersonInfoMapper, PersonInfoEntity>
    implements PersonInfoService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PersonInfoMapper personInfoMapper;

    /**
     * Redis 缓存击穿互斥锁解决方案
     * @param id
     * @return
     */
    @Override
    public ApiResponse cacheBreakdownPattern(Long id) {
        boolean lock = false;
        try{
            // 1. 从缓存中查询数据
            String personJson = stringRedisTemplate.opsForValue().get( PERSON_INFO_PREFIX + id);
            // 2. 命中缓存，直接返回。
            if (StrUtil.isNotBlank(personJson)) {
                return ApiResponse.success(JSONUtil.toBean(personJson, PersonInfoEntity.class), "查询成功");
            }
            // 3. 缓存的是空值（预防缓存穿透）
            if("".equals(personJson)){
                return ApiResponse.success(null, "查询成功");
            }
            // 4. 获取锁
            lock = RedisUtils.tryLock(stringRedisTemplate, PERSON_LOCK_PREFIX + id);
            // 5. 获取锁失败，循环 + 重试机制
            if(!lock){
              lock = RedisUtils.tryLockWithRetry(stringRedisTemplate, PERSON_LOCK_PREFIX + id);
                if (!lock) {
                    return ApiResponse.success("系统繁忙，请稍后重试");
                }
            }
            // 6. 获取锁成功
            PersonInfoEntity entity = personInfoMapper.selectById(id);
            // 7. 缓存空对象（预防缓存穿透） (缓存雪崩 TimeOut + 随机分钟)
            if (entity == null){
                long timeOut = (5 + RandomUtil.randomInt(5));
                stringRedisTemplate.opsForValue().set(PERSON_INFO_PREFIX + id, "", timeOut, TimeUnit.MINUTES);
            }
            else {
                long timeOut = (30 + RandomUtil.randomInt(5));
                stringRedisTemplate.opsForValue().set(PERSON_INFO_PREFIX + id, JSONUtil.toJsonStr(entity), timeOut, TimeUnit.MINUTES);
            }
            return ApiResponse.success(entity, "查询成功");
        } catch (InterruptedException e) {
            log.error("PersonsInfoServiceImpl 中的 cacheBreakdownPattern(Long id) 获取锁失败", e);
        }catch (Exception e){
            log.error("PersonsInfoServiceImpl 中的 cacheBreakdownPattern(Long id) 查询失败", e);
        }finally {
            if (lock) RedisUtils.unLock(stringRedisTemplate, PERSON_LOCK_PREFIX + id);
        }
        return ApiResponse.success("查询失败");
    }

}




