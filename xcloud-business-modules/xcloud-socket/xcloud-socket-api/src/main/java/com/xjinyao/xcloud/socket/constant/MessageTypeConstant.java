package com.xjinyao.xcloud.socket.constant;

import lombok.Getter;

/**
 * @author 谢进伟
 * @description 消息类型
 * @createDate 2020/6/27 17:55
 */
public class MessageTypeConstant {
    /**
     * 广播消息
     */
    public final static String BROADCAST = "broadcast";

    /**
     * 单播消息
     */
    public final static String UNICAST = "unicast";

    /**
     * 广播消息
     */
    public final static String BROADCAST_COMMAND = "broadcastCommand";

    /**
     * 单播消息
     */
    public final static String UNICAST_COMMAND = "unicastCommand";

    @Getter
    private String code;
    @Getter
    private String remark;

    MessageTypeConstant(String code, String remark) {
        this.code = code;
        this.remark = remark;
    }
}
