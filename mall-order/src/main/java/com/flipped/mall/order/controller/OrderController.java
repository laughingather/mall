package com.flipped.mall.order.controller;

import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.order.entity.OrderEntity;
import com.flipped.mall.order.entity.query.OrderQuery;
import com.flipped.mall.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 订单管理模块
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@RestController
@RequestMapping("order/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/page")
    public MyResult<MyPage<OrderEntity>> listOrdersWithPage(@ModelAttribute OrderQuery orderQuery) {
        MyPage<OrderEntity> orderPage = orderService.listOrdersWithPage(orderQuery);
        return MyResult.success(orderPage);
    }

}
