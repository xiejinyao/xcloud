package com.xjinyao.xcloud.common.job.annotation;

import com.xjinyao.xcloud.common.job.XxlJobAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 激活xxl-job配置
 *
 * @date 2020/9/14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({XxlJobAutoConfiguration.class})
public @interface EnableCustomXxlJob {

}
