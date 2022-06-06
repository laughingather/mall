package com.laughingather.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laughingather.gulimall.common.api.MyPage;
import com.laughingather.gulimall.common.constant.AuthConstants;
import com.laughingather.gulimall.common.constant.MemberConstants;
import com.laughingather.gulimall.common.util.BCryptPasswordEncoderUtil;
import com.laughingather.gulimall.member.dao.MemberDao;
import com.laughingather.gulimall.member.dao.MemberLevelDao;
import com.laughingather.gulimall.member.entity.MemberEntity;
import com.laughingather.gulimall.member.entity.MemberLevelEntity;
import com.laughingather.gulimall.member.entity.query.MemberQuery;
import com.laughingather.gulimall.member.entity.to.*;
import com.laughingather.gulimall.member.exception.MobileExistException;
import com.laughingather.gulimall.member.exception.UsernameExistException;
import com.laughingather.gulimall.member.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 会员逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private MemberDao memberDao;
    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public MyPage<MemberEntity> listMembersWithPage(MemberQuery memberQuery) {
        LambdaQueryWrapper<MemberEntity> queryWrapper = Wrappers.lambdaQuery(MemberEntity.class);
        if (StringUtils.isNotBlank(memberQuery.getNickname())) {
            queryWrapper.like(MemberEntity::getNickname, memberQuery.getNickname());
        }
        if (StringUtils.isNotBlank(memberQuery.getEmail())) {
            queryWrapper.like(MemberEntity::getEmail, memberQuery.getEmail());
        }
        if (StringUtils.isNotBlank(memberQuery.getMobile())) {
            queryWrapper.like(MemberEntity::getMobile, memberQuery.getMobile());
        }

        Page<MemberEntity> page = new Page<>(memberQuery.getPn(), memberQuery.getPs());

        Page<MemberEntity> memberPage = memberDao.selectPage(page, queryWrapper);
        return MyPage.restPage(memberPage);
    }


    @Override
    public void registerMember(MemberRegisterTO memberRegisterTO) {
        MemberEntity memberEntity = new MemberEntity();

        // 校验数据唯一性
        checkUsernameUnique(memberRegisterTO.getUsername());
        checkMobileUnique(memberRegisterTO.getMobile());
        memberEntity.setUsername(memberRegisterTO.getUsername());
        memberEntity.setNickname(memberRegisterTO.getUsername());
        memberEntity.setMobile(memberRegisterTO.getMobile());

        // 密码加密（利用算法规则把盐值加到密文中，然后比对的时候按照算法规则取出来）
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String bCryptPassword = bCryptPasswordEncoder.encode(memberRegisterTO.getPassword());
        memberEntity.setPassword(bCryptPassword);

        // 查询默认的会员等级
        memberEntity.setLevelId(getDefaultLevel() != null ? getDefaultLevel() : null);
        memberEntity.setCreateTime(LocalDateTime.now());
        memberDao.insert(memberEntity);
    }


    /**
     * 查询默认等级
     *
     * @return
     */
    private Long getDefaultLevel() {
        MemberLevelEntity defaultLevel = memberLevelDao.getDefaultLevel();
        return defaultLevel.getId();
    }

    @Override
    public MemberTO checkLogin(MemberLoginTO memberLoginTO) {
        MemberEntity member = memberDao.selectOne(new QueryWrapper<MemberEntity>().lambda()
                .eq(MemberEntity::getUsername, memberLoginTO.getUsername()).or()
                .eq(MemberEntity::getMobile, memberLoginTO.getUsername()));

        if (member == null) {
            return null;
        }

        // 密码不匹配
        if (!BCryptPasswordEncoderUtil.matchesPassword(memberLoginTO.getPassword(), member.getPassword())) {
            return null;
        }

        MemberTO memberTO = new MemberTO();
        BeanUtils.copyProperties(member, memberTO);
        return memberTO;
    }


    @Override
    public void checkMobileUnique(String mobile) throws MobileExistException {
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().lambda().eq(MemberEntity::getMobile, mobile));
        if (count > 0) {
            throw new MobileExistException();
        }
    }

    @Override
    public void checkEmailUnique(String email) {

    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().lambda().eq(MemberEntity::getUsername, username));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberTO login(SocialUser socialUser) {
        MemberTO memberTO = new MemberTO();

        // 判断当前社交用户是否登陆过本系统
        MemberEntity member = memberDao.getMemberBySocialUid(socialUser.getUid());
        if (member != null) {
            // 更新令牌和令牌过期时间
            MemberEntity updateMember = new MemberEntity();
            updateMember.setId(member.getId());
            updateMember.setAccessToken(socialUser.getAccess_token());
            updateMember.setExpiresIn(socialUser.getExpires_in());
            updateById(updateMember);

            member.setAccessToken(socialUser.getAccess_token());
            member.setExpiresIn(socialUser.getExpires_in());

            // 返回会员信息
            BeanUtils.copyProperties(member, memberTO);
            return memberTO;
        }

        // 如果没有该用户，则调用第三方社交接口获取用户唯一信息，进行注册
        // 获取密码加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String sendUrl = String.format(AuthConstants.WEIBO_INFO_API_URL, socialUser.getAccess_token(), socialUser.getUid());
        ResponseEntity<WeiboUserInfo> result = restTemplate.getForEntity(sendUrl, WeiboUserInfo.class);
        if (result.getStatusCode() == HttpStatus.OK) {
            WeiboUserInfo weiboUserInfo = result.getBody();
            MemberEntity register = MemberEntity.builder().socialUid(weiboUserInfo.getId()).accessToken(socialUser.getAccess_token()).expiresIn(socialUser.getExpires_in())
                    .levelId(getDefaultLevel() != null ? getDefaultLevel() : null).nickname(weiboUserInfo.getScreen_name())
                    .password(bCryptPasswordEncoder.encode(MemberConstants.DEFAULT_MEMBER_PASSWORD)).header(weiboUserInfo.getProfile_image_url())
                    .gender(Objects.equals("m", weiboUserInfo.getGender()) ? 1 : 2).city(weiboUserInfo.getCity()).createTime(LocalDateTime.now()).build();
            memberDao.insert(register);

            BeanUtils.copyProperties(register, memberTO);
            return memberTO;
        }

        return null;
    }

}