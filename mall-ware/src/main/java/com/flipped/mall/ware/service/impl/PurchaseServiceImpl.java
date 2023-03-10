package com.flipped.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.common.constant.WareConstants;
import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.ware.dao.PurchaseDao;
import com.flipped.mall.ware.entity.PurchaseDetailEntity;
import com.flipped.mall.ware.entity.PurchaseEntity;
import com.flipped.mall.ware.entity.param.DonePurchaseItemParam;
import com.flipped.mall.ware.entity.param.DonePurchaseParam;
import com.flipped.mall.ware.entity.param.MergePurchaseParam;
import com.flipped.mall.ware.entity.query.PurchaseQuery;
import com.flipped.mall.ware.service.PurchaseDetailService;
import com.flipped.mall.ware.service.PurchaseService;
import com.flipped.mall.ware.service.WareSkuService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private PurchaseDao purchaseDao;
    @Resource
    private PurchaseDetailService purchaseDetailService;
    @Resource
    private WareSkuService wareSkuService;

    @Override
    public MyPage<PurchaseEntity> listPurchasesWithPage(PurchaseQuery purchaseQuery) {
        IPage<PurchaseEntity> page = new Page<>(purchaseQuery.getPn(), purchaseQuery.getPs());
        QueryWrapper<PurchaseEntity> queryWrapper = null;
        if (StringUtils.isNotBlank(purchaseQuery.getKey())) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().like(PurchaseEntity::getAssigneeName, purchaseQuery.getKey())
                    .or().like(PurchaseEntity::getPhone, purchaseQuery.getKey());
        }

        IPage<PurchaseEntity> purchasePage = purchaseDao.selectPage(page, queryWrapper);
        return MyPage.restPage(purchasePage);
    }

    @Override
    public List<PurchaseEntity> listUnReceivePurchaseDetail() {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PurchaseEntity::getStatus, 0).or().eq(PurchaseEntity::getStatus, 1);

        return purchaseDao.selectList(queryWrapper);
    }

    @Override
    public void savePurchase(PurchaseEntity purchase) {
        purchase.setCreateTime(LocalDateTime.now());
        purchaseDao.insert(purchase);
    }

    @Override
    public void updatePurchaseById(PurchaseEntity purchase) {
        purchase.setUpdateTime(LocalDateTime.now());
        purchaseDao.updateById(purchase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergePurchase(MergePurchaseParam mergePurchaseParam) {
        Long purchaseId = mergePurchaseParam.getPurchaseId();
        // ?????????????????????????????????????????????????????????
        purchaseId = createNewPurchase(purchaseId);

        // ???????????????????????????????????????????????????????????????????????????
        PurchaseEntity purchaseById = purchaseDao.selectById(purchaseId);
        if (WareConstants.PurchaseEnum.CREATED.getCode().equals(purchaseById.getStatus()) ||
                WareConstants.PurchaseEnum.ASSIGNED.getCode().equals(purchaseById.getStatus())) {

            // ????????????????????????????????????
            updatePurchaseDetails(mergePurchaseParam.getItems(), purchaseId);

            // ???????????????????????????
            PurchaseEntity purchase = PurchaseEntity.builder().id(purchaseId).updateTime(LocalDateTime.now()).build();
            purchaseDao.updateById(purchase);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receivedPurchase(List<Long> ids) {
        // ?????????????????????????????????????????????????????????
        List<PurchaseEntity> unreceivedPurchase = getUnreceivedPurchase(ids);

        // ????????????????????????
        updateBatchPurchase(unreceivedPurchase);

        // ????????????????????????
        updateBatchPurchaseDetail(unreceivedPurchase);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void donePurchase(DonePurchaseParam donePurchaseParam) {
        // ???????????????id
        Long purchaseId = donePurchaseParam.getPurchaseId();

        // ?????????????????????
        Boolean flag = updateDonePurchaseDetails(donePurchaseParam);

        // ?????????????????????
        updateDonePurchase(purchaseId, flag);

        // ??????????????????????????????
    }


    /**
     * ??????????????????????????????????????????id
     *
     * @param purchaseId
     * @return
     */
    private Long createNewPurchase(Long purchaseId) {
        if (purchaseId == null) {
            PurchaseEntity purchase = PurchaseEntity.builder().status(WareConstants.PurchaseEnum.CREATED.getCode())
                    .createTime(LocalDateTime.now()).build();
            purchaseDao.insert(purchase);
            purchaseId = purchase.getId();
        }
        return purchaseId;
    }

    /**
     * ??????????????????????????????????????????????????????id????????????
     *
     * @param items
     * @param purchaseId
     */
    private void updatePurchaseDetails(List<Long> items, Long purchaseId) {
        if (CollectionUtils.isNotEmpty(items)) {
            List<PurchaseDetailEntity> purchaseDetails = items.stream().map(item ->
                    PurchaseDetailEntity.builder().id(item).purchaseId(purchaseId)
                            .status(WareConstants.PurchaseDetailEnum.ASSIGNED.getCode()).build()
            ).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetails);
        }
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param ids
     * @return
     */
    private List<PurchaseEntity> getUnreceivedPurchase(List<Long> ids) {

        List<PurchaseEntity> unreceivedPurchase = ids.stream().map(id ->
                purchaseDao.selectById(id)
        ).filter(item ->
                item.getStatus().equals(WareConstants.PurchaseEnum.CREATED.getCode()) ||
                        item.getStatus().equals(WareConstants.PurchaseEnum.ASSIGNED.getCode())
        ).collect(Collectors.toList());

        return unreceivedPurchase;
    }

    /**
     * ????????????????????????
     *
     * @param unreceivedPurchase
     */
    private void updateBatchPurchase(List<PurchaseEntity> unreceivedPurchase) {
        if (CollectionUtils.isNotEmpty(unreceivedPurchase)) {
            List<PurchaseEntity> purchases = unreceivedPurchase.stream().map(purchase -> {
                purchase.setStatus(WareConstants.PurchaseEnum.RECEIVE.getCode());
                purchase.setUpdateTime(LocalDateTime.now());
                return purchase;
            }).collect(Collectors.toList());

            this.updateBatchById(purchases);
        }
    }

    /**
     * ????????????????????????
     *
     * @param unreceivedPurchase
     */
    private void updateBatchPurchaseDetail(List<PurchaseEntity> unreceivedPurchase) {
        if (CollectionUtils.isNotEmpty(unreceivedPurchase)) {
            unreceivedPurchase.stream().forEach(purchase -> {
                // ???????????????????????????????????????
                List<PurchaseDetailEntity> purchaseDetails = purchaseDetailService.listPurchaseDetailsByPurchaseId(purchase.getId());

                List<PurchaseDetailEntity> collect = purchaseDetails.stream().map(detail ->
                        PurchaseDetailEntity.builder().id(detail.getId()).status(WareConstants.PurchaseDetailEnum.BUYING.getCode()).build()
                ).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(collect);
            });
        }
    }

    /**
     * ????????????????????????
     *
     * @param purchaseId
     * @param flag
     */
    private void updateDonePurchase(Long purchaseId, Boolean flag) {
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setStatus(flag ? WareConstants.PurchaseEnum.FINISH.getCode() : WareConstants.PurchaseEnum.ERROR.getCode());
        purchase.setUpdateTime(LocalDateTime.now());
        purchaseDao.updateById(purchase);
    }

    /**
     * ????????????????????????
     *
     * @param donePurchaseParam
     * @return
     */
    private Boolean updateDonePurchaseDetails(DonePurchaseParam donePurchaseParam) {
        boolean flag = true;
        List<DonePurchaseItemParam> items = donePurchaseParam.getItems();
        List<PurchaseDetailEntity> purchaseDetails = new ArrayList<>();
        for (DonePurchaseItemParam item : items) {
            PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
            if (WareConstants.PurchaseDetailEnum.ERROR.getCode().equals(item.getStatus())) {
                flag = false;
                purchaseDetail.setStatus(item.getStatus());
            } else {
                purchaseDetail.setStatus(WareConstants.PurchaseDetailEnum.FINISH.getCode());
                // ??????????????????????????????????????????????????????
                updateWareSku(item);
            }

            purchaseDetail.setId(item.getItemId());
            purchaseDetails.add(purchaseDetail);
        }
        purchaseDetailService.updateBatchById(purchaseDetails);
        return flag;
    }

    /**
     * ??????????????????
     *
     * @param donePurchaseItemParam
     */
    private void updateWareSku(DonePurchaseItemParam donePurchaseItemParam) {
        PurchaseDetailEntity byId = purchaseDetailService.getById(donePurchaseItemParam.getItemId());
        wareSkuService.addStock(byId.getSkuId(), byId.getWareId(), byId.getSkuNum());
    }

}