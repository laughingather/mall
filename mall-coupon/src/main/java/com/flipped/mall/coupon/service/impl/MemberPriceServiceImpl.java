package com.flipped.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flipped.mall.common.entity.api.MyPage;
import com.flipped.mall.coupon.dao.MemberPriceDao;
import com.flipped.mall.coupon.entity.MemberPriceEntity;
import com.flipped.mall.coupon.entity.dto.MemberPriceDTO;
import com.flipped.mall.coupon.entity.dto.SkuOtherInfoDTO;
import com.flipped.mall.coupon.entity.query.MemberPriceQuery;
import com.flipped.mall.coupon.service.MemberPriceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 会员价格逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("memberPriceService")
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceDao, MemberPriceEntity> implements MemberPriceService {

    @Resource
    private MemberPriceDao memberPriceDao;

    @Override
    public void saveMemberPrice(SkuOtherInfoDTO skuOtherInfoDTO) {
        List<MemberPriceDTO> memberPriceDTO = skuOtherInfoDTO.getMemberPriceDTO();
        if (CollectionUtils.isNotEmpty(memberPriceDTO)) {
            List<MemberPriceEntity> memberPrices = memberPriceDTO.stream().map(item ->
                    MemberPriceEntity.builder().skuId(skuOtherInfoDTO.getSkuId()).memberLevelId(item.getId())
                            .memberLevelName(item.getName()).memberPrice(item.getPrice()).build()
            ).filter(price -> price.getMemberPrice().compareTo(BigDecimal.ZERO) == 1
            ).collect(Collectors.toList());

            this.saveBatch(memberPrices);
        }
    }

    @Override
    public MyPage<MemberPriceEntity> pageMemberPrice(MemberPriceQuery memberPriceQuery) {
        if (memberPriceQuery.getPn() == null || memberPriceQuery.getPn() <= 0) {
            memberPriceQuery.setPn(1L);
        }
        if (memberPriceQuery.getPs() == null || memberPriceQuery.getPs() <= 0) {
            memberPriceQuery.setPs(10L);
        }

        IPage<MemberPriceEntity> page = new Page<>(memberPriceQuery.getPn(), memberPriceQuery.getPs());
        IPage<MemberPriceEntity> memberPricePage = memberPriceDao.selectPage(page, null);

        return MyPage.restPage(memberPricePage);
    }
}