package com.flipped.mall.cart.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项视图展示类
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Data
public class CartItemVO {

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 是否被选中
     */
    private Boolean check;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片地址
     */
    private String image;

    /**
     * 销售属性
     */
    private List<String> skuAttr;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

}

