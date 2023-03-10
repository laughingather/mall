package com.flipped.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.member.dao.MemberLevelDao;
import com.flipped.mall.member.entity.MemberLevelEntity;
import com.flipped.mall.member.entity.query.MemberLevelQuery;
import com.flipped.mall.member.service.MemberLevelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 会员等级逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public MyPage<MemberLevelEntity> listMemberLevelsWithPage(MemberLevelQuery memberLevelQuery) {
        IPage<MemberLevelEntity> page = new Page<>(memberLevelQuery.getPn(), memberLevelQuery.getPs());
        QueryWrapper<MemberLevelEntity> queryWrapper = null;
        if (StringUtils.isNotBlank(memberLevelQuery.getKey())) {
            queryWrapper = new QueryWrapper();
            queryWrapper.lambda().eq(MemberLevelEntity::getId, memberLevelQuery.getKey()).or()
                    .like(MemberLevelEntity::getName, memberLevelQuery.getKey());
        }

        IPage<MemberLevelEntity> memberLevelEntityIPage = memberLevelDao.selectPage(page, queryWrapper);
        return MyPage.restPage(memberLevelEntityIPage);
    }
}