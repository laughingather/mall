package com.laughingather.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laughingather.gulimall.member.entity.GrowthChangeHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成长值变化历史记录
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Mapper
public interface GrowthChangeHistoryDao extends BaseMapper<GrowthChangeHistoryEntity> {

}
