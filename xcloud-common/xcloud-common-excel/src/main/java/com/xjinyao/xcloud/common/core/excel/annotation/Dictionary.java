package com.xjinyao.xcloud.common.core.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 谢进伟
 * @description Excel导入过程抽象
 * @createDate 2021/03/10 14:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Dictionary {

    /**
     * 中文
     *
     * @return
     */
    String chinese() default "";

    /**
     * 编码
     *
     * @return
     */
    String code() default "";
}
