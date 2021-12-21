package com.laughingather.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.laughingather.gulimall.common.api.MyPage;
import com.laughingather.gulimall.ware.entity.PurchaseEntity;
import com.laughingather.gulimall.ware.entity.param.DonePurchaseParam;
import com.laughingather.gulimall.ware.entity.param.MergePurchaseParam;
import com.laughingather.gulimall.ware.entity.query.PurchaseQuery;

import java.util.List;

/**
 * 采购信息
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-12 11:57:24
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    /**
     * 分页查询采购信息列表
     *
     * @param purchaseQuery
     * @return
     */
    MyPage<PurchaseEntity> listPurchasesWithPage(PurchaseQuery purchaseQuery);

    /**
     * 查询新建、已分配的采购单列表
     *
     * @return
     */
    List<PurchaseEntity> listUnReceivePurchaseDetail();

    /**
     * 合并采购单
     *
     * @param mergePurchaseParam
     */
    void mergePurchase(MergePurchaseParam mergePurchaseParam);

    /**
     * 保存采购单信息
     *
     * @param purchase
     */
    void savePurchase(PurchaseEntity purchase);

    /**
     * 更新采购单信息
     *
     * @param purchase
     */
    void updatePurchaseById(PurchaseEntity purchase);


    /**
     * 完成采购单信息
     *
     * @param ids
     */
    void receivedPurchase(List<Long> ids);

    /**
     * xxx
     *
     * @param donePurchaseDTO
     */
    void donePurchase(DonePurchaseParam donePurchaseDTO);
}

