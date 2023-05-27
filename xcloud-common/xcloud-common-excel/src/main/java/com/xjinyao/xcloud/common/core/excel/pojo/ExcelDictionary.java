package com.xjinyao.xcloud.common.core.excel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 谢进伟
 * @description Excel数据字典
 * @createDate 2020/8/20 9:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDictionary {

    /**
     * 數字字典的数字
     */
    private Object code;

    /**
     * 数字对应的中文
     */
    private String chinese;
}
