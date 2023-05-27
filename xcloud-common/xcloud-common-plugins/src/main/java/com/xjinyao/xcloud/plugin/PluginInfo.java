package com.xjinyao.xcloud.plugin;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 插件信息
 * @createDate 2021/3/26 15:35
 */
@Data
public class PluginInfo implements Serializable {

    /**
     * 业务类型
     */
    private String type;
    /**
     * 插件Id
     */
    private String pluginId;
    /**
     * 插件远程地址
     */
    private String remoteUrl;
    /**
     * 插件本地相对路径（相对于${@link SpringPluginManager#pluginsRoot}）
     */
    private String localRelativePath;
    /**
     * 插件版本
     */
    private String version;

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof PluginInfo) {
            return StringUtils.equals(this.getPluginId(), ((PluginInfo) anObject).getPluginId());
        }
        return false;
    }
}
