package com.xjinyao.xcloud.common.core.excel.annotation;

import java.lang.annotation.*;

/**
 * @author 谢进伟
 * @description Excel文件表格头属性
 * @createDate 2021/03/10 14:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelHeardField {

    /**
     * excel中的列名（中文）
     *
     * @return
     */
    String columnName() default "";

    /**
     * 导出时包含在内
     *
     * @return
     */
    boolean exportInclude() default false;

    /**
     * 当发生错误是，是否可以表示标识当前数据。一般像id、code之类的特殊列可以设置为true
     */
    boolean errorIdentify() default false;

    /**
     * 导入时需要导入该字段
     *
     * @return
     */
    boolean importField() default true;

    /**
     * 导入时是否是必填字段
     *
     * @return
     */
    boolean importRequired() default false;

    /**
     * 数据值需要匹配的正则表达式
     *
     * @return
     */
    String importPattern() default "^.*$";

    /**
     * 当正则表达式匹配失败时的提示信息
     *
     * @return
     */
    String importMatchErrorMessage() default "";

    /**
     * 字典映射
     *
     * @return
     */
    Dictionary[] dictionary() default {};
}
