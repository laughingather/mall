package com.flipped.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.common.constant.Constants;
import com.flipped.mall.common.constant.OrderConstants;
import com.flipped.mall.common.entity.api.ErrorCodeEnum;
import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.common.entity.api.ResultCodeEnum;
import com.flipped.mall.common.util.JsonUtil;
import com.flipped.mall.order.dao.OrderDao;
import com.flipped.mall.order.entity.OrderEntity;
import com.flipped.mall.order.entity.OrderItemEntity;
import com.flipped.mall.order.entity.dto.OrderDTO;
import com.flipped.mall.order.entity.dto.PayDTO;
import com.flipped.mall.order.entity.dto.WareSkuLockDTO;
import com.flipped.mall.order.entity.param.OrderCreateParam;
import com.flipped.mall.order.entity.param.OrderSubmitParam;
import com.flipped.mall.order.entity.query.OrderQuery;
import com.flipped.mall.order.entity.vo.OrderConfirmVO;
import com.flipped.mall.order.entity.vo.OrderSubmitVO;
import com.flipped.mall.order.feign.entity.*;
import com.flipped.mall.order.feign.service.CartFeignService;
import com.flipped.mall.order.feign.service.MemberFeignService;
import com.flipped.mall.order.feign.service.ProductFeignService;
import com.flipped.mall.order.feign.service.WareFeignService;
import com.flipped.mall.order.interceptor.LoginUserInterceptor;
import com.flipped.mall.order.service.OrderItemService;
import com.flipped.mall.order.service.OrderService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * ????????????
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private final ThreadLocal<OrderSubmitParam> orderSubmitThreadLocal = new ThreadLocal<>();

    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderItemService orderItemService;

    @Resource
    private MemberFeignService memberFeignService;
    @Resource
    private CartFeignService cartFeignService;
    @Resource
    private WareFeignService wareFeignService;
    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;


    @Override
    public MyPage<OrderEntity> listOrdersWithPage(OrderQuery orderQuery) {

        IPage<OrderEntity> page = new Page<>(orderQuery.getPn(), orderQuery.getPs());

        // ??????????????????
        LambdaQueryWrapper<OrderEntity> queryWrapper = Wrappers.lambdaQuery(OrderEntity.class);
        if (StringUtils.isNotBlank(orderQuery.getOrderSn())) {
            queryWrapper.eq(OrderEntity::getOrderSn, orderQuery.getOrderSn());
        }
        if (StringUtils.isNotBlank(orderQuery.getMemberUsername())) {
            queryWrapper.like(OrderEntity::getMemberUsername, orderQuery.getMemberUsername());
        }
        if (orderQuery.getStatus() != null) {
            queryWrapper.eq(OrderEntity::getStatus, orderQuery.getStatus());
        }

        IPage<OrderEntity> orderPage = orderDao.selectPage(page, queryWrapper);
        return MyPage.restPage(orderPage);
    }


    @Override
    public OrderConfirmVO confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        // ?????????????????????????????????
        MemberDTO member = LoginUserInterceptor.loginUser.get();

        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 1???????????????????????????????????????????????????
            MyResult<List<MemberReceiveAddressDTO>> memberReceiveAddressResult = memberFeignService.listMemberReceiveAddress(member.getId());
            if (memberReceiveAddressResult.getSuccess()) {
                List<MemberReceiveAddressDTO> addresses = memberReceiveAddressResult.getData();
                orderConfirmVO.setAddresses(addresses);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> itemsCompletableFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 2??????????????????????????????????????????????????????
            MyResult<List<OrderItemDTO>> currentUserCartItemsResult = cartFeignService.getCurrentUserCartItems();
            if (currentUserCartItemsResult.getSuccess()) {
                List<OrderItemDTO> items = currentUserCartItemsResult.getData();
                orderConfirmVO.setItems(items);
            }
        }, threadPoolExecutor).thenRunAsync(() -> {
            // 3???????????????????????????????????????????????????
            List<OrderItemDTO> items = orderConfirmVO.getItems();
            if (CollectionUtils.isNotEmpty(items)) {
                List<Long> skuIds = items.stream().map(OrderItemDTO::getSkuId).collect(Collectors.toList());
                MyResult<List<SkuHashStockDTO>> skusHasStockResult = wareFeignService.getSkusHasStock(skuIds);
                if (skusHasStockResult.getSuccess()) {
                    for (OrderItemDTO item : items) {
                        for (SkuHashStockDTO skuHashStockDTO : skusHasStockResult.getData()) {
                            if (item.getSkuId().equals(skuHashStockDTO.getSkuId())) {
                                item.setHasStock(skuHashStockDTO.getHasStock());
                            }
                        }
                    }
                }
            }
        }, threadPoolExecutor);

        // 4??????????????????????????????????????????????????????
        Integer integration = member.getIntegration();
        orderConfirmVO.setIntegration(integration);

        // 5???????????????
        String uuid = IdUtil.simpleUUID();
        orderConfirmVO.setOrderToken(uuid);
        redisTemplate.opsForValue().set(OrderConstants.USER_ORDER_TOKEN_PREFIX + member.getId(), uuid, 30, TimeUnit.MINUTES);

        // ??????????????????????????????
        CompletableFuture.allOf(addressCompletableFuture, itemsCompletableFuture).get();

        return orderConfirmVO;
    }


    /**
     * ????????????
     *
     * @param orderSubmitParam
     * @return
     * @@Transactional??????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderSubmitVO submitOrder(OrderSubmitParam orderSubmitParam) {
        orderSubmitThreadLocal.set(orderSubmitParam);

        // ??????????????????
        MemberDTO member = LoginUserInterceptor.loginUser.get();

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();

        // 1??????????????????????????????????????????????????????????????????
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmitParam.getOrderToken();
        Long result = (Long) redisTemplate.execute(RedisScript.of(script, Long.class), Collections.singletonList(OrderConstants.USER_ORDER_TOKEN_PREFIX + member.getId()), orderToken);
        // ??????????????????????????????
        if (Objects.equals(result, 0L)) {
            orderSubmitVO.setCode(ErrorCodeEnum.TOKEN_VERIFICATION_EXCEPTION.getCode());
            orderSubmitVO.setMessage(ErrorCodeEnum.TOKEN_VERIFICATION_EXCEPTION.getMessage());
            return orderSubmitVO;
        }

        // 2?????????
        OrderCreateParam orderCreateParam = createOrder();

        // 3?????????
        BigDecimal payAmount = orderCreateParam.getOrder().getPayAmount();
        BigDecimal payPrice = orderSubmitParam.getPayPrice();
        if (Math.abs(payAmount.subtract(payPrice).doubleValue()) > OrderConstants.PRICE_DIFFERENCES) {
            // ????????????????????????????????????????????????????????????0.01??????????????????
            orderSubmitVO.setCode(ErrorCodeEnum.PRICE_VERIFICATION_EXCEPTION.getCode());
            orderSubmitVO.setMessage(ErrorCodeEnum.PRICE_VERIFICATION_EXCEPTION.getMessage());
            return orderSubmitVO;
        }

        // 4???????????????
        saveOrder(orderCreateParam);

        // 5???????????????
        WareSkuLockDTO wareSkuLockDTO = new WareSkuLockDTO();
        List<OrderItemDTO> orderItemDTOList = orderCreateParam.getOrderItems().stream().map(item -> {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setSkuId(item.getSkuId());
            orderItemDTO.setTitle(item.getSkuName());
            orderItemDTO.setCount(item.getSkuQuantity());
            return orderItemDTO;
        }).collect(Collectors.toList());
        wareSkuLockDTO.setOrderSn(orderCreateParam.getOrder().getOrderSn());
        wareSkuLockDTO.setLocks(orderItemDTOList);

        MyResult<Void> lockStockResult = wareFeignService.orderLockStock(wareSkuLockDTO);
        // ??????????????????????????????????????????
        if (!lockStockResult.getSuccess()) {
            orderSubmitVO.setCode(ErrorCodeEnum.NO_STOCK_EXCEPTION.getCode());
            orderSubmitVO.setMessage(ErrorCodeEnum.NO_STOCK_EXCEPTION.getMessage());
            return orderSubmitVO;
        }

        // 6??????????????????????????????
        String order = JsonUtil.bean2Json(orderCreateParam.getOrder());
        rabbitTemplate.convertAndSend(OrderConstants.EXCHANGE, OrderConstants.CREATE_ROUTING_KEY, order);

        // ????????????
        orderSubmitVO.setCode(ResultCodeEnum.SUCCESS.getCode());
        orderSubmitVO.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        orderSubmitVO.setOrder(orderCreateParam.getOrder());
        return orderSubmitVO;
    }


    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderEntity::getOrderSn, orderSn);

        return orderDao.selectOne(queryWrapper);
    }


    @Override
    public void closeOrder(Long orderId) {
        OrderEntity order = orderDao.selectById(orderId);

        if (order != null && Objects.equals(OrderConstants.OrderStatusEnum.CREATE_NEW.getCode(), order.getStatus())) {
            // ????????????????????????????????????
            OrderEntity updateOrder = new OrderEntity();
            updateOrder.setId(orderId);
            updateOrder.setStatus(OrderConstants.OrderStatusEnum.CANCELLED.getCode());
            orderDao.updateById(updateOrder);

            // ????????????????????????
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order, orderDTO);
            rabbitTemplate.convertAndSend(OrderConstants.EXCHANGE, OrderConstants.OTHER_ROUTING_KEY, JsonUtil.bean2Json(orderDTO));
        }
    }

    @Override
    public PayDTO getPayOrderInfo(String orderSn) {
        OrderEntity order = getOrderByOrderSn(orderSn);

        String payAmount = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString();
        return PayDTO.builder().out_trade_no(orderSn).subject("??????????????????").total_amount(payAmount).body("??????").build();
    }


    /**
     * ????????????
     *
     * @param orderCreateParam
     */
    public void saveOrder(OrderCreateParam orderCreateParam) {
        // ????????????
        OrderEntity order = orderCreateParam.getOrder();
        order.setCreateTime(LocalDateTime.now());
        orderDao.insert(order);

        // ???????????????
        List<OrderItemEntity> orderItems = orderCreateParam.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }


    /**
     * ????????????
     *
     * @return
     */
    private OrderCreateParam createOrder() {
        OrderSubmitParam orderSubmitParam = orderSubmitThreadLocal.get();

        OrderCreateParam orderCreateParam = new OrderCreateParam();

        // 1?????????????????????
        OrderEntity order = buildOrder(orderSubmitParam);
        orderCreateParam.setOrder(order);

        // 2????????????????????????
        List<OrderItemEntity> orderItems = buildOrderItems(order.getOrderSn());
        orderCreateParam.setOrderItems(orderItems);

        // 3????????????????????????
        computePrice(order, orderItems);

        return orderCreateParam;
    }


    /**
     * ??????????????????
     *
     * @param orderSubmitParam
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitParam orderSubmitParam) {
        OrderEntity order = new OrderEntity();
        // ???????????????
        String orderSn = IdWorker.getTimeId();
        order.setOrderSn(orderSn);

        // ??????session???????????????
        MemberDTO member = LoginUserInterceptor.loginUser.get();
        // ??????????????????
        order.setMemberId(member.getId());
        order.setMemberUsername(member.getUsername() != null ? member.getUsername() : member.getNickname());

        // ?????????????????????????????????????????????????????????
        MyResult<FareDTO> fareResult = wareFeignService.getFare(orderSubmitParam.getAddressId());
        if (fareResult.getSuccess()) {
            FareDTO fareDTO = fareResult.getData();
            // ??????????????????
            order.setFreightAmount(fareDTO.getFare());
            // ?????????????????????
            order.setReceiverProvince(fareDTO.getAddress().getProvince());
            order.setReceiverCity(fareDTO.getAddress().getCity());
            order.setReceiverRegion(fareDTO.getAddress().getRegion());
            order.setReceiverDetailAddress(fareDTO.getAddress().getDetailAddress());
            order.setReceiverName(fareDTO.getAddress().getName());
            order.setReceiverPhone(fareDTO.getAddress().getPhone());
            order.setReceiverPostCode(fareDTO.getAddress().getPostCode());
        }

        // ?????????????????????????????????
        order.setStatus(OrderConstants.OrderStatusEnum.CREATE_NEW.getCode());
        // ?????????????????????15???
        order.setAutoConfirmDay(OrderConstants.AUTO_CONFIRM_DAY);
        // ????????????????????????????????????
        order.setDeleteStatus(Constants.NO);

        return order;
    }


    /**
     * ???????????????????????????
     *
     * @param
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        MyResult<List<OrderItemDTO>> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        List<OrderItemDTO> cartItems = currentUserCartItems.getData();

        if (CollectionUtils.isEmpty(cartItems)) {
            return null;
        }

        return cartItems.stream().map(orderItemVO -> {
            OrderItemEntity orderItem = buildOrderItem(orderItemVO);
            orderItem.setOrderSn(orderSn);
            return orderItem;
        }).collect(Collectors.toList());
    }

    /**
     * ?????????????????????
     *
     * @param orderItemDTO
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemDTO orderItemDTO) {
        OrderItemEntity orderItem = new OrderItemEntity();

        // ?????????spu??????
        MyResult<SpuInfoDTO> spuInfoResult = productFeignService.getSpuInfoBySkuId(orderItemDTO.getSkuId());
        if (spuInfoResult.getSuccess()) {
            SpuInfoDTO spuInfo = spuInfoResult.getData();
            orderItem.setSpuId(spuInfo.getId());
            orderItem.setSpuName(spuInfo.getSpuName());
            orderItem.setSpuPic(spuInfo.getImage());
            orderItem.setCategoryId(spuInfo.getCategoryId());
            orderItem.setBrandId(spuInfo.getBrandId());
        }

        // ?????????sku??????
        orderItem.setSkuId(orderItemDTO.getSkuId());
        orderItem.setSkuName(orderItemDTO.getTitle());
        orderItem.setSkuPic(orderItemDTO.getImage());
        orderItem.setSkuPrice(orderItemDTO.getPrice());
        orderItem.setSkuQuantity(orderItemDTO.getCount());
        orderItem.setSkuAttrsValues(orderItem.getSkuAttrsValues());

        // ????????????
        orderItem.setGiftGrowth(orderItemDTO.getPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString())).intValue());
        orderItem.setGiftIntegration(orderItemDTO.getPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString())).intValue());

        // ?????????????????????
        orderItem.setPromotionAmount(new BigDecimal("0"));
        orderItem.setCouponAmount(new BigDecimal("0"));
        orderItem.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal originalPrice = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString()));
        BigDecimal realPrice = originalPrice.subtract(orderItem.getPromotionAmount()).subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getIntegrationAmount());
        orderItem.setRealAmount(realPrice);

        return orderItem;
    }


    /**
     * ?????????????????????????????????
     *
     * @param order
     * @param orderItems
     */
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {

        // ???????????????????????????????????????
        BigDecimal totalPrice = new BigDecimal("0.0");
        BigDecimal promotionPrice = new BigDecimal("0.0");
        BigDecimal couponPrice = new BigDecimal("0.0");
        BigDecimal integrationPrice = new BigDecimal("0.0");
        // ??????????????????
        Integer giftIntegration = 0;
        Integer giftGrowth = 0;

        for (OrderItemEntity orderItem : orderItems) {
            totalPrice = totalPrice.add(orderItem.getRealAmount());
            promotionPrice = promotionPrice.add(orderItem.getPromotionAmount());
            couponPrice = couponPrice.add(orderItem.getCouponAmount());
            integrationPrice = integrationPrice.add(orderItem.getIntegrationAmount());

            giftIntegration += orderItem.getGiftIntegration();
            giftGrowth += orderItem.getGiftGrowth();
        }
        order.setTotalAmount(totalPrice);
        order.setPromotionAmount(promotionPrice);
        order.setCouponAmount(couponPrice);
        order.setIntegrationAmount(integrationPrice);
        order.setIntegration(giftIntegration);
        order.setGrowth(giftGrowth);

        // ???????????? = ??????????????? + ??????
        order.setPayAmount(totalPrice.add(order.getFreightAmount()));
    }

}