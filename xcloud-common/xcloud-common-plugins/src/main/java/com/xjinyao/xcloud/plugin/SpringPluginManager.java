package com.xjinyao.xcloud.plugin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.xjinyao.xcloud.common.core.util.FileSizeUtil;
import com.xjinyao.xcloud.plugin.exception.PluginNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.pf4j.util.FileUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class SpringPluginManager extends DefaultPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String localPluginRepository;

    public SpringPluginManager() {
    }

    public SpringPluginManager(Path pluginsRoot) {
        super(pluginsRoot);
        this.localPluginRepository = pluginsRoot.toFile().getPath();
        final File pluginsRootFile = new File(localPluginRepository);
        if (!pluginsRootFile.exists()) {
            pluginsRootFile.mkdir();
        }
    }


    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 删除插件
     *
     * @param pluginInfo 插件信息
     */
    public void deletePlugin(PluginInfo pluginInfo) {
        try {
            this.stopPlugin(pluginInfo.getPluginId());
            this.disablePlugin(pluginInfo.getPluginId());
            FileUtils.optimisticDelete(Paths.get(getPluginLocalPath(pluginInfo)));
            log.info("delete plugin file：{}", pluginInfo);
        } catch (Exception e) {
            log.error("delete plugin failed!", e);
        }
    }

    /**
     * 新增插件
     *
     * @param pluginInfo 插件信息
     */
    public void addPlugin(PluginInfo pluginInfo) {
        //此处暂不做任何处理
        log.info("add plugin：{}", pluginInfo);
    }

    /**
     * 更新插件
     *
     * @param pluginInfo 插件信息
     */
    public void updatePlugin(PluginInfo pluginInfo) {
        //此处暂不做任何处理
        log.info("update plugin：{}", pluginInfo);
    }

    /**
     * 根据插件相对位置启动单个个插件
     *
     * @param pluginInfo 插件信息
     * @return
     */
    public PluginState startPlugin(PluginInfo pluginInfo) {
        log.info("正在尝试启动插件：{}", pluginInfo);
        String remoteUrl = pluginInfo.getRemoteUrl();
        if (StringUtils.isNotBlank(remoteUrl)) {
            //下载最新插件
            downloadPlugin(pluginInfo);
        }
        //加载并启动插件
        Path pluginPath = Paths.get(getPluginLocalPath(pluginInfo));
        log.info("插件位置：{}", pluginPath);
        String pluginId = pluginInfo.getPluginId();
        if (!plugins.containsKey(pluginId)) {
            pluginId = loadPlugin(pluginPath);
        }
        PluginState pluginState = startPlugin(pluginId);
        log.info("插件启动结果：{}", pluginState);
        injectExtensionsToSpring(pluginId);
        return pluginState;
    }

    /**
     * 加载插件
     *
     * @param pluginInfoList 插件信息集合
     */
    public void startPlugins(List<PluginInfo> pluginInfoList) {
        if (CollectionUtil.isNotEmpty(pluginInfoList)) {
            stopPlugins(pluginInfoList);
            //加载插件
            for (PluginInfo pluginInfo : pluginInfoList) {
                startPlugin(pluginInfo);
            }
        }
    }

    /**
     * 停止插件
     *
     * @param pluginInfoList 插件信息集合
     */
    public void stopPlugins(List<PluginInfo> pluginInfoList) {
        if (CollectionUtil.isNotEmpty(pluginInfoList)) {
            //加载插件
            for (PluginInfo pluginInfo : pluginInfoList) {
                String pluginId = pluginInfo.getPluginId();
                //插件已加
                if (plugins.containsKey(pluginId)) {
                    stopPlugin(pluginId);
                }
            }
        }
    }

    /**
     * 启用插件
     *
     * @param pluginInfo 插件信息
     * @return
     */
    public boolean enablePlugin(PluginInfo pluginInfo) throws PluginNotFoundException {
        String pluginId = pluginInfo.getPluginId();
        if (!plugins.containsKey(pluginId)) {
            throw new PluginNotFoundException(pluginId);
        }
        return super.enablePlugin(pluginId);
    }

    /**
     * 禁用插件
     *
     * @param pluginInfo 插件信息
     * @return
     */
    public boolean disablePlugin(PluginInfo pluginInfo) throws PluginNotFoundException {
        String pluginId = pluginInfo.getPluginId();
        if (!plugins.containsKey(pluginId)) {
            throw new PluginNotFoundException(pluginId);
        }
        PluginWrapper pluginWrapper = plugins.get(pluginId);
        if (pluginWrapper.getPluginState().equals(PluginState.STARTED)) {
            stopPlugin(pluginId);
        }
        if (super.disablePlugin(pluginId)) {
            return super.unloadPlugin(pluginId);
        }
        return false;
    }

    /**
     * 获取插件Wrapper
     *
     * @param pluginInfo 插件信息
     * @return
     */
    public PluginWrapper getPluginWrapper(PluginInfo pluginInfo) {
        String pluginId = pluginInfo.getPluginId();
        if (!plugins.containsKey(pluginId)) {
            downloadPlugin(pluginInfo, false);
            pluginId = loadPlugin(Paths.get(getPluginLocalPath(pluginInfo)));
        }
        return plugins.get(pluginId);
    }

    /**
     * 将插件类注入到Spring容器中
     *
     * @param pluginId 插件id
     */
    private void injectExtensionsToSpring(String pluginId) {
        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
        ExtensionsInjector extensionsInjector = new ExtensionsInjector(this, beanFactory);
        extensionsInjector.injectExtensions(pluginId);
    }


    /**
     * 下载远程插件仓库中的插件
     *
     * @param pluginInfo 插件信息
     */
    private void downloadPlugin(PluginInfo pluginInfo) {
        downloadPlugin(pluginInfo, true);
    }

    /**
     * 下载远程插件仓库中的插件
     *
     * @param pluginInfo 插件信息
     * @param override   覆盖本地文件
     */
    private void downloadPlugin(PluginInfo pluginInfo, boolean override) {
        String remoteUrl = pluginInfo.getRemoteUrl();
        if (remoteUrl.contains("?")) {
            remoteUrl = remoteUrl + "&";
        } else {
            remoteUrl = remoteUrl + "?";
        }
        String url = remoteUrl + "uuid=" + UUID.fastUUID().toString();
        File destFile = new File(getPluginLocalPath(pluginInfo));
        if (destFile.exists()) {
            if (override) {
                org.apache.commons.io.FileUtils.deleteQuietly(destFile);
            } else {
                return;
            }
        }
        try {
            HttpUtil.downloadFileFromUrl(url, destFile, 10 * 60 * 1000, new StreamProgress() {
                @Override
                public void start() {
                    log.info("开始下载远程插件库中插件：{}", url);
                }

                @Override
                public void progress(long progressSize) {
                    log.info("已下载：" + FileSizeUtil.getNetFileSizeDescription(progressSize));
                }

                @Override
                public void finish() {
                    log.info("插件下载完成，存储位置：" + destFile.getPath());
                }
            });
        } catch (Exception e) {
            log.error("插件下载失败,请检查插件下载路径是否可以正常访问,插件下载地址:{}", url);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getPluginLocalPath(PluginInfo pluginInfo) {
        String pluginName = StringUtils.substringAfterLast(pluginInfo.getLocalRelativePath(), com.xjinyao.xcloud.common.core.util.StringUtils.SLASH_SEPARATOR);
        return localPluginRepository + File.separator + pluginInfo.getVersion() + File.separator + pluginName;
    }
}
