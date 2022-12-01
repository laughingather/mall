package com.laughingather.gulimall.admin;

import com.laughingather.gulimall.common.aspect.PlatformLogAspect;
import com.laughingather.gulimall.common.exception.ExceptionControllerAdvice;
import com.laughingather.gulimall.common.handle.MyMetaObjectHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * 后台管理服务启动类
 *
 * <p>@SpringBootApplication(scanBasePackages = {"com.laughingather.gulimall.*"})   注入common包中的类</p>
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@SpringBootApplication
@EnableDiscoveryClient
@Import({PlatformLogAspect.class, ExceptionControllerAdvice.class, MyMetaObjectHandler.class})
public class GulimallAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAdminApplication.class, args);
    }

}

