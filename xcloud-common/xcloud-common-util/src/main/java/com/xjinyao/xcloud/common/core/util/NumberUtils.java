package com.xjinyao.xcloud.common.core.util;

import cn.hutool.core.util.NumberUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @author lyl
 * @description: 计算工具
 * @date 2020/11/6 20:32
 */
public class NumberUtils extends NumberUtil {

    /**
     * bigDecimal 计算平方根
     *
     * @param value
     * @param scale 保留小数位
     * @return
     */
    public static BigDecimal sqrt(BigDecimal value, int scale) {
        BigDecimal num2 = BigDecimal.valueOf(2);
        int precision = 100;
        MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
        BigDecimal deviation = value;
        int cnt = 0;
        while (cnt < precision) {
            deviation = (deviation.add(value.divide(deviation, mc))).divide(num2, mc);
            cnt++;
        }
        deviation = deviation.setScale(scale, RoundingMode.HALF_UP);
        return deviation;
    }


    /**
     * bigDecimal计算平均数
     *
     * @param size
     * @param num
     * @return
     */
    public static BigDecimal getAverageValue(int size, BigDecimal... num) {
        if (size == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal res = BigDecimal.ZERO;
        for (BigDecimal bigDecimal : num) {
            res = res.add(bigDecimal);
        }
        return res.divide(BigDecimal.valueOf(size));
    }


    /**
     * 解析为Long类型
     *
     * @param serializable 待解析对象
     * @return {@link Long}
     */
    public static Integer parseInt(Serializable serializable) {
        return Integer.parseInt(serializable.toString());
    }

    /**
     * 解析为Long类型
     *
     * @param serializable 待解析对象
     * @return {@link Long}
     */
    public static Long parseLong(Serializable serializable) {
        return Long.parseLong(serializable.toString());
    }

    /**
     * 比较两个数值类型值是否相等
     *
     * @param o1 第一个数
     * @param o2 第二个数
     * @return boolean
     */
    public static boolean equals(Serializable o1, Serializable o2) {
        if (o1 != null && o2 != null) {
            String str1 = o1.toString();
            String str2 = o2.toString();
            if (!isNumber(str1) || !isNumber(str2)) {
                return false;
            }
            Number number1 = parseNumber(str1);
            Number number2 = parseNumber(str2);
            return number1.equals(number2);
        }
        return false;
    }
}
