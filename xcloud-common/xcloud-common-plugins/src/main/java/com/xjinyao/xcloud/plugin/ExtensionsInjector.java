package com.xjinyao.xcloud.plugin;

import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtensionsInjector {

    private static final Logger log = LoggerFactory.getLogger(ExtensionsInjector.class);

    protected final SpringPluginManager springPluginManager;
    protected final AbstractAutowireCapableBeanFactory beanFactory;

    public ExtensionsInjector(SpringPluginManager springPluginManager, AbstractAutowireCapableBeanFactory beanFactory) {
        this.springPluginManager = springPluginManager;
        this.beanFactory = beanFactory;
    }

    public void injectExtensions(String pluginId) {
        // add extensions from classpath (non plugin)
        Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(null);
        for (String extensionClassName : extensionClassNames) {
            try {
                log.info("Register extension '{}' as bean", extensionClassName);
                Class<?> extensionClass = getClass().getClassLoader().loadClass(extensionClassName);
                registerExtension(extensionClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }

        // add extensions for each started plugin
        List<PluginWrapper> startedPlugins = springPluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            if (!plugin.getPluginId().equals(pluginId)) {
                continue;
            }
            log.info("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
            extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
            for (String extensionClassName : extensionClassNames) {
                try {
                    log.info("Register extension '{}' as bean", extensionClassName);
                    Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
                    registerExtension(extensionClass);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Register an extension as bean.
     * Current implementation register extension as singleton using {@code beanFactory.registerSingleton()}.
     * The extension instance is created using {@code pluginManager.getExtensionFactory().create(extensionClass)}.
     * The bean name is the extension class name.
     * Override this method if you wish other register strategy.
     */
    protected void registerExtension(Class<?> clz) {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(clz);
        if (extensionBeanMap.isEmpty()) {
            Object extension = springPluginManager.getExtensionFactory().create(clz);
            if (beanFactory.containsSingleton(clz.getName())) {
                beanFactory.destroySingleton(clz.getName());
            }
            beanFactory.registerSingleton(clz.getName(), extension);
        } else {
            log.info("Bean registeration aborted! Extension '{}' already existed as bean!", clz.getName());
        }
    }

}
