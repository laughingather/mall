package com.laughingather.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.laughingather.gulimall.ware.entity.WareOrderTaskEntity;

/**
 * 库存工作单
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-12 11:57:23
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    /**
     * 根据订单号获取锁定库存清单
     *
     * @param orderSn
     * @return
     */
    WareOrderTaskEntity getWareOrderTaskByOrderSn(String orderSn);
}

