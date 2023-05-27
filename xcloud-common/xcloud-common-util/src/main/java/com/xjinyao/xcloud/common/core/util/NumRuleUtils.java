package com.xjinyao.xcloud.common.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chenjie
 * @description: 计算工具
 * @date 2022/12/07 11：01
 */
public class NumRuleUtils {

    /**
     * 设置增长的数，不够前边补0
     */
    private static final int DEFAULT_LENGTH = 6;

    /**
     * 计算批次号
     * 规则 年月日时分秒毫秒
     *
     * @return 批次号
     */
    public static String generateBatchNumBySecond() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        return (sdf.format(date));
    }

    /**
     * 计算票号
     * 规则 年月日
     *
     * @return 批次号
     */
    public static String generateBatchNumByDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return (sdf.format(date));
    }

    /**
     * 收款-收款单号
     * 格式：yyyyMMddHHmmssSSS000001-yyyyMMddHHmmssSSS999999
     * 规则 年月日时分秒毫秒+流水号（6位自增流水号）
     *
     * @return 批次号
     */
    public static String generateSerialNumByAuto(long seq, int length) {
        String serialNum = generateBatchNumBySecond();
        //获取6位自增流水号 从000001-999999
        String sequence = getSequence(seq, length);
        return serialNum + sequence;
    }

    /**
     * 将传入的数 seq 格式化成 length 位，不够前边补 0
     * 如果 length < 3 则按照 3 算
     *
     * @param seq
     * @param length
     * @return
     */
    private static String getSequence(long seq, int length) {
        String str = String.valueOf(seq);
        int len = str.length();
        length = Math.max(length, DEFAULT_LENGTH);
        if (len >= length) {
            return str;
        }
        int rest = length - len;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rest; i++) {
            sb.append('0');
        }
        sb.append(str);
        return sb.toString();
    }
}
