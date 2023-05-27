package com.xjinyao.xcloud.admin.api.feign.factory;

import com.xjinyao.xcloud.admin.api.feign.RemoteOrganizationService;
import com.xjinyao.xcloud.admin.api.feign.fallback.RemoteOrganizationServiceFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 组织结构远程服务 fallback factory
 *
 * @author 谢进伟
 * @createDate 2022/11/16 10:45
 */
public class RemoteOrganizationServiceFallbackFactory implements FallbackFactory<RemoteOrganizationService> {

    @Override
    public RemoteOrganizationServiceFallbackImpl create(Throwable throwable) {
        RemoteOrganizationServiceFallbackImpl serviceFallback = new RemoteOrganizationServiceFallbackImpl();
        serviceFallback.setCause(throwable);
        return serviceFallback;
    }
}
