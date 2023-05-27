package com.xjinyao.xcloud.area.feign.factory;

import com.xjinyao.xcloud.area.feign.RemoteAreaService;
import com.xjinyao.xcloud.area.feign.fallback.RemoteAreaServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author 谢进伟
 * @description
 * @createDate 2020/9/10 14:08
 */
public class RemoteAreaServiceFallbackFactory implements FallbackFactory<RemoteAreaService> {

    @Override
    public RemoteAreaService create(Throwable throwable) {
        RemoteAreaServiceFallbackImpl remoteLogServiceFallback = new RemoteAreaServiceFallbackImpl();
        remoteLogServiceFallback.setCause(throwable);
        return remoteLogServiceFallback;
    }
}
