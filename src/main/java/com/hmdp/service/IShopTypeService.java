package com.hmdp.service;

import com.hmdp.result.Result;
import com.hmdp.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IShopTypeService extends IService<ShopType> {

    /**
     * 查询店铺类型列表
     *
     * @return Result
     */
    Result queryTypeList();
}
