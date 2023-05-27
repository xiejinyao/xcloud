package com.xjinyao.xcloud.mq;

import com.xjinyao.xcloud.mq.api.consts.ExchangeNames;
import com.xjinyao.xcloud.mq.api.consts.RoutingKeys;
import com.xjinyao.xcloud.mq.api.queue.QueueService;
import org.junit.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author 谢进伟
 * @description 队列测试
 * @createDate 2020/11/12 16:34
 */
public class MqTest {

    @Test
    public void t1() {
        QueueService queueService = getQueueService();

//        queueService.sendToExchange(ExchangeNames.DEVICE_DATA_EXCHANGE, null, "{\"sensorNo\":\"l1_sw_1\",\"checkExistsData\":true,\"modelCode\":\"zhongli-shenzhen\",\"checkConditionColumns\":[],\"deviceNo\":\"e7f41800be5c4ab7a18cbd84daf1d70b\",\"body\":{\"sensorNo\":\"l1_sw_1\",\"node\":1,\"dispsY\":-1.33,\"time\":\"2021-08-24 15:25:23\"},\"ruleId\":\"zhongli-ruleId\",\"tableName\":\"data_l1_sw\"}");

        System.out.println("发送完成!");
    }

    private QueueService getQueueService() {
        return new QueueService(getRabbitmqTemplate());
    }

    private RabbitTemplate getRabbitmqTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123456");
        connectionFactory.setVirtualHost("/xcloud");

        connectionFactory.addConnectionListener(connection -> System.out.println("connection success"));

        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

}
