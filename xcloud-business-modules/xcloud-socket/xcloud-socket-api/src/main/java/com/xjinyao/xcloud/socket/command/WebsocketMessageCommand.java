package com.xjinyao.xcloud.socket.command;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author 谢进伟
 * @description websocket消息命令实现
 * @createDate 2022/1/18 17:14
 */
@Data
@Builder
public class WebsocketMessageCommand implements IWebsocketMessageCommand {

    /**
     * 命令
     */
    private String command;

    /**
     * 备注
     */
    private String remark;

    @Tolerate
    public WebsocketMessageCommand() {

    }
}
