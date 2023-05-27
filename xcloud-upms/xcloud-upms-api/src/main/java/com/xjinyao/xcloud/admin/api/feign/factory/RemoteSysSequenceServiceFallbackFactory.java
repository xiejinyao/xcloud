package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteSysSequenceService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteSysSequenceServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author ：lyl
 * @date ：Created in 2021/3/30 19:08
 * @description：
 * @modified By：
 */
public class RemoteSysSequenceServiceFallbackFactory implements FallbackFactory<RemoteSysSequenceService> {
    @Override
    public RemoteSysSequenceService create(Throwable throwable) {
        RemoteSysSequenceServiceFallbackImpl sysSequenceServiceFallback = new RemoteSysSequenceServiceFallbackImpl();
        sysSequenceServiceFallback.setCause(throwable);
        return sysSequenceServiceFallback;
    }
}
