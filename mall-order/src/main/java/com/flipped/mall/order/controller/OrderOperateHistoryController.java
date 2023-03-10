package com.flipped.mall.order.controller;

import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.order.entity.OrderOperateHistoryEntity;
import com.flipped.mall.order.service.OrderOperateHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 订单操作历史模块
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@RestController
@RequestMapping("order/order-operate-history")
public class OrderOperateHistoryController {

    @Resource
    private OrderOperateHistoryService orderOperateHistoryService;


    @GetMapping("/{oid}/history")
    public MyResult<List<OrderOperateHistoryEntity>> listHistoriesByOrderId(@PathVariable("oid") Long orderId) {
        List<OrderOperateHistoryEntity> histories = orderOperateHistoryService.listHistoriesByOrderId(orderId);
        return MyResult.success(histories);
    }


}
