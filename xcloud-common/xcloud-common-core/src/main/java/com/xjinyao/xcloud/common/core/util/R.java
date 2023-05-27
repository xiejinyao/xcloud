package com.xjinyao.xcloud.common.core.util;

import com.xjinyao.xcloud.common.core.constant.CommonConstants;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应信息主体
 *
 * @param <T>
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;

    @Getter
    @Setter
    private T data;

    @Getter
    @Setter
    private Map<String, Object> extendData = new HashMap<>();

    public static <T> R<T> ok() {
        return restResult(null, CommonConstants.SUCCESS, null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, CommonConstants.SUCCESS, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, CommonConstants.SUCCESS, msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, CommonConstants.FAIL, null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, CommonConstants.FAIL, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, CommonConstants.FAIL, null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, CommonConstants.FAIL, msg);
    }

    public static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public R<T> addExtendData(String key, Object value) {
        this.extendData.put(key, value);
        return this;
    }

    public R<T> addExtendData(Map<String, Object> extendDatas) {
        this.extendData.putAll(extendDatas);
        return this;
    }

    public Object getExtendData(String key, Object defalueValue) {
        Object value = getExtendData(key);
        return value == null ? defalueValue : value;
    }

    public Object getExtendData(String key) {
        return this.extendData.get(key);
    }
}
