package com.xjinyao.xcloud.common.core.excel.converter;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.json.JSONObject;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelDictionary;

/**
 * @author 谢进伟
 * @description Excel字典转换器
 * @createDate 2020/8/20 9:58
 */
public class ExcelDictionaryConverter extends AbstractConverter<ExcelDictionary> {

    @Override
    protected ExcelDictionary convertInternal(Object value) {
        if (value != null) {
            JSONObject j = (JSONObject) value;
            ExcelDictionary excelDictionary = new ExcelDictionary();
            excelDictionary.setChinese(j.getOrDefault("chinese", "").toString());
            excelDictionary.setCode(j.getOrDefault("code", "").toString());
            return excelDictionary;
        }
        return null;
    }
}
