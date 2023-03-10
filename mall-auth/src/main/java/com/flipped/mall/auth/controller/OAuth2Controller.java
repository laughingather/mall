package com.flipped.mall.auth.controller;

import com.flipped.mall.auth.entity.dto.SocialUserDTO;
import com.flipped.mall.auth.feign.entity.MemberDTO;
import com.flipped.mall.auth.feign.service.MemberFeignService;
import com.flipped.mall.common.constant.AuthConstants;
import com.flipped.mall.common.entity.api.MyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * oauth2.0路由
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
@Controller
@RequestMapping("/oauth2.0")
public class OAuth2Controller {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private MemberFeignService memberFeignService;

    @Value("${weibo.app-key}")
    private String appKey;
    @Value("${weibo.app-secret}")
    private String appSecret;
    @Value("${weibo.callback-url}")
    private String callbackUrl;

    @GetMapping("/weibo/success")
    public String getAccessToken(HttpSession session, @RequestParam("code") String code) {

        String sendUrl = String.format(AuthConstants.WEIBO_OAUTH_API_URL, appKey, appSecret, code, callbackUrl);
        log.info("请求获取凭证信息地址{}", sendUrl);
        ResponseEntity<SocialUserDTO> result = restTemplate.postForEntity(sendUrl, null, SocialUserDTO.class);

        // 成功
        if (result.getStatusCode() == HttpStatus.OK) {
            // 获取到accessToken
            SocialUserDTO socialUserDTO = result.getBody();
            log.info("获取到的凭证信息{}", socialUserDTO);

            // 当前用户如果是第一次登陆此网址，则自动进行用户注册
            MyResult<MemberDTO> memberResult = memberFeignService.oauth2Login(socialUserDTO);
            if (memberResult.getSuccess()) {
                MemberDTO data = memberResult.getData();
                log.info("用户名：{}", data.getNickname());
                session.setAttribute(AuthConstants.LOGIN_USER, data);
                return "redirect:http://mall.com";
            }
        }

        // 重定向到登录页（失败）
        return "redirect:http://auth.mall.com/login.html";
    }

}
