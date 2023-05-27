package com.xjinyao.xcloud.common.core.annotations;

import java.lang.annotation.*;

/**
 * 文件注解
 *
 * @author 谢进伟
 * @createDate 2022/11/8 15:48
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SysFileInfo {

    /**
     * 存储文件id的字段名称
     *
     * @return
     */
    String value() default "";
}
