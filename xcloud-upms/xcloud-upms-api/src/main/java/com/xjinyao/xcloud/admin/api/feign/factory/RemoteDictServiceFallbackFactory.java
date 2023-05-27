package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteDictService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteDictServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteDictServiceFallbackFactory implements FallbackFactory<RemoteDictService> {

    @Override
    public RemoteDictService create(Throwable throwable) {
        RemoteDictServiceFallbackImpl remoteDictServiceFallback = new RemoteDictServiceFallbackImpl();
        remoteDictServiceFallback.setCause(throwable);
        return remoteDictServiceFallback;
    }

}
