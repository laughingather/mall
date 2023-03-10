package com.flipped.mall.ware.listener;

import com.flipped.mall.common.constant.OrderConstants;
import com.flipped.mall.common.constant.WareConstants;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.common.util.JsonUtil;
import com.flipped.mall.ware.entity.WareOrderTaskDetailEntity;
import com.flipped.mall.ware.entity.WareOrderTaskEntity;
import com.flipped.mall.ware.entity.dto.StockDetailDTO;
import com.flipped.mall.ware.entity.dto.StockLockedDTO;
import com.flipped.mall.ware.feign.entity.OrderDTO;
import com.flipped.mall.ware.feign.service.OrderFeignService;
import com.flipped.mall.ware.service.WareOrderTaskDetailService;
import com.flipped.mall.ware.service.WareOrderTaskService;
import com.flipped.mall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 消息消费端
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
@Component
@RabbitListener(queues = "stock.release.stock.queue")
public class UnlockStockListener {

    @Resource
    public OrderFeignService orderFeignService;
    @Resource
    private WareOrderTaskService wareOrderTaskService;
    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Resource
    private WareSkuService wareSkuService;

    /**
     * 解锁锁定库存
     * 收到库存锁定消息
     * <p>
     * 拿到消息后去数据库查询是否存在该库存锁定清单
     * 如果存在则证明库存锁定没有问题，还需要判断订单状态
     * 如果订单不存在或者订单取消才需要执行需要执行库存解锁任务，订单完成则不需要执行库存解锁任务
     * 如果不存在则表示可能执行业务过程中出现异常，持久层数据进行了回滚，这种情况直接签收消息即可
     *
     * @param stockLockedDTO 消息体
     * @param message        消息
     * @param channel        队列
     */
    @RabbitHandler
    public void handleStockLockRelease(String stockLockedDTO, Message message, Channel channel) throws IOException {

        log.info("定时补偿机制解锁库存");

        try {
            unlockStock(JsonUtil.json2Bean(stockLockedDTO, StockLockedDTO.class));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }


    /**
     * 解锁锁定库存
     * 收到订单取消消息
     * <p>
     * 订单取消完成后立即向该队列发送一条消息，以避免由于订单系统网络延时造成的库存锁定消息提前消费的事故
     *
     * @param orderDTO
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void handleOrderCloseRelease(String orderDTO, Message message, Channel channel) throws IOException {

        log.info("订单关闭主动解锁库存");

        try {
            unlockStock(JsonUtil.json2Bean(orderDTO, OrderDTO.class));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }


    /**
     * 解锁库存锁定
     *
     * @param stockLockedDTO
     */
    private void unlockStock(StockLockedDTO stockLockedDTO) {
        StockDetailDTO detail = stockLockedDTO.getDetail();
        WareOrderTaskDetailEntity wareOrderTaskDetail = wareOrderTaskDetailService.getById(detail.getId());

        // 如果库存锁定清单为null则直接签收消息
        if (wareOrderTaskDetail == null) {
            return;
        }

        // 如果库存锁定清单状态不为锁定则直接签收消息
        if (!Objects.equals(WareConstants.StockLockEnum.LOCKED.getCode(), wareOrderTaskDetail.getLockStatus())) {
            return;
        }

        // 远程获取订单状态
        WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getById(wareOrderTaskDetail.getTaskId());
        String orderSn = wareOrderTask.getOrderSn();
        MyResult<OrderDTO> orderResult = orderFeignService.getOrderByOrderSn(orderSn);

        // 如果远程失败则抛出异常
        if (!orderResult.getSuccess()) {
            throw new RuntimeException("远程调用失败");
        }

        OrderDTO order = orderResult.getData();
        // 订单不存在解锁库存
        if (order == null) {
            wareSkuService.unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detail.getId());
            return;
        }

        // 订单状态取消解锁库存
        if (Objects.equals(OrderConstants.OrderStatusEnum.CANCELLED.getCode(), order.getStatus())) {
            wareSkuService.unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detail.getId());
        }
    }


    /**
     * 解锁库存锁定
     *
     * @param orderDTO
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void unlockStock(OrderDTO orderDTO) {
        WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getWareOrderTaskByOrderSn(orderDTO.getOrderSn());
        if (wareOrderTask == null) {
            return;
        }

        List<WareOrderTaskDetailEntity> wareOrderTaskDetails = wareOrderTaskDetailService.listLockerWareOrderTaskDetailByTaskId(wareOrderTask.getId());
        if (CollectionUtils.isEmpty(wareOrderTaskDetails)) {
            return;
        }

        // 解锁库存
        for (WareOrderTaskDetailEntity wareOrderTaskDetail : wareOrderTaskDetails) {
            wareSkuService.unlockStock(wareOrderTaskDetail.getSkuId(), wareOrderTaskDetail.getWareId(),
                    wareOrderTaskDetail.getSkuNum(), wareOrderTaskDetail.getId());
        }

    }


}

