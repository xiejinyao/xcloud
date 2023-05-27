package com.xjinyao.xcloud.socket.websocket.stream.receive;

import com.xjinyao.xcloud.socket.constant.MessageTypeConstant;
import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import com.xjinyao.xcloud.socket.websocket.stream.messaging.WebsocketMessageInput;
import com.xjinyao.xcloud.socket.websocket.stream.service.WebsocketMessageDistributeService;
import com.xjinyao.xcloud.socket.websocket.service.WebsocketMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author 谢进伟
 * @description websocket消息接收，此类负责接收消息，并将消息通过WebsocketMessageService发布到进行相关订阅的客户
 * @createDate 2022/1/14 10:56
 */
@Slf4j
@Component
@AllArgsConstructor
public class WebsocketMessageReceive {

    private final WebsocketMessageService websocketMessageService;

    /**
     * 接收广播websocket消息
     *
     * @param payload           消息体
     * @param messageTypeHeader 消息类型Header
     */
    @StreamListener(value = WebsocketMessageInput.WEBSOCKET_MESSAGE_INPUT,
            condition = "headers['messageType']  == '" + MessageTypeConstant.BROADCAST + "'")
    public void receive(@Payload BroadcastMessageInfo payload,
                        @Header(WebsocketMessageDistributeService.MESSAGE_TYPE) String messageTypeHeader) {
        log.info("接收到{}消息!即将发布消息:{}", messageTypeHeader, payload);
        websocketMessageService.broadcast(payload);
    }


    /**
     * 接收单播websocket消息
     *
     * @param payload           消息体
     * @param messageTypeHeader 消息类型Header
     */
    @StreamListener(value = WebsocketMessageInput.WEBSOCKET_MESSAGE_INPUT,
            condition = "headers['messageType']  == '" + MessageTypeConstant.UNICAST + "'")
    public void receive(@Payload UnicastMessageInfo payload,
                        @Header(WebsocketMessageDistributeService.MESSAGE_TYPE) String messageTypeHeader) {
        log.info("接收到{}消息!即将发布消息:{}", messageTypeHeader, payload);
        websocketMessageService.unicast(payload);
    }

    /**
     * 接收广播命令websocket消息
     *
     * @param payload           消息体
     * @param messageTypeHeader 消息类型Header
     */
    @StreamListener(value = WebsocketMessageInput.WEBSOCKET_MESSAGE_INPUT,
            condition = "headers['messageType']  == '" + MessageTypeConstant.BROADCAST_COMMAND + "'")
    public void receive(@Payload BroadcastCommand payload,
                        @Header(WebsocketMessageDistributeService.MESSAGE_TYPE) String messageTypeHeader) {
        log.info("接收到{}消息!即将发布消息:{}", messageTypeHeader, payload);
        websocketMessageService.broadcastCommand(payload);
    }

    /**
     * 接收单播命令websocket消息
     *
     * @param payload           消息体
     * @param messageTypeHeader 消息类型Header
     */
    @StreamListener(value = WebsocketMessageInput.WEBSOCKET_MESSAGE_INPUT,
            condition = "headers['messageType'] == '" + MessageTypeConstant.UNICAST_COMMAND + "'")
    public void receive(@Payload UnicastCommand payload,
                        @Header(WebsocketMessageDistributeService.MESSAGE_TYPE) String messageTypeHeader) {
        log.info("接收到{}消息!即将发布消息:{}", messageTypeHeader, payload);
        websocketMessageService.unicastCommand(payload);
    }
}
