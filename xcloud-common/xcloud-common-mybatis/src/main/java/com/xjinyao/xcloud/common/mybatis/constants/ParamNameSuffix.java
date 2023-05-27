package com.xjinyao.xcloud.common.mybatis.constants;

/**
 * @description 参数名特殊后缀，没有后缀的参数名将直接根据参数名进行模糊匹配
 * @createDate 2020/5/8 11:04
 */
public class ParamNameSuffix {
    public static final String DEFAULT_IN_SEPARATOR = ",";
    /**
     * 枚举类型匹配模式下的值分隔符
     */
    public static final String IN_SEPARATOR = "_in_separator";
    /**
     * 固定枚举值匹配
     * 例如：
     * 实体类中存在字段：status
     * 传入参数 status_in 的值为：1,2,3
     * 最后sql条件为：WHERE status IN('1','2','3')
     */
    public static final String IN = "_in";
    /**
     * 固定枚举值匹配取反
     * 例如：
     * 实体类中存在字段：status
     * 传入参数 status_not_in 的值为：1,2,3
     * 最后sql条件为：WHERE status NOT IN('1','2','3')
     */
    public static final String NOT_IN = "_not_in";
    /**
     * 固定枚举值匹配,多个分割值之间或关系
     * 例如：
     * 实体类中存在字段：status
     * 传入参数 status_or_like 的值为：1,2,3
     * 最后sql条件为：WHERE ( status LIKE('%1%') OR  status LIKE('%2%') OR  status LIKE('%3%')))
     */
    public static final String ORELIKE = "_or_like";
    /**
     * 固定枚举值匹配,多个分割值之间且关系
     * 例如：
     * 实体类中存在字段：status
     * 传入参数 status_and_like 的值为：1,2,3
     * 最后sql条件为：WHERE ( status LIKE('%1%') AND  status LIKE('%2%') AND  status LIKE('%3%')))
     */
    public static final String ANDELIKE = "_and_like";
    /**
     * 范围查询起始范围值（包含临界值）
     * 例如：
     * 实体类中存在字段：createTime
     * 传入参数 createTime.begin 的值为：2020-05-08 10:36:86.00
     * 最后sql条件为：WHERE createTime >='2020-05-08 10:36:86.00'
     */
    public static final String BEGIN = ".begin";
    /**
     * 范围查询结束范围值(包含临界值)
     * 例如：
     * 实体类中存在字段：createTime
     * 传入参数 createTime.end 的值为：2020-05-08 10:36:86.00
     * 最后sql条件为：WHERE createTime <='2020-05-08 10:36:86.00'
     */
    public static final String END = ".end";
    /**
     * 范围查询起始范围值（包含临界值）
     * 例如：
     * 实体类中存在字段：createTime
     * 传入参数 createTime_begin 的值为：2020-05-08 10:36:86.00
     * 最后sql条件为：WHERE createTime >='2020-05-08 10:36:86.00'
     */
    public static final String BEGIN_1 = "_begin";
    /**
     * 范围查询结束范围值(包含临界值)
     * 例如：
     * 实体类中存在字段：createTime
     * 传入参数 createTime_end 的值为：2020-05-08 10:36:86.00
     * 最后sql条件为：WHERE createTime <='2020-05-08 10:36:86.00'
     */
    public static final String END_1 = "_end";
}
