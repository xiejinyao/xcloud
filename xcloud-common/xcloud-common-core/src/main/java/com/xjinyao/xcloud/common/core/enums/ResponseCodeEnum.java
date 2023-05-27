package com.xjinyao.xcloud.common.core.enums;

import lombok.Getter;

/**
 *
 **/
@Getter
public enum ResponseCodeEnum {

    NOT_FOUND(404, "查询结果为空"),
    PARAMETER_ERROR(500, "参数错误"),
    FILE_NULL(500, "未接收到文件"),
    FILE_TYPE_ERROR(500, "文件类型错误"),
    SEARCHPARAM_ERROR(500, "查询条件内容解析错误或非法内容"),
    DEVICE_REGISTER_ERROR(500, "设备注册失败"),
    DEVICE_THRESHOLD_ERROR(500, "设备暂无阈值信息"),
    DEVICE_REGISTER_INSERT_ERROR(500, "设备注册错误，请联系管理员"),
    DEVICE_REGISTER_ERROR100007(100007, "参数不合法"),
    DEVICE_REGISTER_ERROR100416(100416, "设备已经绑定"),
    DEVICE_REGISTER_ERROR100426(100426, "设备已经绑定"),
    DEVICE_REGISTER_ERROR100001(100001, "服务器内部错误"),
    DEVICE_REGISTER_ERROR1012(1012, "注册平台token错误"),
    DEVICE_REGISTER_OK(200, "设备注册成功"),
    DEVICE_OTHER_OK(200, "成功"),
    DEVICE_OTHER_ERROR(500, "失败"),
    DEVICE_UPDATE_OK(200, "修改设备成功"),
    DEVICE_UPDATE_ERROR(500, "绑点错误,只能选择同一灾害点下的监测点"),
    DEVICE_ORDER_OK(200, "命令发送成功"),
    DEVICE_DEL_OK(200, "设备删除成功"),
    DEVICE_DEL_ERROR(500, "设备删除失败"),
    DEVICE_ORDER_ERROR(500, "命令发送失败"),
    DEVICE_CHOOSE_ERROR(500, "线开通平台为电信，协议为NB"),
    XUHAO_ERROR(601, "序号重复,请重新选择!"),
    LONANDLAT_ERROR(500, "经度或纬度格式错误");


    private int code;
    private String msg;

    ResponseCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
