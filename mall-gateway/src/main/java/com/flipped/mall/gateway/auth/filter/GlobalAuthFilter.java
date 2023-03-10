package com.flipped.mall.gateway.auth.filter;

import com.flipped.mall.common.constant.AuthConstants;
import com.flipped.mall.common.entity.api.MyResult;
import com.flipped.mall.common.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 权限拦截全局过滤器
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Value("${mall.gateway.auth.allow.urls}")
    private String authAllowUrls;

    /**
     * <p> @SneakyThrows注解用于异常语法糖 </p>
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // OPTIONS预检请求直接放行
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS)) {
            return chain.filter(exchange);
        }

        // 路由白名单，直接放行
        String uri = request.getURI().getPath();
        boolean allow = Stream.of(authAllowUrls.split(","))
                .anyMatch(allowUrl -> antPathMatcher.match(allowUrl, uri));
        if (allow) {
            return chain.filter(exchange);
        }

        // 获取token
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // 如果token为空则返回Unauthorized
        if (StringUtils.isBlank(authorization) || !authorization.startsWith(AuthConstants.TOKEN_PREFIX)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return out(response, "请求头 Authorization 为空或格式不正确");
        }

        // 将认证与校验下放到各个微服务模块
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Mono<Void> out(ServerHttpResponse response, String message) {
        MyResult<Void> result = new MyResult<>();
        result.setCode(401);
        result.setData(null);
        result.setMessage(message);

        byte[] bytes = JsonUtil.bean2Json(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        // 指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输出http响应
        return response.writeWith(Mono.just(buffer));
    }
}
