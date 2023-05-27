package com.xjinyao.xcloud.socket.websocket.stream.binding;

import com.xjinyao.xcloud.socket.websocket.stream.messaging.WebsocketMessageProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * @author 谢进伟
 * @description websocket消息的发布、接收相关的关系绑定
 * @createDate 2022/1/17 11:21
 */
@EnableBinding(WebsocketMessageProcessor.class)
public class EnableWebsocketMessageBinding {

}
