package com.xjinyao.xcloud.common.core.excel.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 谢进伟
 * @description Excel 头信息
 * @createDate 2020/8/20 9:43
 */
@Data
@NoArgsConstructor
public class ExcelHeard {
    /**
     * excel标题
     **/
    private String columnName;

    /**
     * 对应的java属性
     **/
    private String javaField;
    /**
     * 是否是必须的
     */
    private Boolean required;

    /**
     * 属性对应的数字字典
     */
    private List<ExcelDictionary> dictionary;

    /**
     * 字段对应的列的类型
     */
    private String columnType;

    /**
     * 匹配的正则表达式
     */
    private String pattern;

    /**
     * 匹配错误的信息
     */
    private String matchErrorMessage;
}
