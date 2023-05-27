package com.xjinyao.xcloud.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xjinyao.xcloud.common.security.component.CustomAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * @date 2019/2/1
 */
@JsonSerialize(using = CustomAuth2ExceptionSerializer.class)
public class ForbiddenException extends CustomAuth2Exception {

    public ForbiddenException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "access_denied";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.FORBIDDEN.value();
    }

}
