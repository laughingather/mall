package com.laughingather.gulimall.auth.service.impl;

import com.laughingather.gulimall.auth.entity.to.AdminLoginByMobileTO;
import com.laughingather.gulimall.auth.entity.to.AdminLoginTO;
import com.laughingather.gulimall.auth.entity.vo.AdminVO;
import com.laughingather.gulimall.auth.exception.SmsCodeCheckFailException;
import com.laughingather.gulimall.auth.exception.SmsCodeExpireException;
import com.laughingather.gulimall.auth.feign.entity.AdminInfoTO;
import com.laughingather.gulimall.auth.feign.entity.AdminTO;
import com.laughingather.gulimall.auth.feign.service.AdminFeignService;
import com.laughingather.gulimall.auth.service.AdminLoginService;
import com.laughingather.gulimall.auth.service.AuthService;
import com.laughingather.gulimall.common.api.MyResult;
import com.laughingather.gulimall.common.constant.AuthConstants;
import com.laughingather.gulimall.common.entity.JwtPayLoad;
import com.laughingather.gulimall.common.util.TokenProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 管理登录逻辑实现
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Service
public class AdminLoginServiceImpl implements AdminLoginService {

    @Resource
    private AuthService authService;
    @Resource
    private AdminFeignService adminFeignService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String login(AdminLoginTO adminLoginTO) {
        MyResult<AdminTO> adminLoginResult = adminFeignService.login(adminLoginTO);
        // 登录失败
        if (!adminLoginResult.isSuccess()) {
            return null;
        }

        // 登录成功，生成token
        AdminTO adminTO = adminLoginResult.getData();
        JwtPayLoad jwtPayLoad = new JwtPayLoad(adminTO.getUserid(), adminTO.getUsername());
        return authService.generateToken(jwtPayLoad);
    }

    @Override
    public String loginByMobile(AdminLoginByMobileTO adminLoginByMobileTO) {
        Boolean hasSmsCode = redisTemplate.hasKey(AuthConstants.SMS_CODE_CACHE_PREFIX + adminLoginByMobileTO.getMobile());
        if (Boolean.FALSE.equals(hasSmsCode)) {
            // 验证码过期
            throw new SmsCodeExpireException();
        }

        String cacheCode = redisTemplate.opsForValue().get(AuthConstants.SMS_CODE_CACHE_PREFIX + adminLoginByMobileTO.getMobile());
        if (!Objects.equals(adminLoginByMobileTO.getMobile(), cacheCode)) {
            // 验证码校验失败
            throw new SmsCodeCheckFailException();
        }


        return null;
    }

    @Override
    public AdminVO getUserinfo(String token) {
        // 解析token
        token = token.replace(AuthConstants.TOKEN_PREFIX, "");
        JwtPayLoad jwtPayLoad = TokenProvider.getJwtPayLoad(token);
        Long userid = jwtPayLoad.getUserid();

        MyResult<AdminInfoTO> getUserinfoResult = adminFeignService.getUserinfo(userid);

        // 登录成功
        if (!getUserinfoResult.isSuccess()) {
            return null;
        }

        AdminInfoTO adminInfoTO = getUserinfoResult.getData();

        // 生成token
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(adminInfoTO, adminVO);
        return adminVO;
    }
}

