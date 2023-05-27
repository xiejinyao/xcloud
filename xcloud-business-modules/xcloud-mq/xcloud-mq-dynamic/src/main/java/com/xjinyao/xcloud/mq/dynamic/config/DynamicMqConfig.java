package com.xjinyao.xcloud.mq.dynamic.config;

import com.xjinyao.xcloud.mq.dynamic.service.IDynamicMqService;
import com.xjinyao.xcloud.mq.dynamic.service.Impl.DynamicRabbitmqService;
import org.springframework.context.annotation.Bean;

/**
 * @author 谢进伟
 * @description
 * @createDate 2021/7/5 15:05
 */
public class DynamicMqConfig {

    @Bean
    public IDynamicMqService dynamicMqService() {
        return new DynamicRabbitmqService();
    }
}
