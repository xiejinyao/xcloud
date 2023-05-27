package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteUserService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteUserServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteUserServiceFallbackFactory implements FallbackFactory<RemoteUserService> {

    @Override
    public RemoteUserService create(Throwable throwable) {
        RemoteUserServiceFallbackImpl remoteUserServiceFallback = new RemoteUserServiceFallbackImpl();
        remoteUserServiceFallback.setCause(throwable);
        return remoteUserServiceFallback;
    }

}
