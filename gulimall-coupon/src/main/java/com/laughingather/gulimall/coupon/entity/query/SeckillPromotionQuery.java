package com.laughingather.gulimall.coupon.entity.query;

import com.laughingather.gulimall.common.query.PageQuery;
import lombok.Data;

/**
 * 秒杀条件参数
 *
 * @author：laughingather
 * @create：2021-11-10 2021/11/10
 */
@Data
public class SeckillPromotionQuery extends PageQuery {

    private String key;

}

