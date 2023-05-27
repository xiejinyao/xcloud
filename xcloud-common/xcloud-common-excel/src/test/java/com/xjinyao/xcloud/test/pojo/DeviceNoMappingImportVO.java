package com.xjinyao.xcloud.test.pojo;

import com.xjinyao.xcloud.common.core.excel.annotation.ExcelHeardField;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelVO;
import com.xjinyao.xcloud.common.core.excel.pojo.PersistenceErrorVO;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备编码映射导入声明VO
 *
 * @author 谢进伟
 * @date 2021-03-10 16:00:00
 */
@Data
@ExcelVO
public class DeviceNoMappingImportVO extends PersistenceErrorVO implements Serializable {

    @ExcelHeardField(columnName = "设备编号", importRequired = true, exportInclude = true, errorIdentify = true)
    private String deviceNo;

    @ExcelHeardField(columnName = "映射编码", importRequired = true, exportInclude = true, errorIdentify = true)
    private String mappingNo;

}
