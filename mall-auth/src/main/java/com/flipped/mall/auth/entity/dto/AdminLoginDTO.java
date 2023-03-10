package com.flipped.mall.auth.entity.dto;

import com.flipped.mall.common.valid.CaptchaEnableGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 用户登录实体类
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Data
public class AdminLoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码长度必须在6-16位之间")
    private String password;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空", groups = CaptchaEnableGroup.class)
    private String captcha;

}
