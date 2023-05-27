package com.xjinyao.xcloud.socket.feign;

import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.socket.feign.factory.RemoteSocketIOMessageServiceFallbackFactory;
import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author 谢进伟
 * @description SocketIo 消息远程调用服务
 * @createDate 2020/6/27 18:17
 */
@FeignClient(contextId = "remoteSocketIOMessageService", value = ServiceNameConstants.SOCKT_SERVICE,
        fallbackFactory = RemoteSocketIOMessageServiceFallbackFactory.class)
public interface RemoteSocketIOMessageService {

    /**
     * 广播消息
     *
     * @param msg 消息对象
     * @return
     */
    @PostMapping("/inner/broadcast")
    R broadcast(@RequestBody BroadcastMessageInfo msg, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 单播消息
     *
     * @param msg 消息对象
     */
    @PostMapping("/inner/unicast")
    R unicast(@RequestBody UnicastMessageInfo msg, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 广播命令消息
     *
     * @param broadcastCommand 消息对象
     * @return
     */
    @PostMapping("/inner/broadcastCommand")
    R broadcastCommand(@RequestBody BroadcastCommand broadcastCommand, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 单播命令消息
     *
     * @param unicastCommand 消息对象
     */
    @PostMapping("/inner/unicastCommand")
    R unicastCommand(@RequestBody UnicastCommand unicastCommand, @RequestHeader(SecurityConstants.FROM) String from);

}
