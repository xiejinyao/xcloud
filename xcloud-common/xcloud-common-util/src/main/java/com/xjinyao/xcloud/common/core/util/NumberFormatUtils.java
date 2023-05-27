package com.xjinyao.xcloud.common.core.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author chenjie
 * @description 针对数字（钱）的格式化
 * @createDate 2022/12/14 09:28
 */
public class NumberFormatUtils {

    public static String getCurrencyInstance(BigDecimal v1) {
        //建立货币格式化引用 中文¥  ￥16,374.23
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        String currency = currencyInstance.format(v1);
        return currency;
    }

    public static String getPercentInstance(BigDecimal v1) {
        ////建立百分比格式化引用 百分比小数点最多2位
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(2);
        String percent = percentInstance.format(v1);
        return percent;
    }
}
