package com.flipped.mall.admin.filter;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.flipped.mall.admin.entity.CustomUserDetails;
import com.flipped.mall.admin.entity.PermissionEntity;
import com.flipped.mall.admin.entity.UserEntity;
import com.flipped.mall.common.constant.AdminConstants;
import com.flipped.mall.common.constant.AuthConstants;
import com.flipped.mall.common.entity.JwtPayLoad;
import com.flipped.mall.common.util.JsonUtil;
import com.flipped.mall.common.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 用户信息拦截器
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 登录等接口没有token，配置白名单后边会直接放行，由下一个链抛出异常
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            chain.doFilter(request, response);
            return;
        }

        // 如果token体为空，则直接放行，交由后边的过滤器处理
        String token = authorization.replace(AuthConstants.TOKEN_PREFIX, "");
        JwtPayLoad jwtPayLoad = TokenProvider.getJwtPayLoad(token);
        if (jwtPayLoad == null) {
            chain.doFilter(request, response);
            return;
        }

        // 获取用户信息及权限
        String customUserDetailsJson = redisTemplate.opsForValue().get(AdminConstants.ADMIN_INFO + jwtPayLoad.getUsername());
        if (StringUtils.isBlank(customUserDetailsJson)) {
            chain.doFilter(request, response);
            return;
        }

        CustomUserDetails customUserDetails = jsonToCustomUserDetails(customUserDetailsJson);
        if (customUserDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 获取用户信息，并将用户信息存储到安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private CustomUserDetails jsonToCustomUserDetails(String customUserDetailsJson) {
        CustomUserDetails customUserDetails = null;
        try {
            // 将 json 格式的字符串转换成 JSONObject 对象
            JSONObject customUserDetailsJSONObject = JSONObject.parseObject(customUserDetailsJson);
            // 简单的直接获取值
            JSONObject userJSONObject = customUserDetailsJSONObject.getJSONObject("user");
            UserEntity user = JsonUtil.json2Bean(userJSONObject.toJSONString(), UserEntity.class);
            // 如果 json 格式的字符串里含有数组格式的属性，将其转换成 JSONArray ，以方便后面转换成对应的实体
            JSONArray permissionJSONArray = customUserDetailsJSONObject.getJSONArray("permissions");
            List<PermissionEntity> permissions = JsonUtil.json2BeanList(permissionJSONArray.toJSONString(), List.class, PermissionEntity.class);

            customUserDetails = new CustomUserDetails();
            customUserDetails.setUser(user);
            customUserDetails.setPermissions(permissions);
        } catch (Exception e) {
            log.error("============= json转换成实体类出错，用户拦截获取缓存信息失败 =============\n 错误信息：{}", e.getMessage());
            e.printStackTrace();
        }
        return customUserDetails;
    }
}