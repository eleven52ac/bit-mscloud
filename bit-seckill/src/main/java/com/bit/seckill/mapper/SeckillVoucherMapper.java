package com.bit.seckill.mapper;

import com.bit.seckill.entity.SeckillVoucherEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author camel
* @description 针对表【tb_seckill_voucher(秒杀优惠券表，与优惠券是一对一关系)】的数据库操作Mapper
* @createDate 2025-06-02 15:11:14
* @Entity com.bit.seckill.entity.SeckillVoucher
*/
@Mapper
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucherEntity> {

}




