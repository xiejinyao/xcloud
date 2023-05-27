package com.xjinyao.xcloud.xxl.job.admin.api.config;

import com.xjinyao.xcloud.xxl.job.admin.api.feign.factory.RemoteJobInfoServiceFallbackFactory;
import com.xjinyao.xcloud.xxl.job.admin.api.feign.fallback.RemoteJobInfoServiceFallbackImpl;
import org.springframework.context.annotation.Bean;

/**
 * 远程服务配置
 *
 * @author 谢进伟
 * @createDate 2022/11/4 14:27
 */
public class RemoteServiceConfig {

    @Bean
    public RemoteJobInfoServiceFallbackImpl remoteJobInfoServiceFallbackImpl() {
        return new RemoteJobInfoServiceFallbackImpl();
    }

    @Bean
    public RemoteJobInfoServiceFallbackFactory remoteJobInfoServiceFallbackFactory() {
        return new RemoteJobInfoServiceFallbackFactory();
    }

}
