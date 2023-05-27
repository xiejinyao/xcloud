package com.xjinyao.xcloud.common.swagger.annotation;

import java.lang.annotation.*;

/**
 * 新增默认值
 *
 * @author 谢进伟
 * @createDate 2022/12/6 08:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AddDefaultValue {

    /**
     * 默认值
     *
     * @return {@link String}
     */
    String value();
}
