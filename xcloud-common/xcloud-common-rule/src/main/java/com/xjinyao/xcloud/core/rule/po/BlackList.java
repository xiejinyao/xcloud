package com.xjinyao.xcloud.core.rule.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author 谢进伟
 * @description 黑名单列表
 * @createDate 2020/11/17 17:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackList implements Serializable {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求uri
     */
    private String requestUri;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 截止时间
     */
    private LocalTime endTime;

    /**
     * 黑名单状态：1:开启　0:关闭
     */
    private String status;

}
