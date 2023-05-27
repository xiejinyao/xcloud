package com.xjinyao.xcloud.mq.dynamic.service.Impl;

import com.xjinyao.xcloud.common.core.util.SpringContextHolder;
import com.xjinyao.xcloud.mq.dynamic.properties.MqProperties;
import com.xjinyao.xcloud.mq.dynamic.properties.RabbitmqProperties;
import com.xjinyao.xcloud.mq.dynamic.service.IDynamicMqService;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Primary;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 谢进伟
 * @description 动态Rabbitmq配置服务
 * @createDate 2020/11/4 17:20
 */
@Slf4j
@Primary
public class DynamicRabbitmqService implements IDynamicMqService {

    public static final String RABBIT_TEMPLATE_BEAN_PREFIX = "config_";
    public static final String RABBIT_TEMPLATE_SUFFIX = "_rabbitTemplate";
    public static final String RABBIT_ADMIN_SUFFIX = "_rabbitAdmin";

    /**
     * 连接缓存
     */
    private final ConcurrentHashMap<String, List<Connection>> rabbitmqConnectionMap = new ConcurrentHashMap<>();

    /**
     * 自动重新注册处理器映射
     */
    private final ConcurrentHashMap<String, Disposable> disposables = new ConcurrentHashMap<>();


    /**
     * 手动将RabbitmqTemplate、RabbitAdmin bean实列 放入Spring容器中
     *
     * @param code         实列编码
     * @param mqProperties 配置参数
     * @return
     */
    @Override
    public boolean register(String code, MqProperties mqProperties) {
        RabbitmqProperties properties = (RabbitmqProperties) mqProperties;
        String rabbitmqTemplateBeanName = getCustomRegisteredMqTemplateBeanName(code);
        if (SpringContextHolder.containsBean(rabbitmqTemplateBeanName)) {
            return true;
        }
        try {
            RabbitTemplate rabbitTemplate = getRabbitTemplate(code, rabbitmqTemplateBeanName, properties);
            if (rabbitTemplate == null) {
                return false;
            }
            String exchangeType = properties.getExchangeType();
            String[] supperExchangeTypes = {ExchangeTypes.DIRECT, ExchangeTypes.TOPIC, ExchangeTypes.FANOUT};
            if (!ArrayUtils.contains(supperExchangeTypes, exchangeType)) {
                return false;
            }
            String queueName = properties.getQueueName();
            String exchangeName = properties.getExchangeName();
            CustomRabbitAdmin rabbitAdmin = new CustomRabbitAdmin(rabbitTemplate);
            rabbitAdmin.setBeanName(code + RABBIT_ADMIN_SUFFIX);

            declareExchangeAndQueue(rabbitAdmin, exchangeType, exchangeName, queueName, properties.getRoutingKey());

            rabbitTemplate.setExchange(exchangeName);
            rabbitTemplate.setRoutingKey(properties.getRoutingKey());

            SpringContextHolder.registerBean(rabbitmqTemplateBeanName, rabbitTemplate);
            SpringContextHolder.registerBean(rabbitAdmin.getBeanName(), rabbitAdmin);
            log.info("Rabbitmq {} 注册成功!", rabbitmqTemplateBeanName);

            Disposable disposable = disposables.get(code);
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            return true;
        } catch (Exception e) {
            log.error("Rabbitmq {} 注册失败(连接参数无效)!,RabbitmqProperties:{}", rabbitmqTemplateBeanName, properties);
        }
        return false;
    }

    /**
     * 声明交换机和队列
     *
     * @param rabbitAdmin  rabbitmq管理工具
     * @param exchangeType 交换机类型
     * @param exchangeName 交换机名称
     * @param queueName    队列名称
     * @param routingKey   队列与交换机之间的绑定路由
     */
    private void declareExchangeAndQueue(CustomRabbitAdmin rabbitAdmin, String exchangeType, String exchangeName,
                                         String queueName, String routingKey) {
        switch (exchangeType) {
            case ExchangeTypes.DIRECT:
                DirectExchange directExchange = new DirectExchange(exchangeName, true, false);
                Queue directQueue = new Queue(queueName, true, false, false);
                Binding directBinding = BindingBuilder.bind(directQueue)
                        .to(directExchange)
                        .with(routingKey);

                rabbitAdmin.declareExchange(directExchange);
                rabbitAdmin.declareQueue(directQueue);
                rabbitAdmin.declareBinding(directBinding);
                break;
            case ExchangeTypes.TOPIC:
                TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);
                Queue topicQueue = new Queue(queueName, true, false, false);
                Binding topicBinding = BindingBuilder.bind(topicQueue)
                        .to(topicExchange)
                        .with(routingKey);

                rabbitAdmin.declareExchange(topicExchange);
                rabbitAdmin.declareQueue(topicQueue);
                rabbitAdmin.declareBinding(topicBinding);
                break;
            case ExchangeTypes.FANOUT:
                FanoutExchange fanoutExchange = new FanoutExchange(exchangeName, true, false);
                Queue fanoutQueue = new Queue(queueName, true, false, false);
                Binding fanoutBinding = BindingBuilder.bind(fanoutQueue)
                        .to(fanoutExchange);

                rabbitAdmin.declareExchange(fanoutExchange);
                rabbitAdmin.declareQueue(fanoutQueue);
                rabbitAdmin.declareBinding(fanoutBinding);
                break;
        }
    }

    /**
     * 更新手动放入Spring容器中的RabbitmqTemplate、RabbitAdmin bean实列
     *
     * @param code         实列编码
     * @param mqProperties 配置参数
     * @return
     */
    @Override
    public boolean update(String code, MqProperties mqProperties) {
        RabbitmqProperties properties = (RabbitmqProperties) mqProperties;
        unregister(code);
        return register(code, properties);
    }

    /**
     * 取消注册 手动放入Spring容器中的RabbitmqTemplate、RabbitAdmin bean实列
     *
     * @param code 实列编码
     * @return
     */
    @Override
    public boolean unregister(String code) {
        String rabbitmqTemplateBeanName = getCustomRegisteredMqTemplateBeanName(code);
        Object bean;
        try {
            bean = SpringContextHolder.getBean(rabbitmqTemplateBeanName);
        } catch (NoSuchBeanDefinitionException e) {
            return true;
        }
        if (bean instanceof RabbitTemplate) {
            List<Connection> connectionList = rabbitmqConnectionMap.get(rabbitmqTemplateBeanName);
            Optional.ofNullable(connectionList).ifPresent(_connectionList -> _connectionList.forEach(connection -> {
                if (connection.isOpen()) {
                    connection.close();
                    log.info("{} connection closed!", rabbitmqTemplateBeanName);
                }
            }));
        }
        if (SpringContextHolder.unregisterBean(rabbitmqTemplateBeanName)) {
            SpringContextHolder.unregisterBean(getCustomRegisteredRabbitAdminBeanName(code));
            disposables.remove(code);
            return true;
        }
        return false;
    }

    /**
     * 用指定编码的RabbitTemplate实例发送信息
     *
     * @param code    注册时使用的rabbitmq实列编码
     * @param message 消息内容
     * @return
     */
    @Override
    public boolean sendMsg(String code, String exchange, String routingKey, String message) {
        RabbitTemplate rabbitTemplate = getRabbitTemplateByCode(code);
        if (rabbitTemplate != null) {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            return true;
        }
        return false;
    }

    /**
     * 获取rabbitTemplate实例名称
     *
     * @param code 编码
     * @return
     */
    @Override
    public String getCustomRegisteredMqTemplateBeanName(String code) {
        return RABBIT_TEMPLATE_BEAN_PREFIX + code + RABBIT_TEMPLATE_SUFFIX
                + "_" + BEAN_UNIQUE_IDENTIFER;
    }


    /**
     * 通过code 获取spring 容器中的 rabbittemplate 实例
     *
     * @param code 注册时使用的rabbitmq实列编码
     * @return
     */
    private RabbitTemplate getRabbitTemplateByCode(String code) {
        String rabbitmqBeanName = getCustomRegisteredMqTemplateBeanName(code);
        if (SpringContextHolder.containsBean(rabbitmqBeanName)) {
            Object rabbitTemplate = SpringContextHolder.getBean(rabbitmqBeanName);
            if (rabbitTemplate != null && rabbitTemplate instanceof RabbitTemplate) {
                return (RabbitTemplate) rabbitTemplate;
            }
        }
        return null;
    }

    /**
     * 获取spring提供的rabbitmq操作工具
     *
     * @param properties rabbitmq连接参数配置
     * @return
     */
    private RabbitTemplate getRabbitTemplate(String code, String rabbitTemplateBeanName, RabbitmqProperties properties) {
        if (StringUtils.isBlank(properties.getHost()) ||
                StringUtils.isBlank(properties.getExchangeName()) ||
                StringUtils.isBlank(properties.getQueueName())) {
            return null;
        }
        return new RabbitTemplate(getConnectionFactory(code, rabbitTemplateBeanName, properties));
    }

    /**
     * 获取rabbitmq连接工厂
     *
     * @param properties rabbitmq连接配置
     * @return
     */
    private ConnectionFactory getConnectionFactory(String code,
                                                   String rabbitTemplateBeanName,
                                                   RabbitmqProperties properties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setConnectionTimeout(5000);
        connectionFactory.setCloseTimeout(5000);
        connectionFactory.setChannelCheckoutTimeout(5000);
        connectionFactory.addConnectionListener(new ConnectionListener() {

            private String _code = code;
            private RabbitmqProperties connConfig = properties;

            @Override
            public void onCreate(Connection connection) {
                log.info("创建Rabbitmq连接成功,连接信息：{}", connConfig);
                rabbitmqConnectionMap.computeIfAbsent(rabbitTemplateBeanName, k -> new ArrayList<>()).add(connection);
            }

            @Override
            public void onClose(Connection connection) {
                log.warn("Rabbitmq连接已关闭,连接信息：{}", connConfig);
                //注销注册
                retryRegister();
            }

            @Override
            public void onShutDown(ShutdownSignalException signal) {
                log.error("Rabbitmq发生异常正在被强制关闭!异常信息：", signal);
                retryRegister();
            }

            /**
             * 重新尝试注册
             */
            private void retryRegister() {
                //注销注册
                unregister(_code);
                //自动重新注册
                autoRegister();
            }

            /**
             * 自动重新注册
             */
            private void autoRegister() {
                disposables.put(_code, Mono.fromRunnable(() -> register(_code, connConfig))
                        .delaySubscription(Duration.ofMinutes(10))
                        .repeat()
                        .doOnError(e -> log.error("自动注册任务出现异常(code:{},config:{})!", code, connConfig, e))
                        .doOnCancel(() -> log.info("自动注册任务正在被取消(code:{},config:{})!", code, connConfig))
                        .doFinally(f -> disposables.remove(_code))
                        .subscribe());
            }

        });
        return connectionFactory;
    }

    /**
     * 获取rabbitAdmin实例名称
     *
     * @param code 编码
     * @return
     */
    private String getCustomRegisteredRabbitAdminBeanName(String code) {
        return RABBIT_TEMPLATE_BEAN_PREFIX + code + RABBIT_ADMIN_SUFFIX
                + "_" + BEAN_UNIQUE_IDENTIFER;
    }

    /**
     * 自定义Rabbitmq进行管理类，主要重写initialize方法从而取消初始化容器中的交换机、队列等信息
     */
    private class CustomRabbitAdmin extends RabbitAdmin {

        public CustomRabbitAdmin(RabbitTemplate rabbitTemplate) {
            super(rabbitTemplate);
        }

        @Override
        public void initialize() {
            logger.info("Nothing to declare");
        }
    }

}
