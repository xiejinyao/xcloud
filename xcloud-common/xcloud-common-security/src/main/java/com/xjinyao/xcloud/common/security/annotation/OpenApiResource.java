package com.xjinyao.xcloud.common.security.annotation;

import java.lang.annotation.*;

/**
 * @author 谢进伟
 * @description 开放API资源注解，当控制器标注此注解时，相关接口将跳过登录权限验证
 * @createDate 2021/2/25 15:11
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenApiResource {

    /**
     * 资源编码
     *
     * @return
     */
    String code();

    /**
     * 接口标题
     *
     * @return
     */
    String title() default "";

    /**
     * 资源描述
     *
     * @return
     */
    String description();
}
