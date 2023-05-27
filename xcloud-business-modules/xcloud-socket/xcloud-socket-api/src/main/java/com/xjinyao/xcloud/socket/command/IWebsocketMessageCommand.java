package com.xjinyao.xcloud.socket.command;

/**
 * @author 谢进伟
 * @description websocket消息命令
 * @createDate 2022/1/18 17:07
 */
public interface IWebsocketMessageCommand {

    /**
     * 获取命令
     *
     * @return
     */
    String getCommand();

    /**
     * 获取命令备注
     *
     * @return
     */
    String getRemark();
}
