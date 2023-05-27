package com.xjinyao.xcloud.socket.message.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

/**
 * @author 谢进伟
 * @description 命令消息
 * @createDate 2020/6/27 18:52
 */
@Data
@SuperBuilder
@ApiModel(value = "单播命令消息", parent = BaseCommand.class, description = "指定的客户端收到命令消息")
public class UnicastCommand extends BaseCommand {

    /**
     * 接收者
     */
    @ApiModelProperty("接收者")
    private String to;

    @Tolerate
    public UnicastCommand() {
    }

    @Override
    public String toString() {
        return "UnicastCommand{" +
                "to='" + to + '\'' +
                ", msgId='" + msgId + '\'' +
                ", command='" + command + '\'' +
                ", data='" + data + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
