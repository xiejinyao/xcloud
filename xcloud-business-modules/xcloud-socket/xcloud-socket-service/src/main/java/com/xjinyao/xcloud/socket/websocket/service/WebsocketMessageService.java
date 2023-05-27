package com.xjinyao.xcloud.socket.websocket.service;

import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import com.xjinyao.xcloud.socket.websocket.constants.WebSocketConstants;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 谢进伟
 * @description Websocket消息服务
 * @createDate 2021/12/30 11:36
 */
@Service
@AllArgsConstructor
public class WebsocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 广播消息
     *
     * @param msg 消息对象
     * @return
     */
    public boolean broadcast(BroadcastMessageInfo msg) {
        messagingTemplate.convertAndSend(WebSocketConstants.TOPIC_BROADCAST_DESTINATION, msg);
        return true;
    }

    /**
     * 单播消息
     *
     * @param msg 消息对象
     */
    public boolean unicast(UnicastMessageInfo msg) {
        messagingTemplate.convertAndSendToUser(msg.getTo(), WebSocketConstants.TOPIC_UNICAST_DESTINATION, msg);
        return true;
    }

    /**
     * 广播命令
     *
     * @param msg 消息对象
     */
    public boolean broadcastCommand(BroadcastCommand msg) {
        messagingTemplate.convertAndSend(WebSocketConstants.TOPIC_BROADCAST_COMMAND_DESTINATION, msg);
        return true;
    }


    /**
     * 单播命令
     *
     * @param msg 消息对象
     */
    public boolean unicastCommand(UnicastCommand msg) {
        messagingTemplate.convertAndSendToUser(msg.getTo(), WebSocketConstants.TOPIC_UNICAST_COMMAND_DESTINATION, msg);
        return true;
    }
}
