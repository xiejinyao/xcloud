package com.xjinyao.xcloud.common.mybatis.annotations;

import com.xjinyao.xcloud.common.mybatis.interceptor.CommonFieldSetterInnerInterceptor;

import java.lang.annotation.*;

/**
 * 自动设置值，作用到拦截器 {@link CommonFieldSetterInnerInterceptor}
 *
 * @author 谢进伟
 * @createDate 2023/2/13 18:48
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface AutoSetterValue {

    /**
     * 是否自动设置值
     *
     * @return boolean
     */
    boolean value() default true;
}
