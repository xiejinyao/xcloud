package com.xjinyao.xcloud.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xjinyao.xcloud.common.security.component.CustomAuth2ExceptionSerializer;
import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @date 2019/2/1 自定义OAuth2Exception
 */
@JsonSerialize(using = CustomAuth2ExceptionSerializer.class)
public class CustomAuth2Exception extends OAuth2Exception {

    @Getter
    private String errorCode;

    public CustomAuth2Exception(String msg) {
        super(msg);
    }

    public CustomAuth2Exception(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

}
