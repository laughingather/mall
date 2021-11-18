package com.laughingather.gulimall.coupon.dao;

import com.laughingather.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laughingather.gulimall.coupon.entity.to.SeckillSkuRelationTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动商品关联
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-12 11:49:53
 */
@Mapper
public interface SeckillSkuRelationDao extends BaseMapper<SeckillSkuRelationEntity> {

    /**
     * 查询关联商品
     *
     * @param promotionSessionId
     * @return
     */
    List<SeckillSkuRelationTO> getRelationSkusByPromotionSessionId(@Param("promotionSessionId") Long promotionSessionId);

}
