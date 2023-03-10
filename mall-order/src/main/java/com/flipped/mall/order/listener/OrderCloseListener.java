package com.flipped.mall.order.listener;

import com.flipped.mall.order.entity.OrderEntity;
import com.flipped.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 订单关闭监听
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
@Component
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {

    @Resource
    private OrderService orderService;

    @RabbitHandler
    public void releaseOrder(OrderEntity order, Message message, Channel channel) throws IOException {
        try {
            orderService.closeOrder(order.getId());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }

}

