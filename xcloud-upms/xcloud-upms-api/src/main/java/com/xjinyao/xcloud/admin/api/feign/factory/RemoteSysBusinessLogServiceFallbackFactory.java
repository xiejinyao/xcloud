package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteSysBusinessLogService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteSysBusinessLogServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @date 2019/2/1
 */
public class RemoteSysBusinessLogServiceFallbackFactory implements FallbackFactory<RemoteSysBusinessLogService> {

    @Override
    public RemoteSysBusinessLogService create(Throwable cause) {
        RemoteSysBusinessLogServiceFallbackImpl remoteSysBusinessLogServiceFallback = new RemoteSysBusinessLogServiceFallbackImpl();
        remoteSysBusinessLogServiceFallback.setCause(cause);
        return remoteSysBusinessLogServiceFallback;
    }


}
