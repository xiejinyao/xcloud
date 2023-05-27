package com.xjinyao.xcloud.mq.api.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 延迟消息载体
 * @createDate 2020/6/15 9:46
 */
@Data
@AllArgsConstructor
public class DelayedMessage implements Serializable {

    /**
     * 交换机名称
     */
    private String exchange;
    /**
     * 路由
     */
    private String routingKey;
    /**
     * 消息内容
     */
    private Object content;
    /**
     * 延迟时间（毫秒）
     */
    private int times;
}
