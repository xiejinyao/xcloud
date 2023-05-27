package com.xjinyao.xcloud.common.core.exception;


import com.xjinyao.xcloud.common.core.enums.ConstantErrorEnum;

/**
 * @author lhl
 * @Description 设备命令下发异常类处理
 * @Date 2019/1/5 13:54
 */
public class ContrlException extends RuntimeException {
    private Integer code;

    public ContrlException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ContrlException(ConstantErrorEnum constantEnum) {
        super(constantEnum.getMessage());
        this.code = constantEnum.getCode();
    }
}
