package com.bit.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bit.seckill.dto.request.VoucherRequest;
import com.bit.seckill.entity.SeckillVoucherEntity;
import com.bit.seckill.entity.VoucherEntity;
import com.bit.seckill.mapper.SeckillVoucherMapper;
import com.bit.seckill.mapper.VoucherMapper;
import com.bit.seckill.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author camel
* @description 针对表【tb_voucher】的数据库操作Service实现
* @createDate 2025-06-02 15:09:50
*/
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, VoucherEntity>
    implements VoucherService {

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
     private SeckillVoucherMapper seckillVoucherMapper;

    @Override
    public boolean addSeckillVoucher(VoucherRequest request) {
        VoucherEntity voucherEntity = request.getVoucher();
        int insertVoucher = voucherMapper.insert(voucherEntity);
        SeckillVoucherEntity seckillVoucherEntity = new SeckillVoucherEntity.Builder()
                .voucherId(voucherEntity.getId())
                .beginTime(request.getSeckillVoucher().getBeginTime())
                .endTime(request.getSeckillVoucher().getEndTime())
                .stock(request.getSeckillVoucher().getStock())
                .createTime(new Date())
                .updateTime(new Date())
                .build();
         int insertSeckillVoucher = seckillVoucherMapper.insert(seckillVoucherEntity);
         return insertVoucher > 0 && insertSeckillVoucher > 0;
    }
}




