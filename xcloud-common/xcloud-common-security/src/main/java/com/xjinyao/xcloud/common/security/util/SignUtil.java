package com.xjinyao.xcloud.common.security.util;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description API接口签名工具类
 * @outhor 谢进伟
 * @create 2021-02-25 18:12
 */
@Slf4j
public class SignUtil {

    private static final String RANDOM_STR = "randomStr";
    private static final String SIGN = "sign";
    private static final String TIMESTAMP = "timestamp";

    /**
     * 验证签名
     *
     * @param paramsMap 需要签名的参数键值对
     * @param timestamp 时间戳
     * @param randomStr 随机字符串
     * @param appId     appId
     * @param appSecret appSecret
     * @param sign      需要验证的签名
     * @return
     */
    public static boolean validateSign(Map<String, Object> paramsMap, String bodyStr, long timestamp, String randomStr,
                                       String appId, String appSecret, String sign) {
        String newSign = genSign(paramsMap, bodyStr, timestamp, randomStr, appId, appSecret);
        log.info("new sign:{} old sign:{}", newSign, sign);
        return newSign.equals(sign);
    }

    /**
     * 生成签名
     * 生成规则：
     * <pre>
     * 1、把除时间戳（timestamp）、随机字符串(randomStr)之外的所有参数按参数的key自然顺序排序。当一个参数同时传递多个值时，值用逗号分割作为一个值参与下面的额拼接
     * 2、根据排序完的key,将所有参数组装成如下格式的字符串：timestamp=客户端传入的timestamp&key1=value1&key2=value2&key3=value3.....&randomStr=客户端传入的randomStr,这一步得到字符串:str1
     * 3、将拼成的字符串str1全部转换成小写,得到字符串：str2
     * 4、将转换成的小写之后的字符串str2倒序,得到字符串：str3
     * 5、分两种情况：
     *  a、非requestBody形式请求：组合需要即将生成签名的字符串，格式：客户端传入的timestamp+第四步得到的str3+()+客户端传入的randomStr，这一步得到字符串：str4
     *  b、requestBody的形式请求：组合需要即将生成签名的字符串，格式：客户端传入的timestamp+第四步得到的str3+#请求的body字符串内容#+客户端传入的randomStr，这一步得到字符串：str4
     * 6、生成签名：使用标准MD5加密第5步获取到的字符串str4,这一步得到的32位字符串将是签名结果
     * </pre>
     *
     * @param paramsMap 需要签名的参数键值对
     * @param bodyStr   body
     * @param timestamp 时间戳
     * @param randomStr 随机字符串
     * @param appId     appId
     * @param appSecret appSecret
     * @return
     */
    public static synchronized String genSign(Map<String, Object> paramsMap, String bodyStr, long timestamp, String randomStr,
                                              String appId, String appSecret) {
        List<String> sortedParamList = new ArrayList<>();
        //时间戳放在开头
        sortedParamList.add("timestamp=" + timestamp);
        paramsMap.put("appId", appId);
        paramsMap.put("appSecret", appSecret);
        //参数自然顺序排序, 组装排序后的参数字符串
        paramsMap.keySet().parallelStream()
                .filter(key -> !SignUtil.TIMESTAMP.equals(key) && !RANDOM_STR.equals(key) && !SIGN.equals(key))
                .sorted()
                .forEachOrdered(key -> {
                    log.info(key);
                    sortedParamList.add(key + "=" + paramsMap.get(key));
                });
        //判断是否存在Body参数
        if (StringUtils.isNotBlank(bodyStr)) {
            sortedParamList.add("#" + bodyStr + "#");
        }
        //随机字符串放在末尾
        sortedParamList.add("randomStr=" + randomStr);
        //全部转换成小写并倒序
        String sortedParamStr = StringUtils.reverse(StringUtils.lowerCase(StringUtils.join(sortedParamList, "&")));

        //生成签名
        String sign = SecureUtil.md5(new StringBuffer().append(timestamp).append(sortedParamStr).append(randomStr).toString());
        soutLog(sortedParamList, paramsMap, bodyStr, timestamp, randomStr, sign);
        return sign;
    }


    private static void soutLog(List<String> sortedParamList, Map<String, ?> paramsMap, String bodyStr, long timestamp, String randomStr, String sign) {
        log.info("----------------  SignUtil param begin ---------------------");
        log.info("paramsMap 参数：");
        paramsMap.forEach((k, v) -> log.info(k + "=" + v));
        log.info("------------------------------------------------------------");
        log.info("bodyStr:" + bodyStr);
        log.info("timestamp:" + timestamp);
        log.info("randomStr:" + randomStr);
        log.info("sortedParamList：");
        sortedParamList.forEach(log::info);
        log.info("sign:" + sign);
        log.info("----------------  SignUtil param end ---------------------");
    }
}
