package com.flipped.mall.admin.config;

import com.laughingather.gulimall.common.config.BaseMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus配置
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Configuration
@MapperScan(basePackages = {"com.laughingather.gulimall.admin.mapper"})
public class MybatisPlusConfig extends BaseMybatisPlusConfig {
}