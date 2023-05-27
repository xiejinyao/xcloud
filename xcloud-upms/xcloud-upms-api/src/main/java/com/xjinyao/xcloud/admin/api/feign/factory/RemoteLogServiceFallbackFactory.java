package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteLogService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteLogServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteLogServiceFallbackFactory implements FallbackFactory<RemoteLogService> {

    @Override
    public RemoteLogService create(Throwable throwable) {
        RemoteLogServiceFallbackImpl remoteLogServiceFallback = new RemoteLogServiceFallbackImpl();
        remoteLogServiceFallback.setCause(throwable);
        return remoteLogServiceFallback;
    }

}
