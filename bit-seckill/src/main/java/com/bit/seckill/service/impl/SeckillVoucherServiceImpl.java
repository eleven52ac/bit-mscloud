package com.bit.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.seckill.entity.SeckillVoucherEntity;
import com.bit.seckill.mapper.SeckillVoucherMapper;
import com.bit.seckill.service.SeckillVoucherService;
import org.springframework.stereotype.Service;

/**
* @author camel
* @description 针对表【tb_seckill_voucher(秒杀优惠券表，与优惠券是一对一关系)】的数据库操作Service实现
* @createDate 2025-06-02 15:11:14
*/
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherMapper, SeckillVoucherEntity>
    implements SeckillVoucherService {


}




