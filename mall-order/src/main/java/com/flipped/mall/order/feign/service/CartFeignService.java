package com.flipped.mall.order.feign.service;

import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.order.feign.entity.OrderItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 购物车服务远程调用类
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@FeignClient("mall-cart")
public interface CartFeignService {

    /**
     * 获取当前用户购物车项
     *
     * @return 用户车购物项列表
     */
    @GetMapping("/mall-cart/openapi/cart/current-user/cart-items")
    MyResult<List<OrderItemDTO>> getCurrentUserCartItems();

}

