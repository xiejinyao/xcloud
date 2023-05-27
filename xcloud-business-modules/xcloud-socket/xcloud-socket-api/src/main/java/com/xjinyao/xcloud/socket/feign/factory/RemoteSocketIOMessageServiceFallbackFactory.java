package com.xjinyao.xcloud.socket.feign.factory;

import com.xjinyao.xcloud.socket.feign.RemoteSocketIOMessageService;
import com.xjinyao.xcloud.socket.feign.fallback.RemoteSocketIOMessageServiceFallbackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author 谢进伟
 * @description SocketIO 消息调用失败处理工厂
 * @createDate 2020/6/27 18:18
 */
@Slf4j
@Component
public class RemoteSocketIOMessageServiceFallbackFactory implements FallbackFactory<RemoteSocketIOMessageService> {

    @Override
    public RemoteSocketIOMessageService create(Throwable throwable) {
        RemoteSocketIOMessageServiceFallbackImpl remoteLogServiceFallback = new RemoteSocketIOMessageServiceFallbackImpl();
        remoteLogServiceFallback.setCause(throwable);
        return remoteLogServiceFallback;
    }
}
