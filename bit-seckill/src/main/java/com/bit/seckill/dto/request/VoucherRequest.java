package com.bit.seckill.dto.request;

import com.bit.seckill.entity.SeckillVoucherEntity;
import com.bit.seckill.entity.VoucherEntity;
import lombok.Data;

/**
 * @Datetime: 2025年06月05日14:56
 * @Author: Eleven也想AC
 * @Description: 优惠券DTO
 */
@Data
public class VoucherRequest {

    private VoucherEntity voucher;

    private SeckillVoucherEntity seckillVoucher;
}
