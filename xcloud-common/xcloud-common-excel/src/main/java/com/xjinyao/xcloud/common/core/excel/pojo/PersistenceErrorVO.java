package com.xjinyao.xcloud.common.core.excel.pojo;

import com.xjinyao.xcloud.common.core.excel.annotation.ExcelHeardField;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelVO;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 持久化错误信息
 * @createDate 2021/3/10 16:31
 */
@Data
@ExcelVO
@SuperBuilder(toBuilder = true)
public class PersistenceErrorVO implements Serializable {

    /**
     * 错误信息
     */
    @ExcelHeardField(columnName = "错误信息", importField = false, exportInclude = true, errorIdentify = true)
    private String errorInfo;

    @Tolerate
    public PersistenceErrorVO() {

    }
}
