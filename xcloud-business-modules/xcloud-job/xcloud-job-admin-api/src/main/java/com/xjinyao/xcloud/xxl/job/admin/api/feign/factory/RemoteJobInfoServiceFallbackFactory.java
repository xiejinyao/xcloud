package com.xjinyao.xcloud.xxl.job.admin.api.feign.factory;

import com.xjinyao.xcloud.xxl.job.admin.api.feign.RemoteJobInfoService;
import com.xjinyao.xcloud.xxl.job.admin.api.feign.fallback.RemoteJobInfoServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteJobInfoServiceFallbackFactory implements FallbackFactory<RemoteJobInfoService> {

    @Override
    public RemoteJobInfoService create(Throwable throwable) {
        throwable.printStackTrace();
        RemoteJobInfoServiceFallbackImpl remoteJobInfoServiceFallback = new RemoteJobInfoServiceFallbackImpl();
        remoteJobInfoServiceFallback.setCause(throwable);
        return remoteJobInfoServiceFallback;
    }

}
