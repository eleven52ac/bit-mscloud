package com.bit.seckill.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.seckill.entity.SeckillVoucherEntity;
import com.bit.seckill.entity.VoucherOrderEntity;
import com.bit.seckill.mapper.VoucherOrderMapper;
import com.bit.seckill.service.SeckillVoucherService;
import com.bit.seckill.service.VoucherOrderService;

import common.dto.response.ApiResponse;
import common.dto.response.ApiUtils;
import common.utils.RedisLock;
import common.utils.core.SnowflakeIdGenerator;
import common.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

import static common.constant.RedisConstants.SECKILL_LOCK_PREFIX;


/**
* @author camel
* @description 针对表【tb_voucher_order】的数据库操作Service实现
* @createDate 2025-06-07 17:42:35
*/
@Service
@DS("mysql3")
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrderEntity>
    implements VoucherOrderService {

    @Autowired
    private SeckillVoucherService seckillVoucherService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);


    /**
     * 下单秒杀券【单机部署版】
     * @param voucherId
     * @return 订单ID
     * @描述： 采用乐(CAS)观锁解决超卖问题，悲观锁处理一人一单问题。适用于单个服务部署，分布式不适用。
     */
    @Override
    public ApiResponse orderSeckillVoucherStandAloneDeploymentVersion(Long voucherId) {
        // 1. 获取用户ID（必须放在事务外）
        Long userId = Long.valueOf(UserThreadLocal.getUserInfo().getUserId());
        // 2. 查询优惠券（只查必要字段）
        SeckillVoucherEntity voucher = seckillVoucherService.lambdaQuery()
                .select(SeckillVoucherEntity::getBeginTime, SeckillVoucherEntity::getEndTime)
                .eq(SeckillVoucherEntity::getVoucherId, voucherId)
                .one();
        if (ObjectUtil.isEmpty(voucher)) {
            return ApiUtils.error(404, "优惠券不存在");
        }
        // 3. 秒杀时间判断
        Date now = new Date();
        if (now.before(voucher.getBeginTime())) {
            return ApiUtils.error(400, "秒杀尚未开始");
        }
        if (now.after(voucher.getEndTime())) {
            return ApiUtils.error(400, "秒杀已结束");
        }
        // 4. 同步锁（防止并发下“一人一单”校验穿透）
        synchronized (userId.toString().intern()) { // todo：锁住用户ID，intern 避免字符串重复创建
            // todo：Spring 事务代理是基于 AOP 实现的，如果直接调用 this.createOrder 会绕过事务。
            VoucherOrderService proxy = (VoucherOrderService) AopContext.currentProxy();
            return proxy.createOrder(voucherId, userId);
            // todo：如果锁加在 createOrder 方法内部，事务提交还未完成就释放锁，会导致并发问题
            //  所以加锁要围绕事务整个过程，确保数据一致性。
        }
    }


    /**
     * 下单秒杀券【分布式版Redis锁】
     * @param voucherId
     * @return 订单ID
     * @描述： 采用乐观锁解决订单超卖问题，采用Redis锁解决分布式场景下的一人一单问题。
     */
    @Override
    public ApiResponse orderSeckillVoucherDistributedVersion(Long voucherId) {
        // 1. 获取用户ID（必须放在事务外）
        Long userId = Long.valueOf(UserThreadLocal.getUserInfo().getUserId());
        // 2. 查询优惠券（只查必要字段）
        SeckillVoucherEntity voucher = seckillVoucherService.lambdaQuery()
                .select(SeckillVoucherEntity::getBeginTime, SeckillVoucherEntity::getEndTime)
                .eq(SeckillVoucherEntity::getVoucherId, voucherId)
                .one();
        if (ObjectUtil.isEmpty(voucher)) {
            return ApiUtils.error(404, "优惠券不存在");
        }
        // 3. 秒杀时间判断
        Date now = new Date();
        if (now.before(voucher.getBeginTime())) {
            return ApiUtils.error(400, "秒杀尚未开始");
        }
        if (now.after(voucher.getEndTime())) {
            return ApiUtils.error(400, "秒杀已结束");
        }
        // 4. 分布式锁（防止并发下“一人一单”校验穿透）
        RedisLock lock = new RedisLock(stringRedisTemplate, SECKILL_LOCK_PREFIX + userId);
        // 5. 尝试获取锁
        boolean isLock = lock.tryLock(1000);
        if (!isLock) {
            return ApiUtils.error(400, "请勿重复下单");
        }
        try {
            // 6. 获取代理对象（事务）
            VoucherOrderService proxy = (VoucherOrderService)AopContext.currentProxy();
            return proxy.createOrder(voucherId, userId);
        }catch (Exception e){
            log.error("【秒杀下单异常】voucherId={}, userId={}", voucherId, userId, e);
            return ApiUtils.error(500, "服务器异常");
        }finally {
            // 7.释放锁
            lock.unLock();
        }
    }


    /**
     * 下单秒杀券【分布式版Redisson锁】
     * @param voucherId
     * @return
     */
    @Override
    public ApiResponse orderSeckillVoucherRessionVersion(Long voucherId) {
        // 1. 获取用户ID（必须放在事务外）
        Long userId = Long.valueOf(UserThreadLocal.getUserInfo().getUserId());
        // 2. 查询优惠券（只查必要字段）
        SeckillVoucherEntity voucher = seckillVoucherService.lambdaQuery()
                .select(SeckillVoucherEntity::getBeginTime, SeckillVoucherEntity::getEndTime)
                .eq(SeckillVoucherEntity::getVoucherId, voucherId)
                .one();
        if (ObjectUtil.isEmpty(voucher)) {
            return ApiUtils.error(404, "优惠券不存在");
        }
        // 3. 秒杀时间判断
        Date now = new Date();
        if (now.before(voucher.getBeginTime())) {
            return ApiUtils.error(400, "秒杀尚未开始");
        }
        if (now.after(voucher.getEndTime())) {
            return ApiUtils.error(400, "秒杀已结束");
        }
        // 4. 分布式锁（防止并发下“一人一单”校验穿透）
        RLock lock = redissonClient.getLock(SECKILL_LOCK_PREFIX + userId);
        // 5. 尝试获取锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            return ApiUtils.error(400, "请勿重复下单");
        }
        try {
            // 6. 获取代理对象（事务）
            VoucherOrderService proxy = (VoucherOrderService)AopContext.currentProxy();
            return proxy.createOrder(voucherId, userId);
        }catch (Exception e){
            log.error("【秒杀下单异常】voucherId={}, userId={}", voucherId, userId, e);
            return ApiUtils.error(500, "服务器异常");
        }finally {
            // 7.释放锁
            lock.unlock();
        }
    }


    /**
     * 创建订单（含一人一单校验 + 乐观锁扣减 + 订单保存）
     */
    @Transactional
    public ApiResponse createOrder(Long voucherId, Long userId) {
        // 5. 一人一单校验
        boolean alreadyOrdered = this.query()
                .eq("user_id", userId)
                .eq("voucher_id", voucherId)
                .count() > 0;
        if (alreadyOrdered) {
            return ApiUtils.error(400, "您已经抢购过一次了");
        }
        // 6. 扣减库存（乐观锁）
        boolean stockUpdated = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId)
                .gt("stock", 0)
                .update();
        if (!stockUpdated) {
            return ApiUtils.error(400, "库存不足");
        }
        // 7. 构建订单并保存
        try {
            Date now = new Date();
            VoucherOrderEntity order = new VoucherOrderEntity.Builder()
                    .id(idGenerator.nextId())
                    .voucherId(voucherId)
                    .userId(userId)
                    .status(1)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            if (!this.save(order)) {
                throw new RuntimeException("订单创建失败");
            }
            return ApiUtils.success(order.getId());
        } catch (Exception e) {
            // todo：可加入库存补偿机制，如MQ异步恢复库存
            return ApiUtils.error(500, "系统繁忙，请重试");
        }
    }
}




