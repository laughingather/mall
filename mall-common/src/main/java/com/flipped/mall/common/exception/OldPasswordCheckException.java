package com.flipped.mall.common.exception;

import com.flipped.mall.common.entity.api.ErrorCodeEnum;

/**
 * 密码校验异常
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-06-13 21:20:00
 */
public class OldPasswordCheckException extends PlatformException {

    public OldPasswordCheckException() {
        super(ErrorCodeEnum.OLD_PASSWORD_CHECK_EXCEPTION, "原密码校验失败");
    }

    public OldPasswordCheckException(String additionalErrorMessage) {
        super(ErrorCodeEnum.OLD_PASSWORD_CHECK_EXCEPTION, additionalErrorMessage);
    }
}
