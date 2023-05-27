package com.xjinyao.xcloud.socket.websocket.handing;

import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import com.xjinyao.xcloud.socket.websocket.constants.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

/**
 * @author 谢进伟
 * @description websocket消息映射, 参考文档：https://docs.spring.io/spring-framework/docs/4.2.7.RELEASE/spring-framework-reference/htmlsingle/#websocket
 * @createDate 2021/12/30 08:49
 */
@Slf4j
@ApiIgnore
@Controller
public class WebsocketMessageMappingController {

    /**
     * 广播消息
     *
     * @param msg 消息对象
     * @return
     */
    @MessageMapping("/broadcast")
    @SendTo(WebSocketConstants.TOPIC_BROADCAST_DESTINATION)
    public BroadcastMessageInfo broadcast(@Payload BroadcastMessageInfo msg, Principal principal) {
        log.info("接收到 {} 的广播消息", principal.getName());
        return msg;
    }

    /**
     * 单播消息
     *
     * @param msg 消息对象
     */
    @MessageMapping("/unicast")
    @SendToUser(value = WebSocketConstants.TOPIC_UNICAST_DESTINATION, broadcast = false)
    public UnicastMessageInfo unicast(@Payload UnicastMessageInfo msg, Principal principal) {
        log.info("接收到 {} 的单播消息", principal.getName());
        return msg;
    }

    /**
     * 广播命令
     *
     * @param msg 消息对象
     */
    @MessageMapping("/broadcastCommand")
    @SendTo(WebSocketConstants.TOPIC_BROADCAST_COMMAND_DESTINATION)
    public BroadcastCommand broadcastCommand(@Payload BroadcastCommand msg, Principal principal) {
        log.info("接收到 {} 广播命令", principal.getName());
        return msg;
    }


    /**
     * 单播命令
     *
     * @param msg 消息对象
     */
    @MessageMapping("/unicastCommand")
    @SendToUser(value = WebSocketConstants.TOPIC_UNICAST_COMMAND_DESTINATION, broadcast = false)
    public UnicastCommand unicastCommand(@Payload UnicastCommand msg, Principal principal) {
        log.info("接收到 {} 单播命令", principal.getName());
        return msg;
    }
}
