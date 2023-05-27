package com.xjinyao.xcloud.common.job;

import com.xjinyao.xcloud.common.job.properties.XxlExecutorProperties;
import com.xjinyao.xcloud.common.job.properties.XxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * xxl-job自动装配
 *
 * @date 2020/9/14
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("com.xjinyao.xcloud.common.job.properties")
public class XxlJobAutoConfiguration {

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(Environment environment,
                                                     InetUtils inetUtils,
                                                     XxlJobProperties xxlJobProperties) {
        XxlExecutorProperties executorProperties = xxlJobProperties.getExecutor();
        if (StringUtils.isBlank(executorProperties.getIp())) {
            executorProperties.setIp(inetUtils.findFirstNonLoopbackHostInfo().getIpAddress());
        }
        if (StringUtils.isBlank(executorProperties.getAppname())) {
            executorProperties.setAppname(environment.getProperty("spring.application.name"));
        }
        XxlJobSpringExecutor jobExecutor = new XxlJobSpringExecutor();
        jobExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        jobExecutor.setAppname(executorProperties.getAppname());
        jobExecutor.setAddress(executorProperties.getAddress());
        jobExecutor.setIp(executorProperties.getIp());
        jobExecutor.setPort(executorProperties.getPort());
        jobExecutor.setAccessToken(executorProperties.getAccessToken());
        jobExecutor.setLogPath(executorProperties.getLogPath());
        jobExecutor.setLogRetentionDays(executorProperties.getLogRetentionDays());
        return jobExecutor;
    }

}
