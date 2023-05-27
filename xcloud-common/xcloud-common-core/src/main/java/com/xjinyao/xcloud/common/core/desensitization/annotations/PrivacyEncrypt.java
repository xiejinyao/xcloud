package com.xjinyao.xcloud.common.core.desensitization.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xjinyao.xcloud.common.core.desensitization.PrivacySerializer;
import com.xjinyao.xcloud.common.core.desensitization.enums.PrivacyTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 * <p>
 * 只有 type 的值为 PrivacyTypeEnum.CUSTOMER（自定义）时，才需要指定脱敏范围，即 prefixNoMaskLen 和 suffixNoMaskLen 的值，像邮箱、
 * 手机号这种隐藏格式都采用固定的
 *
 * @author 谢进伟
 * @createDate 2023/1/4 18:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = PrivacySerializer.class)
public @interface PrivacyEncrypt {

    /**
     * 脱敏数据类型（没给默认值，所以使用时必须指定type）
     */
    PrivacyTypeEnum type();

    /**
     * 前置不需要打码的长度
     */
    int prefixNoMaskLen() default 1;

    /**
     * 后置不需要打码的长度
     */
    int suffixNoMaskLen() default 1;

    /**
     * 用什么打码
     */
    String symbol() default "*";
}
