package com.xjinyao.xcloud.socket.feign.fallback;

import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.socket.feign.RemoteSocketIOMessageService;
import com.xjinyao.xcloud.socket.message.BroadcastMessageInfo;
import com.xjinyao.xcloud.socket.message.UnicastMessageInfo;
import com.xjinyao.xcloud.socket.message.command.BroadcastCommand;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author 谢进伟
 * @description SocketIO 消息调用失败处理实现实现
 * @createDate 2020/6/27 18:19
 */
@Slf4j
@Component
public class RemoteSocketIOMessageServiceFallbackImpl implements RemoteSocketIOMessageService {

    @Setter
    private Throwable cause;

    @Override
    public R broadcast(BroadcastMessageInfo msg, String from) {
        printStackTrace();
        return R.failed("广播消息失败");
    }

    @PostMapping("/inner/unicast")
    @Override
    public R unicast(UnicastMessageInfo msg, String from) {
        printStackTrace();
        return R.failed("单播消息失败");
    }

    @Override
    public R broadcastCommand(BroadcastCommand broadcastCommand, String from) {
        printStackTrace();
        return R.failed("广播命令失败");
    }

    @Override
    public R unicastCommand(UnicastCommand unicastCommand, String from) {
        printStackTrace();
        return R.failed("单播命令失败");
    }

    private void printStackTrace() {
        if (cause != null) {
            cause.printStackTrace();
        }
    }
}
