package com.xjinyao.xcloud.common.core.util;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 谢进伟
 * @description 字符串操作工具类
 * @createDate 2020/5/27 15:35
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {


    /**
     * 斜杠分隔符
     */
    public static final String SLASH_SEPARATOR = "/";

    /**
     * 逗号分隔符
     */
    public static final String COMMA_SEPARATOR = ",";

    public static String defaultString(Object str) {
        return str == null ? EMPTY : str.toString();
    }

    public static String defaultString(Object str, Object defaultStr) {
        return str == null ? defaultString(defaultStr) : defaultString(str);
    }

    /**
     * 将字符串默认分隔符分组，并返回Integer类型数组
     *
     * @param str 需要分组的字符串
     * @return
     */
    public static Integer[] splitToInteger(String str) {
        return splitToInteger(str, ",");
    }

    /**
     * 将字符串按指定分隔符分组，并返回Integer类型数组
     *
     * @param str           需要分组的字符串
     * @param separatorChar 分隔符
     * @return
     */
    public static Integer[] splitToInteger(String str, String separatorChar) {
        List<Integer> resultList = new ArrayList<>();
        String[] split = split(str, separatorChar);
        if (split != null && split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                if (NumberUtils.isNumber(split[i])) {
                    resultList.add(Integer.valueOf(split[i]));
                }
            }
        }
        return resultList.toArray(new Integer[0]);
    }

    /**
     * 将字符串根据java变量规则(驼峰式)进行格式化
     *
     * @param str 需要转换的字符串
     * @return
     */
    public static String converToJavaField(String str) {
        return converToJavaVariable(str, true);
    }

    /**
     * 将一个字符串转换成驼峰字符串,默认分隔符为下划线
     *
     * @param str 需要处理的字符串
     * @return
     */
    public static String humpStr(String str) {
        return humpStr(str, "_");
    }

    /**
     * 将一个字符串转换成驼峰字符串
     *
     * @param str       需要处理的字符串
     * @param separator 分隔符
     * @return
     */
    public static String humpStr(String str, String separator) {
        if (isBlank(str)) {
            return null;
        }
        Pattern compile = Pattern.compile("(" + separator + ".?)");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            matcher.appendReplacement(sb, group.substring(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将驼峰格式字符串转换成下划线格式
     *
     * @param str 驼峰格式字符串
     * @return
     */
    public static String underlineStr(String str) {
        if (isBlank(str)) {
            return null;
        }
        if (isNotBlank(str) && str.length() == 1) {
            return str.toLowerCase();
        }
        Pattern compile = Pattern.compile("[A-Z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            if (group.equals(str)) {
                continue;
            }
            matcher.appendReplacement(sb, "_" + group.toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 将字符串根据java变量规则(驼峰式)进行格式化
     *
     * @param str              需要转换的字符串
     * @param firstChatIsLower 首字母是否小写
     * @return
     */
    public static String converToJavaVariable(String str, boolean firstChatIsLower) {
        String humpStr = humpStr(str.replaceAll("[-,、，/\\|]", "_"));
        if (firstChatIsLower) {
            return Character.toLowerCase(humpStr.charAt(0)) + humpStr.substring(1);
        } else {
            return Character.toUpperCase(humpStr.charAt(0)) + humpStr.substring(1);
        }
    }

    /**
     * 根据javaBean标准获取一个属性对应的getter/setter的后缀
     *
     * @param fileName 属性名称
     */
    public static String getGetterOrSetterSuffix(String fileName) {
        if (isBlank(fileName)) {
            return null;
        }
        if (fileName.length() == 1) {
            return fileName.toUpperCase();
        } else {
            char firstChart = fileName.charAt(0);
            char secondChart = fileName.charAt(1);
            if ((secondChart > 'A' && secondChart < 'Z') || (firstChart > 'A' && firstChart < 'Z')) {
                return fileName;
            } else {
                return converToJavaVariable(fileName, false);
            }
        }
    }

    public static boolean isAbsoluteNotNull(String str) {
        return !"null".equals(str) && isNotBlank(str);
    }

    /**
     * 将throwable转换成String
     *
     * @param throwable 异常对象
     * @return
     */
    public static String throwableToString(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        String str;
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            str = stringWriter.toString();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            try {
                stringWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return str;
    }
}
