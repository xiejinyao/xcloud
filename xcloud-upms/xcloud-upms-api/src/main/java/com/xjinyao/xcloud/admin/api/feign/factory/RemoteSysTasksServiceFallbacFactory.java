package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteSysTasksService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteSysTasksServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author ：lyl
 * @date ：Created in 2021/2/1 15:00
 * @description：
 * @modified By：
 */
public class RemoteSysTasksServiceFallbacFactory implements FallbackFactory<RemoteSysTasksService> {
    @Override
    public RemoteSysTasksService create(Throwable throwable) {
        RemoteSysTasksServiceFallbackImpl remoteSysTasksServiceFallback = new RemoteSysTasksServiceFallbackImpl();
        remoteSysTasksServiceFallback.setCause(throwable);
        return remoteSysTasksServiceFallback;
    }
}
