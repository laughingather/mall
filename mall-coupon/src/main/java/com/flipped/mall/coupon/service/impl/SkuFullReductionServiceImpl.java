package com.flipped.mall.coupon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.coupon.dao.SkuFullReductionDao;
import com.flipped.mall.coupon.entity.SkuFullReductionEntity;
import com.flipped.mall.coupon.entity.dto.SkuOtherInfoDTO;
import com.flipped.mall.coupon.service.SkuFullReductionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Resource
    private SkuFullReductionDao skuFullReductionDao;

    @Override
    public void saveSkuFullReduction(SkuOtherInfoDTO skuOtherInfoDTO) {
        SkuFullReductionEntity skuFullReduction = SkuFullReductionEntity.builder().skuId(skuOtherInfoDTO.getSkuId())
                .fullPrice(skuOtherInfoDTO.getFullPrice()).reducePrice(skuOtherInfoDTO.getReducePrice()).build();
        if (skuFullReduction.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            skuFullReductionDao.insert(skuFullReduction);
        }
    }
}