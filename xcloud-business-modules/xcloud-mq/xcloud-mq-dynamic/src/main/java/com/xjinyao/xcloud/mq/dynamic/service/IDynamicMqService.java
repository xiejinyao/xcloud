package com.xjinyao.xcloud.mq.dynamic.service;

import cn.hutool.core.lang.UUID;
import com.xjinyao.xcloud.mq.dynamic.properties.MqProperties;

/**
 * @author 谢进伟
 * @description 动态mq配置服务，提供服务的注册、取消注册、更新注册信等息接口
 * @createDate 2020/12/17 15:46
 */
public interface IDynamicMqService {

    String BEAN_UNIQUE_IDENTIFER = UUID.fastUUID().toString().replaceAll("-", "");

    /**
     * 手动将 mq 操作相关 bean实列 放入Spring容器中
     *
     * @param code         实列编码
     * @param mqProperties 配置参数
     * @return
     */
    boolean register(String code, MqProperties mqProperties);

    /**
     * 更新手动放入Spring容器中的 mq 操作相关 bean实列
     *
     * @param code
     * @param mqProperties
     * @return
     */
    boolean update(String code, MqProperties mqProperties);

    /**
     * 取消注册 手动放入Spring容器中的 mq 操作相关 bean实列
     *
     * @param code 容器中的bean名称
     * @return
     */
    boolean unregister(String code);

    /**
     * 用指定编码的mq操作实例发送信息
     *
     * @param code    注册时使用的rabbitmq实列编码
     * @param message 消息内容
     * @return
     */
    boolean sendMsg(String code, String exchange, String routingKey, String message);

    /**
     * 获取mq操作实例名称
     *
     * @param code 编码
     * @return
     */
    String getCustomRegisteredMqTemplateBeanName(String code);
}
