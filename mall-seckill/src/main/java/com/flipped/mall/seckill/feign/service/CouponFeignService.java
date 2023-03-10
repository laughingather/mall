package com.flipped.mall.seckill.feign.service;

import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.seckill.feign.entity.SecKillSessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 优惠服务第三方调用
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    @GetMapping("/mall-coupon/openapi/coupon/last-3days-session")
    MyResult<List<SecKillSessionDTO>> getLast3DaysSession();


}

