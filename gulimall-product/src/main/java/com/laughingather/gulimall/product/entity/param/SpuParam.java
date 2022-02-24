/**
 * Copyright 2021 json.cn
 */
package com.laughingather.gulimall.product.entity.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-04-28 12:2:54
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class SpuParam {
    /**
     * spu名称
     */
    private String spuName;

    /**
     * spu描述
     */
    private String spuDescription;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 上架状态
     */
    private Integer publishStatus;

    private List<String> description;

    /**
     * 图片集
     */
    private List<String> images;

    /**
     * 积分信息
     */
    private SpuBoundParam bounds;

    /**
     * 基本属性信息
     */
    private List<SpuBaseAttrParam> baseAttrs;

    /**
     * sku信息
     */
    private List<SkuParam> skus;
}