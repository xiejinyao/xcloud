package com.xjinyao.xcloud.common.core.annotations;

import java.lang.annotation.*;

/**
 * 默认搜索模式
 *
 * @author 谢进伟
 * @createDate 2022/11/8 15:12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DefaultSearchMode {


    /**
     * 搜索模式
     *
     * @return
     */
    SearchMode value() default SearchMode.LIKE;

    enum SearchMode {
        LIKE,
        EQUALS
    }
}
