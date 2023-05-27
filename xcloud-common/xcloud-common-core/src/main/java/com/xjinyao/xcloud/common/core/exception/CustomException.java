package com.xjinyao.xcloud.common.core.exception;

import com.xjinyao.xcloud.common.core.enums.ResponseCodeEnum;
import lombok.Getter;

/**
 * 自定义异常类
 */
@Getter
public class CustomException extends Exception {
    private Integer code;

    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMsg());
        this.code = responseCodeEnum.getCode();
    }
}
