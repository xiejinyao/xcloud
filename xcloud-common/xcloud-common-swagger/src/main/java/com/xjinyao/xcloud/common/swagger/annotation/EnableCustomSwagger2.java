package com.xjinyao.xcloud.common.swagger.annotation;

import com.xjinyao.xcloud.common.swagger.config.GatewaySwaggerAutoConfiguration;
import com.xjinyao.xcloud.common.swagger.config.SwaggerAutoConfiguration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * @date 2020/10/2 开启 swagger
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableSwagger2
@Import({SwaggerAutoConfiguration.class, GatewaySwaggerAutoConfiguration.class})
public @interface EnableCustomSwagger2 {

}
