package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteTokenService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteTokenServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteTokenServiceFallbackFactory implements FallbackFactory<RemoteTokenService> {

    @Override
    public RemoteTokenService create(Throwable throwable) {
        RemoteTokenServiceFallbackImpl remoteTokenServiceFallback = new RemoteTokenServiceFallbackImpl();
        remoteTokenServiceFallback.setCause(throwable);
        return remoteTokenServiceFallback;
    }

}
