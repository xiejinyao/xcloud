package com.xjinyao.xcloud.core.rule;

import org.springframework.http.HttpMethod;

/**
 * @author 谢进伟
 * @description 规则工具类
 * @createDate 2020/11/18 17:21
 */
public class RuleUtil {

    public static String getBasePattern(String url) {
        return url.replaceAll("\\{(.*?)\\}", "*");
    }

    public static String getApiPattern(String basePattern, HttpMethod methodEnum) {
        return methodEnum + ":" + basePattern;
    }

}
