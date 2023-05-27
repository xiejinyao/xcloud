package com.xjinyao.xcloud.socket.websocket.stream.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author 谢进伟
 * @description websocket消息接收
 * @createDate 2022/1/14 11:06
 */
public interface WebsocketMessageInput {

    String WEBSOCKET_MESSAGE_INPUT = "WEBSOCKET_MESSAGE_INPUT";

    @Input(WebsocketMessageInput.WEBSOCKET_MESSAGE_INPUT)
    SubscribableChannel webSocketMessageInputChannel();

}
