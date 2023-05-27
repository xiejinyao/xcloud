package com.xjinyao.xcloud.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xjinyao.xcloud.common.security.component.CustomAuth2ExceptionSerializer;

/**
 * @date 2019/2/1
 */
@JsonSerialize(using = CustomAuth2ExceptionSerializer.class)
public class InvalidException extends CustomAuth2Exception {

    public InvalidException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_exception";
    }

    @Override
    public int getHttpErrorCode() {
        return 426;
    }

}
