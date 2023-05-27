package com.xjinyao.xcloud.common.core.desensitization.util;

import com.xjinyao.xcloud.common.core.util.StringUtils;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * @author 谢进伟
 * @createDate 2023/1/4 18:47
 */
@UtilityClass
public class PrivacyUtil {

    /**
     * 隐藏手机号中间四位
     *
     * @param phone 电话
     * @return {@link String}
     */
    public String hidePhone(String phone) {
        return Optional.ofNullable(phone)
                .orElse(StringUtils.EMPTY)
                .replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 隐藏电子邮件
     *
     * @param email 电子邮件
     * @return {@link String}
     */
    public String hideEmail(String email) {
        return Optional.ofNullable(email)
                .orElse(StringUtils.EMPTY)
                .replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    /**
     * 隐藏身份证
     *
     * @param idCard 身份证
     * @return {@link String}
     */
    public String hideIDCard(String idCard) {
        return Optional.ofNullable(idCard)
                .orElse(StringUtils.EMPTY)
                .replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }

    /**
     * 隐藏中文名字
     * 【中文姓名】只显示第一个汉字，其他隐藏为星号，比如：任**
     *
     * @param chineseName 中文名字
     * @return {@link String}
     */
    public String hideChineseName(String chineseName) {
        if (chineseName == null) {
            return null;
        }
        return desValue(chineseName, 1, 0, "*");
    }

    /**
     * des价值
     * 对字符串进行脱敏操作
     *
     * @param origin          原始字符串
     * @param prefixNoMaskLen 左侧需要保留几位明文字段
     * @param suffixNoMaskLen 右侧需要保留几位明文字段
     * @param maskStr         用于遮罩的字符串, 如'*'
     * @return 脱敏后结果
     */
    public String desValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (origin == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, n = origin.length(); i < n; i++) {
            if (i < prefixNoMaskLen) {
                sb.append(origin.charAt(i));
                continue;
            }
            if (i > (n - suffixNoMaskLen - 1)) {
                sb.append(origin.charAt(i));
                continue;
            }
            sb.append(maskStr);
        }
        return sb.toString();
    }

}
