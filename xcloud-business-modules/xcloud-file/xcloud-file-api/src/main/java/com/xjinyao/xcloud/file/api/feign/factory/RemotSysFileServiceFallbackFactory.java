package com.xjinyao.xcloud.file.api.feign.factory;

import com.xjinyao.xcloud.file.api.feign.RemoteSysFileService;
import com.xjinyao.xcloud.file.api.feign.fallback.RemoteFileServiceFallbackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author 谢进伟
 * @description 文件服务调用
 * @createDate 2020/6/9 9:27
 */
@Slf4j
@Component
public class RemotSysFileServiceFallbackFactory implements FallbackFactory<RemoteSysFileService> {

    @Override
    public RemoteSysFileService create(Throwable throwable) {
        RemoteFileServiceFallbackImpl remoteLogServiceFallback = new RemoteFileServiceFallbackImpl();
        remoteLogServiceFallback.setCause(throwable);
        return remoteLogServiceFallback;
    }
}
