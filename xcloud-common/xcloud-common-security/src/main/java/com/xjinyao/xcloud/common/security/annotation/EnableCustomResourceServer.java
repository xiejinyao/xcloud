package com.xjinyao.xcloud.common.security.annotation;

import com.xjinyao.xcloud.common.security.component.CustomSecurityBeanDefinitionRegistrar;
import com.xjinyao.xcloud.common.security.config.CustomResourceServerAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import java.lang.annotation.*;

/**
 * @date 2019/03/08
 * <p>
 * 资源服务注解
 */
@Documented
@Inherited
@EnableResourceServer
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({CustomResourceServerAutoConfiguration.class, CustomSecurityBeanDefinitionRegistrar.class})
public @interface EnableCustomResourceServer {

}
