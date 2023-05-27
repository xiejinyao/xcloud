package com.xjinyao.xcloud.mq.dynamic.properties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author 谢进伟
 * @description Rabbitmq 队列参数
 * @createDate 2020/11/4 17:33
 */
@Data
@Builder
@ApiModel(value = "Rabbitmq连接配置信息", parent = MqProperties.class)
public class RabbitmqProperties extends MqProperties {

    @ApiModelProperty(value = "连接ip地址", required = true)
    protected String host;

    @ApiModelProperty("端口号，默认值：5672")
    protected Integer port = 5672;

    @ApiModelProperty(value = "用户名", required = true)
    protected String username;

    @ApiModelProperty(value = "密码", required = true)
    protected String password;

    @ApiModelProperty("虚拟主机")
    protected String virtualHost = "/";

    @ApiModelProperty(value = "交换机名称", required = true)
    protected String exchangeName;

    @ApiModelProperty(value = "交换机类型", required = true)
    protected String exchangeType;

    @ApiModelProperty(value = "队列名称", required = true)
    protected String queueName;

    @ApiModelProperty(value = "队列与交换机之间绑定的路由key", required = true)
    protected String routingKey;

    @Tolerate
    public RabbitmqProperties() {

    }
}
