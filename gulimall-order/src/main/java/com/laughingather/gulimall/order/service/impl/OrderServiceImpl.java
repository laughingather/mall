package com.laughingather.gulimall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laughingather.gulimall.common.api.ErrorCodeEnum;
import com.laughingather.gulimall.common.api.MyPage;
import com.laughingather.gulimall.common.api.MyResult;
import com.laughingather.gulimall.common.api.ResultCodeEnum;
import com.laughingather.gulimall.common.constant.Constants;
import com.laughingather.gulimall.common.constant.OrderConstants;
import com.laughingather.gulimall.order.dao.OrderDao;
import com.laughingather.gulimall.order.entity.OrderEntity;
import com.laughingather.gulimall.order.entity.OrderItemEntity;
import com.laughingather.gulimall.order.entity.dto.OrderDTO;
import com.laughingather.gulimall.order.entity.dto.PayDTO;
import com.laughingather.gulimall.order.entity.dto.WareSkuLockDTO;
import com.laughingather.gulimall.order.entity.param.OrderCreateParam;
import com.laughingather.gulimall.order.entity.param.OrderSubmitParam;
import com.laughingather.gulimall.order.entity.query.OrderQuery;
import com.laughingather.gulimall.order.entity.vo.OrderConfirmVO;
import com.laughingather.gulimall.order.entity.vo.OrderSubmitVO;
import com.laughingather.gulimall.order.feign.entity.*;
import com.laughingather.gulimall.order.feign.service.CartFeignService;
import com.laughingather.gulimall.order.feign.service.MemberFeignService;
import com.laughingather.gulimall.order.feign.service.ProductFeignService;
import com.laughingather.gulimall.order.feign.service.WareFeignService;
import com.laughingather.gulimall.order.interceptor.LoginUserInterceptor;
import com.laughingather.gulimall.order.service.OrderItemService;
import com.laughingather.gulimall.order.service.OrderService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 订单逻辑
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

        // 拼装查询条件
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

        IPage<OrderEntity> orderIPage = orderDao.selectPage(page, queryWrapper);
        return MyPage.restPage(orderIPage);
    }


    @Override
    public OrderConfirmVO confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        // 从拦截类中获取用户信息
        MemberTO member = LoginUserInterceptor.loginUser.get();

        // 拿到主线程的请求信息，将请求信息设置到副线程里面（每一个线程都共享请求信息）
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 1、远程调用会员服务获取收货地址信息
            MyResult<List<MemberReceiveAddressTO>> memberReceiveAddressTOResult = memberFeignService.listMemberReceiveAddress(member.getId());
            if (memberReceiveAddressTOResult.isSuccess()) {
                List<MemberReceiveAddressTO> addresses = memberReceiveAddressTOResult.getData();
                orderConfirmVO.setAddresses(addresses);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> itemsCompletableFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 2、远程调用购物车服务获取购物车项信息
            MyResult<List<OrderItemTO>> currentUserCartItemsResult = cartFeignService.getCurrentUserCartItems();
            if (currentUserCartItemsResult.isSuccess()) {
                List<OrderItemTO> items = currentUserCartItemsResult.getData();
                orderConfirmVO.setItems(items);
            }
        }, threadPoolExecutor).thenRunAsync(() -> {
            // 3、远程调用库存服务获取商品库存信息
            List<OrderItemTO> items = orderConfirmVO.getItems();
            if (CollectionUtils.isNotEmpty(items)) {
                List<Long> skuIds = items.stream().map(OrderItemTO::getSkuId).collect(Collectors.toList());
                MyResult<List<SkuHashStockTO>> skusHasStockResult = wareFeignService.getSkusHasStock(skuIds);
                if (skusHasStockResult.isSuccess()) {
                    for (OrderItemTO item : items) {
                        for (SkuHashStockTO skuHashStockTO : skusHasStockResult.getData()) {
                            if (item.getSkuId().equals(skuHashStockTO.getSkuId())) {
                                item.setHasStock(skuHashStockTO.getHasStock());
                            }
                        }
                    }
                }
            }
        }, threadPoolExecutor);

        // 4、查询会员优惠券信息，主要是会员积分
        Integer integration = member.getIntegration();
        orderConfirmVO.setIntegration(integration);

        // 5、防重令牌
        String uuid = IdUtil.simpleUUID();
        orderConfirmVO.setOrderToken(uuid);
        redisTemplate.opsForValue().set(OrderConstants.USER_ORDER_TOKEN_PREFIX + member.getId(), uuid, 30, TimeUnit.MINUTES);

        // 等待所有异步任务完成
        CompletableFuture.allOf(addressCompletableFuture, itemsCompletableFuture).get();

        return orderConfirmVO;
    }


    /**
     * 订单提交
     *
     * @param orderSubmitParam
     * @return
     * @@Transactional：本地事务，在分布式事务下只能控制自己的回滚，不能控制远程调用的回滚
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderSubmitVO submitOrder(OrderSubmitParam orderSubmitParam) {
        orderSubmitThreadLocal.set(orderSubmitParam);

        // 获取会员信息
        MemberTO member = LoginUserInterceptor.loginUser.get();

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();

        // 1、验证令牌（令牌的对比和删除必须保证原子性）
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmitParam.getOrderToken();
        Long result = (Long) redisTemplate.execute(RedisScript.of(script, Long.class), Arrays.asList(OrderConstants.USER_ORDER_TOKEN_PREFIX + member.getId()), orderToken);
        // 令牌验证失败直接返回
        if (result.equals(0L)) {
            orderSubmitVO.setCode(ErrorCodeEnum.TOKEN_VERIFICATION_EXCEPTION.getCode());
            orderSubmitVO.setMessage(ErrorCodeEnum.TOKEN_VERIFICATION_EXCEPTION.getMessage());
            return orderSubmitVO;
        }

        // 2、下单
        OrderCreateParam orderCreateParam = createOrder();

        // 3、验价
        BigDecimal payAmount = orderCreateParam.getOrder().getPayAmount();
        BigDecimal payPrice = orderSubmitParam.getPayPrice();
        if (Math.abs(payAmount.subtract(payPrice).doubleValue()) > OrderConstants.PRICE_DIFFERENCES) {
            // 前端传入价格和后台计算价格对比，差价大于0.01直接返回错误
            orderSubmitVO.setCode(ErrorCodeEnum.PRICE_VERIFICATION_EXCEPTION.getCode());
            orderSubmitVO.setMessage(ErrorCodeEnum.PRICE_VERIFICATION_EXCEPTION.getMessage());
            return orderSubmitVO;
        }

        // 4、保存订单
        saveOrder(orderCreateParam);

        // 5、锁定库存
        WareSkuLockDTO wareSkuLockDTO = new WareSkuLockDTO();
        List<OrderItemTO> orderItemVOList = orderCreateParam.getOrderItems().stream().map(item -> {
            OrderItemTO orderItemTO = new OrderItemTO();
            orderItemTO.setSkuId(item.getSkuId());
            orderItemTO.setTitle(item.getSkuName());
            orderItemTO.setCount(item.getSkuQuantity());
            return orderItemTO;
        }).collect(Collectors.toList());
        wareSkuLockDTO.setOrderSn(orderCreateParam.getOrder().getOrderSn());
        wareSkuLockDTO.setLocks(orderItemVOList);

        MyResult<Void> lockStockResult = wareFeignService.orderLockStock(wareSkuLockDTO);
        // 如果锁定库存失败，则返回错误
        if (!lockStockResult.isSuccess()) {
            orderSubmitVO.setCode(ErrorCodeEnum.NO_STOCK_EXCEPTION.getCode());
            orderSubmitVO.setMessage(ErrorCodeEnum.NO_STOCK_EXCEPTION.getMessage());
            return orderSubmitVO;
        }

        // 6、发送消息到消息队列
        rabbitTemplate.convertAndSend(OrderConstants.EXCHANGE, OrderConstants.CREATE_ROUTING_KEY, orderCreateParam.getOrder());

        // 全部成功
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
            // 更新订单状态为已取消状态
            OrderEntity updateOrder = new OrderEntity();
            updateOrder.setId(orderId);
            updateOrder.setStatus(OrderConstants.OrderStatusEnum.CANCELLED.getCode());
            orderDao.updateById(updateOrder);

            // 立即发送一个消息
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order, orderDTO);
            rabbitTemplate.convertAndSend(OrderConstants.EXCHANGE, OrderConstants.OTHER_ROUTING_KEY, orderDTO);
        }
    }

    @Override
    public PayDTO getPayOrderInfo(String orderSn) {
        OrderEntity order = getOrderByOrderSn(orderSn);

        String payAmount = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString();
        PayDTO payDTO = PayDTO.builder().out_trade_no(orderSn).subject("谷粒商城支付").total_amount(payAmount).body("测试").build();
        return payDTO;
    }


    /**
     * 保存订单
     *
     * @param orderCreateParam
     */
    public void saveOrder(OrderCreateParam orderCreateParam) {
        // 保存订单
        OrderEntity order = orderCreateParam.getOrder();
        order.setCreateTime(LocalDateTime.now());
        orderDao.insert(order);

        // 保存订单项
        List<OrderItemEntity> orderItems = orderCreateParam.getOrderItems();
        orderItemService.saveBatch(orderItems);

    }


    /**
     * 创建订单
     *
     * @return
     */
    private OrderCreateParam createOrder() {
        OrderSubmitParam orderSubmitParam = orderSubmitThreadLocal.get();

        OrderCreateParam orderCreateParam = new OrderCreateParam();

        // 1、组装订单信息
        OrderEntity order = buildOrder(orderSubmitParam);
        orderCreateParam.setOrder(order);

        // 2、组装订单项信息
        List<OrderItemEntity> orderItems = buildOrderItems(order.getOrderSn());
        orderCreateParam.setOrderItems(orderItems);

        // 3、计算价格、积分
        computePrice(order, orderItems);

        return orderCreateParam;
    }


    /**
     * 构建订单信息
     *
     * @param orderSubmitParam
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitParam orderSubmitParam) {
        OrderEntity order = new OrderEntity();
        // 生成订单号
        String orderSn = IdWorker.getTimeId();
        order.setOrderSn(orderSn);

        // 获取session的会员信息
        MemberTO member = LoginUserInterceptor.loginUser.get();
        // 设置会员信息
        order.setMemberId(member.getId());
        order.setMemberUsername(member.getUsername() != null ? member.getUsername() : member.getNickname());

        // 远程调用库存服务获取收货地址及运费信息
        MyResult<FareTO> fareResult = wareFeignService.getFare(orderSubmitParam.getAddressId());
        if (fareResult.isSuccess()) {
            FareTO fareTO = fareResult.getData();
            // 设置运费信息
            order.setFreightAmount(fareTO.getFare());
            // 设置收货人信息
            order.setReceiverProvince(fareTO.getAddress().getProvince());
            order.setReceiverCity(fareTO.getAddress().getCity());
            order.setReceiverRegion(fareTO.getAddress().getRegion());
            order.setReceiverDetailAddress(fareTO.getAddress().getDetailAddress());
            order.setReceiverName(fareTO.getAddress().getName());
            order.setReceiverPhone(fareTO.getAddress().getPhone());
            order.setReceiverPostCode(fareTO.getAddress().getPostCode());
        }

        // 设置订单状态（未付款）
        order.setStatus(OrderConstants.OrderStatusEnum.CREATE_NEW.getCode());
        // 设置自动收货为15天
        order.setAutoConfirmDay(OrderConstants.AUTO_CONFIRM_DAY);
        // 设置订单删除状态为未删除
        order.setDeleteStatus(Constants.NO);

        return order;
    }


    /**
     * 构建所有订单项信息
     *
     * @param
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        MyResult<List<OrderItemTO>> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        List<OrderItemTO> cartItems = currentUserCartItems.getData();

        if (CollectionUtils.isEmpty(cartItems)) {
            return null;
        }

        List<OrderItemEntity> orderItems = cartItems.stream().map(orderItemVO -> {
            OrderItemEntity orderItem = buildOrderItem(orderItemVO);
            orderItem.setOrderSn(orderSn);
            return orderItem;
        }).collect(Collectors.toList());

        return orderItems;
    }

    /**
     * 构建订单项信息
     *
     * @param orderItemTO
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemTO orderItemTO) {
        OrderItemEntity orderItem = new OrderItemEntity();

        // 商品的spu信息
        MyResult<SpuInfoTO> spuInfoResult = productFeignService.getSpuInfoBySkuId(orderItemTO.getSkuId());
        if (spuInfoResult.isSuccess()) {
            SpuInfoTO spuInfo = spuInfoResult.getData();
            orderItem.setSpuId(spuInfo.getId());
            orderItem.setSpuName(spuInfo.getSpuName());
            orderItem.setSpuPic(spuInfo.getImage());
            orderItem.setCategoryId(spuInfo.getCategoryId());
            orderItem.setBrandId(spuInfo.getBrandId());
        }

        // 商品的sku信息
        orderItem.setSkuId(orderItemTO.getSkuId());
        orderItem.setSkuName(orderItemTO.getTitle());
        orderItem.setSkuPic(orderItemTO.getImage());
        orderItem.setSkuPrice(orderItemTO.getPrice());
        orderItem.setSkuQuantity(orderItemTO.getCount());
        orderItem.setSkuAttrsValues(orderItem.getSkuAttrsValues());

        // 积分信息
        orderItem.setGiftGrowth(orderItemTO.getPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString())).intValue());
        orderItem.setGiftIntegration(orderItemTO.getPrice().multiply(new BigDecimal(orderItem.getSkuQuantity().toString())).intValue());

        // 订单项价格信息
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
     * 计算价格、积分相关信息
     *
     * @param order
     * @param orderItems
     */
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {

        // 订单总金额以及其他优惠金额
        BigDecimal totalPrice = new BigDecimal("0.0");
        BigDecimal promotionPrice = new BigDecimal("0.0");
        BigDecimal couponPrice = new BigDecimal("0.0");
        BigDecimal integrationPrice = new BigDecimal("0.0");
        // 积分和成长值
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

        // 应付总额 = 订单总金额 + 运费
        order.setPayAmount(totalPrice.add(order.getFreightAmount()));
    }

}