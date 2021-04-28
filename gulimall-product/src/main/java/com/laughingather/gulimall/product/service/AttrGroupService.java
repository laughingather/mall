package com.laughingather.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.laughingather.gulimall.common.api.MyPage;
import com.laughingather.gulimall.product.entity.AttrGroupEntity;
import com.laughingather.gulimall.product.entity.query.AttrGroupQuery;
import com.laughingather.gulimall.product.entity.vo.AttrGroupVO;
import com.laughingather.gulimall.product.entity.vo.AttrGroupWithAttrsVO;

import java.util.List;

/**
 * 属性分组
 *
 * @author laughingather
 * @email laughingather@gmail.com
 * @date 2021-04-11 15:12:49
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    MyPage<AttrGroupEntity> pageAttrGroupsByParams(Long catId, AttrGroupQuery attrGroupQuery);

    AttrGroupVO getAttrGroupById(Long attrGroupId);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCategoryId(Long categoryId);
}

