package com.laughingather.gulimall.search.entity;

import cn.easyes.annotation.IndexField;
import cn.easyes.annotation.IndexId;
import cn.easyes.annotation.IndexName;
import cn.easyes.common.constants.Analyzer;
import cn.easyes.common.enums.IdType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Data
@IndexName("gulimall_product")
@Document(indexName = "gulimall_product")
public class EsSku {

    @Id
    @IndexId(type = IdType.CUSTOMIZE)
    private Long skuId;

    /**
     * spuId
     */
    @Field(type = FieldType.Long)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.LONG)
    private Long spuId;

    /**
     * 标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.TEXT, analyzer = Analyzer.IK_SMART)
    private String skuTitle;

    /**
     * 价格
     */
    @Field(type = FieldType.Keyword)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD)
    private BigDecimal skuPrice;

    /**
     * 图片
     */
    @Field(type = FieldType.Keyword)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD)
    private String skuImg;

    /**
     *
     */
    @Field(type = FieldType.Long)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.LONG)
    private Long saleCount;

    /**
     * 是否有库存
     */
    @Field(type = FieldType.Boolean)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.BOOLEAN)
    private Boolean hasStock;

    /**
     * 评分
     */
    @Field(type = FieldType.Long)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.LONG)
    private Long hotScore;

    /**
     * 分类id
     */
    @Field(type = FieldType.Long)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.LONG)
    private Long categoryId;

    /**
     * 分类名称
     */
    @Field(type = FieldType.Keyword, index = false)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD, exist = false)
    private String categoryName;

    /**
     * 品牌id
     */
    @Field(type = FieldType.Long)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.LONG)
    private Long brandId;

    /**
     * 品牌名称
     */
    @Field(type = FieldType.Keyword)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD)
    private String brandName;

    /**
     * 品牌图片
     */
    @Field(type = FieldType.Keyword, index = false)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD, exist = false)
    private String brandImg;

    /**
     * 属性信息
     */
    @Field(type = FieldType.Nested)
    @IndexField(fieldType = cn.easyes.common.enums.FieldType.NESTED)
    private List<Attr> attrs;

    @Data
    public static class Attr {
        /**
         * 属性id
         */
        @Field(type = FieldType.Long)
        @IndexField(fieldType = cn.easyes.common.enums.FieldType.LONG)
        private Long attrId;

        /**
         * 属性名
         */
        @Field(type = FieldType.Keyword)
        @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD)
        private String attrName;

        /**
         * 属性值
         */
        @Field(type = FieldType.Keyword)
        @IndexField(fieldType = cn.easyes.common.enums.FieldType.KEYWORD)
        private String attrValue;
    }

}
