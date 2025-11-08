package com.bit.seckill.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.bit.seckill.dto.request.VoucherRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bit.seckill.entity.VoucherEntity;


/**
* @author Eleven也想AC
* @description 针对表【tb_voucher】的数据库操作Service
* @createDate 2025-06-02 15:09:50
*/
@DS( "mysql3")
public interface VoucherService extends IService<VoucherEntity> {

    boolean addSeckillVoucher(VoucherRequest request);
}
