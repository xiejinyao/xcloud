package com.xjinyao.xcloud.common.core.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @date 2019/2/1 Spring 工具类
 */
@Slf4j
@Service
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		return (T) applicationContext.getBean(beanName);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> Collection<T> getBeans(Class<T> requiredType) {
		return Optional.of(applicationContext.getBeansOfType(requiredType, true, true))
				.orElse(Collections.emptyMap()).values();
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 判断spring容器中是否存在指定光明村的bean
	 *
	 * @param beanName
	 * @return
	 */
	public static boolean containsBean(String beanName) {
		return applicationContext.containsBean(beanName);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		if (log.isDebugEnabled()) {
			log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		applicationContext = null;
	}

	/**
	 * 发布事件
	 *
	 * @param event
	 */
	public static void publishEvent(ApplicationEvent event) {
		if (applicationContext == null) {
			return;
		}
		applicationContext.publishEvent(event);
	}

	/**
	 * 动态注册bean
	 *
	 * @param beanName 注册到spring容器中的名称
	 * @param bean     需要注册的bean
	 * @param <T>      注册成功之后再spring容器中的对象
	 * @return
	 */
	public static <T> T registerBean(String beanName, T bean) {
		if (containsBean(beanName)) {
			return getBean(beanName);
		}
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) SpringContextHolder.applicationContext;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

		BeanDefinition beanDefinition = new RootBeanDefinition(bean.getClass(), ConfigurableBeanFactory.SCOPE_SINGLETON,
				(Supplier) () -> bean);

		beanFactory.registerBeanDefinition(beanName, beanDefinition);

		T registerBean = getBean(beanName);
		log.info("{} bean {} register success!", beanName, registerBean);

		return registerBean;
	}

	/**
	 * 动态注册bean
	 *
	 * @param beanName 注册到spring容器中的名称
	 * @return
	 */
	public static boolean unregisterBean(String beanName) {
		if (containsBean(beanName)) {
			ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
			String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
			if (ArrayUtils.contains(beanDefinitionNames, beanName)) {
				beanFactory.removeBeanDefinition(beanName);
			}
			log.info("{} bean unregister success!", beanName);
			return true;
		}
		return false;
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	@SneakyThrows
	public void destroy() {
		SpringContextHolder.clearHolder();
	}

}
