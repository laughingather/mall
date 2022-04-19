package com.laughingather.gulimall.auth.controller;

import cn.hutool.core.util.RandomUtil;
import com.laughingather.gulimall.auth.entity.to.AdminLoginTO;
import com.laughingather.gulimall.auth.entity.vo.AdminVO;
import com.laughingather.gulimall.auth.feign.service.ThirdPartyFeignService;
import com.laughingather.gulimall.auth.service.AdminLoginService;
import com.laughingather.gulimall.common.api.ErrorCodeEnum;
import com.laughingather.gulimall.common.api.MyResult;
import com.laughingather.gulimall.common.constant.AuthConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 管理用户登录路由
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@RestController
@RequestMapping("/auth/admin")
public class AdminLoginController {

    @Resource
    private ThirdPartyFeignService thirdPartyFeignService;
    @Resource
    private AdminLoginService adminLoginService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/sms/send-code")
    public MyResult<Void> sendCode(@RequestParam("phoneNum") String phoneNum) {
        Object cacheCode = redisTemplate.opsForValue().get(AuthConstants.SMS_CODE_CACHE_PREFIX + phoneNum);
        if (!Objects.isNull(cacheCode)) {
            // 如果缓存中存在验证码则不允许重发
            return MyResult.failed();
        }

        // 生成随机验证码
        String code = RandomUtil.randomNumbers(6);

        // TODO：第三方调用短信服务暂不可用
        thirdPartyFeignService.sendCheckCode(phoneNum, code);

        // 把验证码放到缓存中
        redisTemplate.opsForValue().set(AuthConstants.SMS_CODE_CACHE_PREFIX + phoneNum, code, 60, TimeUnit.SECONDS);

        return MyResult.success();
    }


    @PostMapping("/login")
    public MyResult<String> login(@Valid @RequestBody AdminLoginTO adminLoginTO) {
        String token = adminLoginService.login(adminLoginTO);
        return StringUtils.isNotBlank(token) ? MyResult.success(token) : MyResult.failed(ErrorCodeEnum.ACCOUNT_PASSWORD_INVALID_EXCEPTION);
    }


    @PostMapping("/logout")
    public MyResult<Void> logout() {
        return null;
    }


    @GetMapping("/userinfo")
    public MyResult<AdminVO> getUserinfo(@RequestParam("token") String token) {
        AdminVO adminVO = adminLoginService.getUserinfo(token);
        return MyResult.success(adminVO);
    }


}
