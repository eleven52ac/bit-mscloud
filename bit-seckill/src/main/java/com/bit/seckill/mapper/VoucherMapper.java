package com.bit.seckill.mapper;

import com.bit.seckill.entity.VoucherEntity;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author camel
* @description 针对表【tb_voucher】的数据库操作Mapper
* @createDate 2025-06-02 15:09:50
* @Entity com.bit.seckill.entity.Voucher
*/
@Mapper
public interface VoucherMapper extends BaseMapper<VoucherEntity> {

}




