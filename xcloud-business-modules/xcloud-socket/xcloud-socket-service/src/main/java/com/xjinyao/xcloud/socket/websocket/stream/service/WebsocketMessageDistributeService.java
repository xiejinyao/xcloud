package com.xjinyao.xcloud.socket.websocket.stream.service;

import com.xjinyao.xcloud.socket.constant.MessageTypeConstant;
import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import com.xjinyao.xcloud.socket.websocket.stream.messaging.WebsocketMessageProcessor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author 谢进伟
 * @description websocket消息分发服务，此服务负责将消息发布到队列中
 * @createDate 2022/1/14 11:00
 */
@Service
@AllArgsConstructor
public class WebsocketMessageDistributeService {

    private final WebsocketMessageProcessor messageProcessor;

    public final static String MESSAGE_TYPE = "messageType";

    /**
     * 发布广播消息
     *
     * @param msg 消息对象
     * @return
     */
    public boolean sendBroadcastMessage(BroadcastMessageInfo msg) {
        return messageProcessor.webSocketMessageOutputChannel().send(MessageBuilder.createMessage(msg,
                new MessageHeaders(Collections.singletonMap(MESSAGE_TYPE, MessageTypeConstant.BROADCAST))));
    }

    /**
     * 发布单播消息
     *
     * @param msg 消息对象
     */
    public boolean sendUnicastMessage(UnicastMessageInfo msg) {
        return messageProcessor.webSocketMessageOutputChannel().send(MessageBuilder.createMessage(msg,
                new MessageHeaders(Collections.singletonMap(MESSAGE_TYPE, MessageTypeConstant.UNICAST))));
    }

    /**
     * 发布广播命令消息
     *
     * @param msg 消息对象
     */
    public boolean sendBroadcastCommandMessage(BroadcastCommand msg) {
        return messageProcessor.webSocketMessageOutputChannel().send(MessageBuilder.createMessage(msg,
                new MessageHeaders(Collections.singletonMap(MESSAGE_TYPE, MessageTypeConstant.BROADCAST_COMMAND))));
    }

    /**
     * 发布单播命令消息
     *
     * @param msg 消息对象
     */
    public boolean sendUnicastCommandMessage(UnicastCommand msg) {
        return messageProcessor.webSocketMessageOutputChannel().send(MessageBuilder.createMessage(msg,
                new MessageHeaders(Collections.singletonMap(MESSAGE_TYPE, MessageTypeConstant.UNICAST_COMMAND))));
    }
}