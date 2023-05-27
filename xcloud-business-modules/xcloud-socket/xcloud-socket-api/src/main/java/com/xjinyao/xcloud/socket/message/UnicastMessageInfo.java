package com.xjinyao.xcloud.socket.message;

import cn.hutool.core.lang.UUID;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 谢进伟
 * @description 单播消息
 * @createDate 2020/6/27 16:37
 */
@Data
@Builder
@ApiModel("单播消息")
public class UnicastMessageInfo implements Serializable {

    /**
     * 消息id
     */
    @ApiModelProperty("消息id")
    @Setter(AccessLevel.PRIVATE)
    private String msgId = UUID.fastUUID().toString();
    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String type = "toUser";
    /**
     * 发送者
     */
    @ApiModelProperty("发送者")
    private String from;
    /**
     * 发送者名称
     */
    @ApiModelProperty("发送者名称")
    private String fromName;
    /**
     * 接收者
     */
    @ApiModelProperty("接收者")
    private String to;
    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;
    /**
     * 发送的文本
     */
    @ApiModelProperty("发送的文本")
    private String content;
    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    @Setter(AccessLevel.PRIVATE)
    private String sendTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    @Tolerate
    public UnicastMessageInfo() {

    }

    @Override
    public String toString() {
        return "UnicastMessageInfo{" +
                "msgId='" + msgId + '\'' +
                ", type='" + type + '\'' +
                ", from='" + from + '\'' +
                ", fromName='" + fromName + '\'' +
                ", to='" + to + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                ", msgId='" + msgId + '\'' +
                ", type='" + type + '\'' +
                ", from='" + from + '\'' +
                ", fromName='" + fromName + '\'' +
                ", to='" + to + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}
