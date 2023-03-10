package com.flipped.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.order.entity.OrderReturnReasonEntity;
import com.flipped.mall.order.entity.query.OrderReturnReasonQuery;

/**
 * 退货原因逻辑接口
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    /**
     * 分页查询退货原因列表
     *
     * @param orderReturnReasonQuery
     * @return
     */
    MyPage<OrderReturnReasonEntity> listOrderReturnReasonsWithPage(OrderReturnReasonQuery orderReturnReasonQuery);

    /**
     * 保存退货原因
     *
     * @param orderReturnReason
     */
    void saveOrderReturnReason(OrderReturnReasonEntity orderReturnReason);

    /**
     * 更新退货原因
     *
     * @param orderReturnReason
     */
    void updateOrderReturnReason(OrderReturnReasonEntity orderReturnReason);
}

