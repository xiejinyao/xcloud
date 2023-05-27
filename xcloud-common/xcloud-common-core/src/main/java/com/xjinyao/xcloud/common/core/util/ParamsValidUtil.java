package com.xjinyao.xcloud.common.core.util;

import cn.hutool.core.util.ObjectUtil;

/**
 * 参数合法性校验
 *
 * @author liwei
 * @createDate 2023-3-8 16:23
 */
public class ParamsValidUtil {
    /**
     * 参数校验
     *
     * @param params 参数
     */
    public static void paramsIsNull(Object... params) {
        for (Object param : params) {
            if (ObjectUtil.isEmpty(param)) {
                throw new RuntimeException("必需参数为空");
            }
        }
    }
}
