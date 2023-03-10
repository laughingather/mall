package com.flipped.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.member.dao.MemberReceiveAddressDao;
import com.flipped.mall.member.entity.MemberReceiveAddressEntity;
import com.flipped.mall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 会员收货地址逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Resource
    private MemberReceiveAddressDao memberReceiveAddressDao;

    @Override
    public List<MemberReceiveAddressEntity> listMemberReceiveAddresses(Long memberId) {
        LambdaQueryWrapper<MemberReceiveAddressEntity> queryWrapper = new QueryWrapper<MemberReceiveAddressEntity>().lambda()
                .eq(MemberReceiveAddressEntity::getMemberId, memberId);

        return memberReceiveAddressDao.selectList(queryWrapper);
    }
}