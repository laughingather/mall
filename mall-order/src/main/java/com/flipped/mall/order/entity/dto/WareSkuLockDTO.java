package com.flipped.mall.order.entity.dto;

import com.flipped.mall.order.feign.entity.OrderItemDTO;
import lombok.Data;

import java.util.List;

/**
 * 库存锁定传输类
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Data
public class WareSkuLockDTO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单项
     */
    private List<OrderItemDTO> locks;

}

