package com.xjinyao.xcloud.admin.api.config;

import com.xjinyao.xcloud.admin.api.feign.factory.*;
import com.xjinyao.xcloud.admin.api.feign.fallback.*;
import com.xjinyao.xcloud.admin.api.feign.factory.*;
import com.xjinyao.xcloud.admin.api.feign.fallback.*;
import org.springframework.context.annotation.Bean;

/**
 * 远程服务配置
 *
 * @author 谢进伟
 * @createDate 2022/11/4 14:27
 */
public class RemoteServiceConfig {

    @Bean
    public RemoteApplicationServiceFallbackImpl remoteApplicationServiceFallback() {
        return new RemoteApplicationServiceFallbackImpl();
    }

    @Bean
    public RemoteApplicationFallbackFactory remoteApplicationFallbackFactory() {
        return new RemoteApplicationFallbackFactory();
    }

    @Bean
    public RemoteDictServiceFallbackImpl remoteDictServiceFallbackImpl() {
        return new RemoteDictServiceFallbackImpl();
    }

    @Bean
    public RemoteDictServiceFallbackFactory remoteDictServiceFallbackFactory() {
        return new RemoteDictServiceFallbackFactory();
    }

    @Bean
    public RemoteLogServiceFallbackImpl remoteLogServiceFallback() {
        return new RemoteLogServiceFallbackImpl();
    }

    @Bean
    public RemoteLogServiceFallbackFactory remoteLogServiceFallbackFactory() {
        return new RemoteLogServiceFallbackFactory();
    }

    @Bean
    public RemoteOrganizationServiceFallbackImpl remoteOrganizationServiceFallback() {
        return new RemoteOrganizationServiceFallbackImpl();
    }

    @Bean
    public RemoteOrganizationServiceFallbackFactory remoteOrganizationServiceFallbackFactory() {
        return new RemoteOrganizationServiceFallbackFactory();
    }

    @Bean
    public RemoteSysSequenceServiceFallbackImpl remoteSysSequenceServiceFallback() {
        return new RemoteSysSequenceServiceFallbackImpl();
    }

    @Bean
    public RemoteSysSequenceServiceFallbackFactory remoteSysSequenceServiceFallbackFactory() {
        return new RemoteSysSequenceServiceFallbackFactory();
    }

    @Bean
    public RemoteSysTasksServiceFallbackImpl remoteSysTasksServiceFallback() {
        return new RemoteSysTasksServiceFallbackImpl();
    }

    @Bean
    public RemoteSysTasksServiceFallbacFactory remoteSysTasksServiceFallbacFactory() {
        return new RemoteSysTasksServiceFallbacFactory();
    }

    @Bean
    public RemoteTokenServiceFallbackImpl remoteTokenServiceFallback() {
        return new RemoteTokenServiceFallbackImpl();
    }

    @Bean
    public RemoteTokenServiceFallbackFactory remoteTokenServiceFallbackFactory() {
        return new RemoteTokenServiceFallbackFactory();
    }

    @Bean
    public RemoteUserServiceFallbackImpl remoteUserServiceFallback() {
        return new RemoteUserServiceFallbackImpl();
    }

    @Bean
    public RemoteUserServiceFallbackFactory remoteUserServiceFallbackFactory() {
        return new RemoteUserServiceFallbackFactory();
    }

    @Bean
    public RemoteSysBusinessLogServiceFallbackFactory remoteSysBusinessLogServiceFallbackFactory() {
        return new RemoteSysBusinessLogServiceFallbackFactory();
    }

    @Bean
    public RemoteSysBusinessLogServiceFallbackImpl remoteSysBusinessLogServiceFallback() {
        return new RemoteSysBusinessLogServiceFallbackImpl();
    }
}
