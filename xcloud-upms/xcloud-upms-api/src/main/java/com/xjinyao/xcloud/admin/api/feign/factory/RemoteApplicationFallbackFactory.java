package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteApplicationService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteApplicationServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteApplicationFallbackFactory implements FallbackFactory<RemoteApplicationService> {

    @Override
    public RemoteApplicationService create(Throwable throwable) {
        RemoteApplicationServiceFallbackImpl applicationServiceFallback = new RemoteApplicationServiceFallbackImpl();
        applicationServiceFallback.setCause(throwable);
        return applicationServiceFallback;
    }

}
