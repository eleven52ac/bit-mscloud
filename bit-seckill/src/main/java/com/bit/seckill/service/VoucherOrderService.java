package com.bit.seckill.service;

import com.bit.seckill.entity.VoucherOrderEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import common.dto.response.ApiResponse;

/**
* @author camel
* @description 针对表【tb_voucher_order】的数据库操作Service
* @createDate 2025-06-07 17:42:35
*/
public interface VoucherOrderService extends IService<VoucherOrderEntity> {

    ApiResponse orderSeckillVoucherStandAloneDeploymentVersion(Long voucherId);

    ApiResponse createOrder(Long voucherId, Long userId);

    ApiResponse orderSeckillVoucherDistributedVersion(Long voucherId);

    ApiResponse orderSeckillVoucherRessionVersion(Long voucherId);
}
