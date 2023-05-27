package com.xjinyao.xcloud.test;

import com.xjinyao.xcloud.common.core.excel.util.ExcelHelper;
import com.xjinyao.xcloud.test.pojo.DeviceNoMappingImportVO;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 谢进伟
 * @description excelHelper测试
 * @createDate 2021/3/11 10:17
 */
public class ExcelHelperTest {

    @Test
    public void exportTest() {
        List<DeviceNoMappingImportVO> data = new ArrayList<DeviceNoMappingImportVO>() {{
            for (int i = 0; i < 100; i++) {
                int no = i;
                this.add((new DeviceNoMappingImportVO() {{
                    this.setDeviceNo((no + 100) + "");
                    this.setMappingNo("aa" + no);

                    this.setErrorInfo("数据已存在");
                }}));
            }
        }};

        try (final XSSFWorkbook workbook = ExcelHelper.export(data, "错误数据", DeviceNoMappingImportVO.class);
             FileOutputStream out = new FileOutputStream(new File("D:/test.xls"))) {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("导入excel完成!");
    }

}
