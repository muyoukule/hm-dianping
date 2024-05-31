package com.hmdp.service;

import com.hmdp.result.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IShopService extends IService<Shop> {

    /**
     * 根据id查询商铺信息
     *
     * @param id
     * @return Result
     */
    Result queryById(Long id);

    /**
     * 更新商铺信息
     *
     * @param shop
     * @return Result
     */
    Result update(Shop shop);

    /**
     * 根据商铺类型分页查询商铺信息
     *
     * @param typeId  商铺类型
     * @param current 页码
     * @return 商铺列表
     */
    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
