package com.xjinyao.xcloud.common.core.enums;

import lombok.Getter;

/**
 * @author lhl
 * @Description 错误类
 * @Date 2019/1/3 17:41
 */
@Getter
public enum ConstantErrorEnum {

    NOT_LOAD_FILE(1, "文件未能加载"),
    NOT_FILE_UP(1, "文件上传失败"),
    NOT_PARAM(1, "参数错误或参数为空"),
    PARAM_MAX_UPLOAD(1, "上报周期阈值过大"),
    PARAM_MAX_VIBRATION(1, "震动阈值过大"),
    PARAM_MAX_WARNTIME(1, "报警时间设置过大"),
    NOT_ONENET_ERROR(1, "未与物联网平台建立连接"),
    PARAM_MAX_SAMP(1, "采样间隔范围0~127"),
    PARAM_MAX_RAIN(1, "雨量阈值过大"),
    CONNENT_TIME_OUT(1, "请求超时，请重试!"),
    ;

    private int code;
    private String message;

    ConstantErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
