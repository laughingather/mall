package com.laughingather.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.laughingather.gulimall.common.api.MyPage;
import com.laughingather.gulimall.member.entity.MemberEntity;
import com.laughingather.gulimall.member.entity.dto.MemberLoginDTO;
import com.laughingather.gulimall.member.entity.dto.MemberRegisterDTO;
import com.laughingather.gulimall.member.entity.dto.SocialUser;
import com.laughingather.gulimall.member.entity.query.MemberQuery;
import com.laughingather.gulimall.member.exception.MobileExistException;
import com.laughingather.gulimall.member.exception.UsernameExistException;

/**
 * 会员逻辑接口
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
public interface MemberService extends IService<MemberEntity> {

    /**
     * 分页查询会员列表
     *
     * @param memberQuery
     * @return
     */
    MyPage<MemberEntity> listMembersWithPage(MemberQuery memberQuery);

    /**
     * 注册添加会员
     *
     * @param memberRegisterDTO
     * @return
     */
    void registerMember(MemberRegisterDTO memberRegisterDTO);

    /**
     * 会员登陆校验
     *
     * @param memberLoginDTO
     */
    MemberEntity checkLogin(MemberLoginDTO memberLoginDTO);

    /**
     * 检验手机号码唯一性
     *
     * @param mobile
     * @throws MobileExistException
     */
    void checkMobileUnique(String mobile) throws MobileExistException;

    /**
     * 检验邮箱唯一性
     *
     * @param email
     */
    void checkEmailUnique(String email);

    /**
     * 校验用户名唯一性
     *
     * @param username
     * @throws UsernameExistException
     */
    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
     * 第三方社交帐号登录
     * 具备登录与注册合并逻辑
     *
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser);
}

