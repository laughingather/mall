package com.flipped.mall.product.controller;

import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.product.entity.SkuInfoEntity;
import com.flipped.mall.product.entity.query.SkuInfoQuery;
import com.flipped.mall.product.service.SkuInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * sku路由
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@RestController
@RequestMapping("/product/sku")
public class SkuInfoController {

    @Resource
    private SkuInfoService skuInfoService;

    @GetMapping("/page")
    public MyResult<MyPage<SkuInfoEntity>> listSkusWithPage(@ModelAttribute SkuInfoQuery skuInfoQuery) {
        MyPage<SkuInfoEntity> skuInfoWithPage = skuInfoService.listSkusWithPage(skuInfoQuery);
        return MyResult.success(skuInfoWithPage);
    }

}
