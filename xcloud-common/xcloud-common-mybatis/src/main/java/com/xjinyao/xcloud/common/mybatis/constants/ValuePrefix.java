package com.xjinyao.xcloud.common.mybatis.constants;

/**
 * @description 参数值特殊前缀，这些前缀将将决定分隔符后面的值仪怎样的规则进行查询
 * @createDate 2020/5/8 11:04
 */
public class ValuePrefix {

    /**
     * 全等比较
     * 例如：
     * 传入参数 code 的值为：=001
     * 最后sql条件为：WHERE code=001
     */
    public static final String EQ = "=";
    /**
     * 不等比较
     * 例如：
     * 传入参数 code 的值为：!=001
     * 最后sql条件为：WHERE code!=001
     */
    public static final String NE = "!=";

    /**
     * 大于比较
     * 例如：
     * 传入参数 money 的值为：>100
     * 最后sql条件为：WHERE money>100
     */
    public static final String GT = ">";
    /**
     * 小于于比较
     * 例如：
     * 传入参数 money 的值为：<100
     * 最后sql条件为：WHERE money<100
     */
    public static final String LT = "<";
    /**
     * 大于等于比较
     * 例如：
     * 传入参数 money 的值为：>=100
     * 最后sql条件为：WHERE money>=100
     */
    public static final String GE = ">=";
    /**
     * 小于等于比较
     * 例如：
     * 传入参数 money 的值为：<=100
     * 最后sql条件为：WHERE money<=100
     */
    public static final String LE = "<=";

    /**
     * 值为空
     * 例如：
     * 传入参数 name 的值为：=NULL
     * 最后sql条件为：WHERE name IS NULL
     */
    public static final String IS_NULL = "=NULL";
    /**
     * 值为空
     * 例如：
     * 传入参数 name 的值为：!=NULL
     * 最后sql条件为：WHERE name IS NOT NULL
     */
    public static final String NOT_NULL = "!=NULL";

    /**
     * 值为空字符串
     * 例如：
     * 传入参数 name 的值为：=NULL
     * 最后sql条件为：WHERE name IS NULL
     */
    public static final String IS_EMPTY = "=empty";
    /**
     * 值为空字符串
     * 例如：
     * 传入参数 name 的值为：!=NULL
     * 最后sql条件为：WHERE name IS NOT NULL
     */
    public static final String NOT_EMPTY = "!=empty";
}
