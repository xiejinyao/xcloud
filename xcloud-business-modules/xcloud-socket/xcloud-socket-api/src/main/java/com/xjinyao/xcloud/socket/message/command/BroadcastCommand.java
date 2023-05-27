package com.xjinyao.xcloud.socket.message.command;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

/**
 * @author 谢进伟
 * @description 广播命令消息
 * @createDate 2020/6/27 18:52
 */
@Data
@SuperBuilder
@ApiModel(value = "广播命令消息", parent = BaseCommand.class, description = "所有客户端都可以收到该命令消息")
public class BroadcastCommand extends BaseCommand {

    @Tolerate
    public BroadcastCommand() {

    }

    @Override
    public String toString() {
        return "BroadcastCommand{" +
                "msgId='" + msgId + '\'' +
                ", command='" + command + '\'' +
                ", data='" + data + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
