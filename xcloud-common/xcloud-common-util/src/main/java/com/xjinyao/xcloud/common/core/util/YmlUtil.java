package com.xjinyao.xcloud.common.core.util;

import cn.hutool.json.JSONUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author 谢进伟
 * @description yml配置文件工具类
 * @createDate 2021/2/5 15:57
 */
public class YmlUtil {

    /**
     * 将一个对象转换成yml配置文件字符串
     *
     * @param propertiesObj 对象
     * @param prefix        前缀
     * @return
     * @throws IllegalAccessException
     */
    public static String toYaml(Object propertiesObj, String prefix) throws IllegalAccessException {
        Class<?> cls = propertiesObj.getClass();
        String tab = "  ";//两个空格
        String blank = " ";//一个空格
        String lineBreak = "\n";
        String dwukropek = ":";
        String listItemPrefix = "-";
        StringBuffer ymlsb = new StringBuffer();
        String[] prefixSplit = prefix.split("\\.");
        int prefixLength = prefixSplit.length;
        for (int i = 0, len = prefixLength; i < len; i++) {
            appendTab(tab, ymlsb, i);
            ymlsb.append(prefixSplit[i]).append(dwukropek).append(lineBreak);
        }
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(propertiesObj);
            appendTab(tab, ymlsb, prefixLength);
            if (value instanceof String || value instanceof Number) {
                ymlsb.append(fieldName)
                        .append(dwukropek)
                        .append(blank)
                        .append(value)
                        .append(lineBreak);
            } else if (value instanceof List) {
                ymlsb.append(fieldName).append(dwukropek).append(lineBreak);
                ((List<?>) value).forEach(v -> {
                            appendTab(tab, ymlsb, (prefix + "." + fieldName).split("\\.").length);
                            ymlsb.append(listItemPrefix)
                                    .append(blank)
                                    .append(JSONUtil.toJsonStr(v))
                                    .append(lineBreak);
                        }
                );
            }
        }
        return ymlsb.toString();
    }

    private static StringBuffer appendTab(String tab, StringBuffer sb, int prefixLength) {
        for (int j = 0; j < prefixLength; j++) {
            sb.append(tab);
        }
        return sb;
    }
}
