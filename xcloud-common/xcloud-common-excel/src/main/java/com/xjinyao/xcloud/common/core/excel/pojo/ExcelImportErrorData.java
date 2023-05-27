package com.xjinyao.xcloud.common.core.excel.pojo;

import com.xjinyao.xcloud.common.core.excel.annotation.ExcelHeardField;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelVO;
import lombok.Data;

/**
 * @author 谢进伟
 * @description excel 导入错误对象
 * @createDate 2019/7/10 16:15
 */
@Data
@ExcelVO
public class ExcelImportErrorData {

    /**
     * 行号
     */
    @ExcelHeardField(columnName = "行号", exportInclude = true)
    private Integer row;
    /**
     * 列
     */
    @ExcelHeardField(columnName = "列", exportInclude = true)
    private Integer column;

    /**
     * 单元格是否有错
     */
    @ExcelHeardField(columnName = "单元格是否有错", exportInclude = true)
    private boolean isError;

    /**
     * 错误信息
     */
    @ExcelHeardField(columnName = "错误信息", exportInclude = true)
    private String errorInfo;

    /**
     * 单元格数据
     */
    @ExcelHeardField(columnName = "单元格数据", exportInclude = true)
    private Object value;

    public ExcelImportErrorData(Integer row, Integer column, boolean isError, String errorInfo, Object value) {
        this.row = row;
        this.column = column;
        this.isError = isError;
        this.errorInfo = errorInfo;
        this.value = value;
    }
}
