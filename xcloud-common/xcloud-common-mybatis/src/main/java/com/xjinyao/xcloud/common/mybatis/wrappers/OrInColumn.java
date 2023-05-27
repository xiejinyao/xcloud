package com.xjinyao.xcloud.common.mybatis.wrappers;

import lombok.Data;

/**
 * @description
 * @createDate 2020/9/8 16:33
 */
@Data
public class OrInColumn {

    /**
     * 数据库字段名
     */
    private String column;
    /**
     * 值，多个值用separator分割
     */
    private String value;
    /**
     * 分隔符，默认用逗号分割
     */
    private String separator = ",";
}
