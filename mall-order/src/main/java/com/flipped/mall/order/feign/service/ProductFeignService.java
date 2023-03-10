package com.flipped.mall.order.feign.service;

import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.order.feign.entity.SpuInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * 根据skuId获取spu信息
     *
     * @param skuId 商品id
     * @returD spu信息
     */
    @GetMapping("/mall-product/openapi/product/spu-info")
    MyResult<SpuInfoDTO> getSpuInfoBySkuId(@RequestParam("sid") Long skuId);

}

