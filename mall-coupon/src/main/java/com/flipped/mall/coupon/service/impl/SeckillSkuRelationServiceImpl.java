package com.flipped.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.coupon.entity.SecKillSkuRelationEntity;
import com.flipped.mall.coupon.dao.SecKillSkuRelationDao;
import com.flipped.mall.coupon.entity.dto.SecKillSkuRelationDTO;
import com.flipped.mall.coupon.entity.query.SecKillSkuRelationQuery;
import com.flipped.mall.coupon.service.SecKillSkuRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("secKillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SecKillSkuRelationDao, SecKillSkuRelationEntity> implements SecKillSkuRelationService {

    @Override
    public MyPage<SecKillSkuRelationEntity> pageSecKillSkuRelation(SecKillSkuRelationQuery secKillSkuRelationQuery) {
        IPage<SecKillSkuRelationEntity> page = new Page<>(secKillSkuRelationQuery.getPn(), secKillSkuRelationQuery.getPs());
        QueryWrapper<SecKillSkuRelationEntity> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(SecKillSkuRelationEntity::getPromotionSessionId, secKillSkuRelationQuery.getPromotionSessionId());

        IPage<SecKillSkuRelationEntity> secKillSkuRelationPage = baseMapper.selectPage(page, queryWrapper);
        return MyPage.restPage(secKillSkuRelationPage);
    }

    @Override
    public List<SecKillSkuRelationDTO> getRelationSkusByPromotionSessionId(Long promotionSessionId) {
        return baseMapper.getRelationSkusByPromotionSessionId(promotionSessionId);
    }


}