package com.flipped.mall.common.exception;

import com.flipped.mall.common.entity.api.ErrorCodeEnum;

/**
 * 新密码两次输入不一致
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-06-13 21:35:01
 */
public class NewPasswordMatchException extends PlatformException {

    public NewPasswordMatchException() {
        super(ErrorCodeEnum.NEW_PASSWORD_MATCH_EXCEPTION, "新密码两次输入不一致");
    }

    public NewPasswordMatchException(String additionalErrorMessage) {
        super(ErrorCodeEnum.NEW_PASSWORD_MATCH_EXCEPTION, additionalErrorMessage);
    }
}
