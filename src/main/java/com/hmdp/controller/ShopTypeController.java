package com.hmdp.controller;


import com.hmdp.result.Result;
import com.hmdp.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {

    @Resource
    private IShopTypeService typeService;

    /**
     * 查询店铺类型列表
     *
     * @return Result
     */
    @GetMapping("/list")
    public Result queryTypeList() {
        return typeService.queryTypeList();
    }
}
