package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.result.Result;
import com.hmdp.entity.VoucherOrder;

public interface IVoucherOrderService extends IService<VoucherOrder> {


    /**
     * 优惠券秒杀下单
     *
     * @param voucherId
     * @return
     */
    Result seckillVoucher(Long voucherId);

//    Result createVoucherOrder(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);
}
