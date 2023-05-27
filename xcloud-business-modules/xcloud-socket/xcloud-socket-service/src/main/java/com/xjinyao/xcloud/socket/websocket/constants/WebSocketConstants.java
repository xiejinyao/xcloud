package com.xjinyao.xcloud.socket.websocket.constants;

/**
 * @author 谢进伟
 * @description websocket 相关常量
 * @createDate 2021/12/30 12:36
 */
public class WebSocketConstants {

    /**
     * 订阅广播 Broker（消息代理）名称
     */
    public static final String BROKER_DESTINATION_PREFIX = "/topic/";

    /**
     * 广播消息目的地
     */
    public static final String TOPIC_BROADCAST_DESTINATION = BROKER_DESTINATION_PREFIX + "broadcast";

    /**
     * 单播消息目的地
     */
    public static final String TOPIC_UNICAST_DESTINATION = BROKER_DESTINATION_PREFIX + "unicast";

    /**
     * 广播命令目的地
     */
    public static final String TOPIC_BROADCAST_COMMAND_DESTINATION = BROKER_DESTINATION_PREFIX + "broadcastCommand";

    /**
     * 单播命令目的地
     */
    public static final String TOPIC_UNICAST_COMMAND_DESTINATION = BROKER_DESTINATION_PREFIX + "unicastCommand";
}
