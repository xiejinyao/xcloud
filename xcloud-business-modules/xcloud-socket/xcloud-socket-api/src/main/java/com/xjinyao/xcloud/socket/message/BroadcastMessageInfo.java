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
 * @description 广播消息
 * @createDate 2020/6/27 16:36
 */
@Data
@Builder
@ApiModel("广播消息")
public class BroadcastMessageInfo implements Serializable {

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
    private String type = "broadcast";
    /**
     * 标题
     **/
    @ApiModelProperty("标题")
    private String title;
    /**
     * 内容
     **/
    @ApiModelProperty("内容")
    private String content;
    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    @Setter(AccessLevel.PRIVATE)
    private String sendTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    @Tolerate
    public BroadcastMessageInfo() {

    }

    @Override
    public String toString() {
        return "BroadcastMessageInfo{" +
                "msgId='" + msgId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}
