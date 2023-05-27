package com.xjinyao.xcloud.common.core.util;

import cn.hutool.poi.excel.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：lyl
 * @date ：Created in 2021/1/29 10:36
 * @description：excel解析
 */
public class ExcelUtils extends ExcelUtil {

    /**
     * 读取excel
     *
     * @param filePath 为网络路径
     */
    public static Workbook readExcel(String filePath) throws IOException {
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));

        InputStream is = null;
        try {
            URL url = new URL(filePath);
            is = url.openStream();
//            is = new FileInputStream(filePath);
            if (".xls".equals(extString)) {
                return new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                return new XSSFWorkbook(is);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wb;
    }

    /**
     * 解析excel 以首行为key 返回List<Map<String, String>>结构
     */
    public static List<Map<String, String>> parseExcel(Workbook sheets) {
        //获取sheet0页，以第一行作为key
        Sheet sheet = sheets.getSheetAt(0);
        Row row = sheet.getRow(0);
        List<String> columns = new ArrayList<>();
        row.forEach(r -> columns.add(r.getStringCellValue()));
//        System.out.println(columns);
        //获取最大列数
        int colNum = row.getPhysicalNumberOfCells();
        //获取最大行数
        int rowNum = sheet.getPhysicalNumberOfRows();
        List<Map<String, String>> resMapList = new ArrayList<>();
        for (int i = 1; i < rowNum; i++) {
            //获取每一行，组装成map
            Row row1 = sheet.getRow(i);
            if (row1 == null) {
                continue;
            }
            Map<String, String> map = new HashMap<>();
            for (int i1 = 0; i1 < colNum; i1++) {
                Cell cell = row1.getCell(i1);
                String cellValue;
                if (cell == null) {
                    cellValue = "";
                } else {
                    cellValue = cell.toString();
                }
                //科学计数法处理
                if (StringUtils.isNotBlank(cellValue)) {
                    if (cellValue.matches("^\\d+\\.\\d+E\\d+$")) {
                        BigDecimal bd = new BigDecimal(String.valueOf(cellValue));
                        cellValue = bd.toPlainString();
                    } else if (cellValue.matches("^\\d+\\.0+$")) {
                        BigDecimal bd = new BigDecimal(String.valueOf(cellValue));
                        cellValue = String.valueOf(bd.longValue());
                    }
                }
                map.put(columns.get(i1), cellValue);
            }
            resMapList.add(map);
        }

        return resMapList;
    }

    private static <T> void createTitle(List<T> beans, Sheet sheet) {
        Row row = sheet.createRow(0);
        T bean = beans.get(0);
        Field[] fields = bean.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Cell cell = row.createCell(i);
            cell.setCellValue(field.getName());
        }
    }

    public static <T> void writeExcel(List<T> beans, String path, boolean writeTitle) {
        if (beans == null || beans.size() == 0) return;
        Workbook workbook = new HSSFWorkbook();
        FileOutputStream fos = null;
        int offset = writeTitle ? 1 : 0;
        try {
            Sheet sheet = workbook.createSheet();
            for (int i = 0; i < beans.size() + offset; ++i) {
                if (writeTitle && i == 0) {
                    createTitle(beans, sheet);
                    continue;
                }
                Row row = sheet.createRow(i);
                T bean = beans.get(i - offset);
                Field[] fields = bean.getClass().getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    Field field = fields[j];
                    field.setAccessible(true);
                    Cell cell = row.createCell(j);
                    //Date,Calender都可以 使用  +"" 操作转成字符串
                    cell.setCellValue(field.get(bean) + "");
                }
            }
            fos = new FileOutputStream(path);
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param response
     * @param fileName    文件名
     * @param headRowList 表头行
     * @param resList     表内容
     */
    public static void export(HttpServletResponse response, String fileName, List<String> headRowList,
                              List<List<Object>> resList) {

        //创建poi导出数据对象
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        //创建sheet页
        SXSSFSheet sheet = sxssfWorkbook.createSheet("Sheet0");
        //创建表头
        SXSSFRow headRow = sheet.createRow(0);
        sxssfWorkbook.setSheetName(0, "设备基础信息");
        for (int i = 0; i < headRowList.size(); i++) {
            headRow.createCell(i).setCellValue(headRowList.get(i));
        }
        //添加表内容
        for (List<Object> rowList : resList) {
            SXSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int i = 0; i < rowList.size(); i++) {
                dataRow.createCell(i).setCellValue(rowList.get(i) + "");
            }
        }
        goToExcel(response, fileName, sxssfWorkbook);

    }

    public static void goToExcel(HttpServletResponse response, String fileName, Workbook workbook) {

        // 下载导出
        String filename = fileName + new SimpleDateFormat("yyyyMMdd").format(new Date());
        // 设置响应头信息
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        //设置成xlsx格式
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            //创建一个输出流
            OutputStream outputStream = response.getOutputStream();
            //写入数据
            workbook.write(outputStream);
            // 关闭
            outputStream.flush();
            outputStream.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
