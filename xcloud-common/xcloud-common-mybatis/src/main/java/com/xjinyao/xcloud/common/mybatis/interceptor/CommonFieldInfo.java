package com.xjinyao.xcloud.common.mybatis.interceptor;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author 谢进伟
 * @createDate 2022/11/29 16:31
 */
@Data
@Builder
class CommonFieldInfo {

    /**
     * 字段集合
     */
    private List<String> fieldNameList;

    /**
     * 默认值提供者
     */
    private Supplier<Object> supplier;

    /**
     * 是否在新增之前初始化
     */
    @Builder.Default
    private boolean insertBefore = false;

    /**
     * 是否在修改之前初始化
     */
    @Builder.Default
    private boolean updateBefore = false;


    @Tolerate
    CommonFieldInfo() {

    }

    public CommonFieldInfo addFieldName(String fieldName) {
        if (this.fieldNameList == null) {
            this.fieldNameList = new ArrayList<>();
        }
        this.fieldNameList.add(fieldName);
        return this;
    }
}
