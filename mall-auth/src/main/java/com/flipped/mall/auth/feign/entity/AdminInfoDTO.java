package com.flipped.mall.auth.feign.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 用户信息传输类
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Data
public class AdminInfoDTO {

    private Long userid;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 性别（1: 男 2: 女）
     */
    private Integer gender;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 状态(1: 正常  2: 冻结 ）
     */
    private Integer enable;

}

