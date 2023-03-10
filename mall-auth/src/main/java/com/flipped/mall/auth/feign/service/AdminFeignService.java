package com.flipped.mall.auth.feign.service;

import com.flipped.mall.auth.entity.dto.AdminLoginDTO;
import com.flipped.mall.auth.feign.entity.AdminDTO;
import com.flipped.mall.auth.feign.entity.AdminInfoDTO;
import com.flipped.mall.auth.feign.entity.AdminPermissionDTO;
import com.flipped.mall.common.entity.api.MyResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理服务远程调用接口
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@FeignClient("mall-admin")
public interface AdminFeignService {


    /**
     * 远程用户名密码登录接口
     * 返回的数据就是一个JSON串，只要属性匹配就可以进行转换
     *
     * @param adminLoginDTO 用户名密码传输类
     * @return 用户信息
     */
    @PostMapping("/mall-admin/openapi/admin/login")
    MyResult<AdminDTO> login(@RequestBody AdminLoginDTO adminLoginDTO);

    /**
     * 远程手机号验证码登录接口
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    @PostMapping("/mall-admin/openapi/admin/login/mobile")
    MyResult<AdminDTO> loginByMobile(@RequestParam(name = "mobile") String mobile);

    /**
     * 远程获取用户信息接口
     *
     * @param userid 用户id
     * @return 用户信息
     */
    @GetMapping("/mall-admin/openapi/admin/userinfo")
    MyResult<AdminInfoDTO> getUserinfo(@RequestParam("userid") Long userid);

    /**
     * 远程退出登录接口
     *
     * @param token
     * @return
     */
    @DeleteMapping("/mall-admin/openapi/admin/logout")
    MyResult<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    /**
     * 远程获取用户权限列表接口
     *
     * @param userid 用户id
     * @return 权限列表
     */
    @GetMapping("/mall-admin/openapi/admin/permission")
    MyResult<List<AdminPermissionDTO>> getPermission(@RequestParam("userid") Long userid);
}
