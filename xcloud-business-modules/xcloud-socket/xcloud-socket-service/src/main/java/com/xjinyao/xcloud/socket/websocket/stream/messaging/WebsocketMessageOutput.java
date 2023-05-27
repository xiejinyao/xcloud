package com.xjinyao.xcloud.socket.websocket.stream.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author 谢进伟
 * @description websocket消息发布
 * @createDate 2022/1/14 11:06
 */
public interface WebsocketMessageOutput {

    String WEBSOCKET_MESSAGE_OUTPUT = "WEBSOCKET_MESSAGE_OUTPUT";

    @Output(WebsocketMessageOutput.WEBSOCKET_MESSAGE_OUTPUT)
    MessageChannel webSocketMessageOutputChannel();
}
