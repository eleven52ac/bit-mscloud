package com.bit.seckill.controller;

import cn.hutool.core.util.ObjectUtil;
import com.bit.seckill.dto.request.VoucherRequest;
import com.bit.seckill.service.SeckillVoucherService;
import com.bit.seckill.service.VoucherOrderService;
import com.bit.seckill.service.VoucherService;
import common.annotation.CheckLogin;
import common.annotation.EncryptCheck;
import common.dto.response.ApiResponse;
import common.dto.response.ApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Datetime: 2025年06月02日15:07
 * @Author: Eleven也想AC
 * @Description: 优惠券相关接口
 */
@RestController
@RequestMapping ("/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;
    
    @Autowired
    private VoucherOrderService voucherOrderService;

    @Autowired
    private SeckillVoucherService seckillVoucherService;

    /**
     * 添加秒杀券
     * @param request
     * @return
     */
    @PostMapping("/add/seckill")
    @EncryptCheck
    public ApiResponse addSeckillVoucher(VoucherRequest request) {
        boolean result = voucherService.addSeckillVoucher(request);
        if (result) {
            return ApiUtils.success( "添加成功");
        }
         return ApiUtils.error( "添加失败");
    }


    /**
     * 下单秒杀券
     * @param voucherId
     * @return
     */
    @CheckLogin
    @GetMapping("/order/seckill")
    public ApiResponse orderseckillVoucher(@RequestParam("voucherId") Long voucherId){
        try{
            if (ObjectUtil.isEmpty(voucherId)) return ApiUtils.error( "参数错误");
            return voucherOrderService.orderSeckillVoucherStandAloneDeploymentVersion(voucherId);
        }catch (Exception e){
            return ApiUtils.error( e.getMessage(),"秒杀券下单失败");
        }
    }


}
