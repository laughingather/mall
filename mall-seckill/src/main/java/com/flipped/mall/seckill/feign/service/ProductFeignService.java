package com.flipped.mall.seckill.feign.service;

import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.seckill.feign.entity.SkuInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品服务第三方调用类
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@FeignClient("mall-product")
public interface ProductFeignService {


    /**
     * 根据skuId获取sku详情信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/mall-product/openapi/product/{sid}/info")
    MyResult<SkuInfoDTO> getSkuInfoBySkuId(@PathVariable("sid") Long skuId);

}
