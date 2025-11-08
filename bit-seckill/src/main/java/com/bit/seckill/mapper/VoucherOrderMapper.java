package com.bit.seckill.mapper;

import com.bit.seckill.entity.VoucherOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author camel
* @description 针对表【tb_voucher_order】的数据库操作Mapper
* @createDate 2025-06-07 17:42:35
* @Entity com.bit.seckill.entity.VoucherOrder
*/
@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrderEntity> {

}




