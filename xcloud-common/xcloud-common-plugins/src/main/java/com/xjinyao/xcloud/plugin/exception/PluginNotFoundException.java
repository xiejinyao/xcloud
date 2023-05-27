package com.xjinyao.xcloud.plugin.exception;

/**
 * @author 谢进伟
 * @description 插件未找到异常
 * @createDate 2021/4/7 10:15
 */
public class PluginNotFoundException extends Exception {

    private String pluginId;

    public PluginNotFoundException(String pluginId) {
        super("plugin " + pluginId + " is not found!");
        this.pluginId = pluginId;
    }

}
