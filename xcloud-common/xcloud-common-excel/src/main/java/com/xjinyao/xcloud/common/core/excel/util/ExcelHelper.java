package com.xjinyao.xcloud.common.core.excel.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjinyao.xcloud.common.core.excel.annotation.Dictionary;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelHeardField;
import com.xjinyao.xcloud.common.core.excel.annotation.ExcelVO;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelDictionary;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelHeard;
import com.xjinyao.xcloud.common.core.excel.pojo.ExcelImportErrorData;
import com.xjinyao.xcloud.common.core.excel.progress.ExcelDisposeProgress;
import com.xjinyao.xcloud.common.core.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 谢进伟
 * @description Excel操作工具
 * @createDate 2020/8/20 9:42
 */
public class ExcelHelper {


    /**
     * 方法 导入excel
     *
     * @param cls                         泛型对象
     * @param voCls                       映射实体
     * @param xlsPath                     Excel对象的路径
     * @param excelImportErrorData        错误信息集合
     * @param fieldDynamicExcelDictionary 属性动态映射字典
     * @param disposeProgress             泛型
     * @param <T>
     * @return
     */
    public static <T> List<T> loadExcel(Class<T> cls,
                                        Class<?> voCls,
                                        String xlsPath,
                                        List<ExcelImportErrorData> excelImportErrorData,
                                        Map<String, List<ExcelDictionary>> fieldDynamicExcelDictionary,
                                        ExcelDisposeProgress disposeProgress) {
        List<ExcelHeard> excelHeards = new ArrayList<>();
        if (!voCls.isAnnotationPresent(ExcelVO.class)) {
            return Collections.emptyList();
        }
        for (Class acls = voCls; acls != null; acls = acls.getSuperclass()) {
            Field[] declaredFields = acls.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(ExcelHeardField.class)) {
                    continue;
                }
                ExcelHeardField excelHeardField = field.getAnnotation(ExcelHeardField.class);
                if (excelHeardField.importField()) {
                    excelHeards.add(new ExcelHeard() {{
                        this.setColumnName(excelHeardField.columnName());
                        this.setRequired(excelHeardField.importRequired());
                        this.setPattern(excelHeardField.importPattern());
                        this.setMatchErrorMessage(excelHeardField.importMatchErrorMessage());
                        this.setJavaField(field.getName());
                        this.setColumnType(field.getType().getTypeName());
                        setHeaderDictionary(this, excelHeardField.dictionary());
                    }});
                }
            }
        }
        if (excelHeards.size() > 0) {
            return doLoadExcel(cls, xlsPath, excelImportErrorData, fieldDynamicExcelDictionary, disposeProgress,
                    excelHeards);
        }
        return Collections.emptyList();
    }

    /**
     * 数据导出
     *
     * @param data      需要导出的数据
     * @param sheetName sheet名称
     * @param voCls     头部映射信息
     * @param <T>
     * @return
     */
    public static <T> XSSFWorkbook export(List<T> data, String sheetName, Class<?> voCls) {
        List<ExcelHeard> excelHeards = new ArrayList<ExcelHeard>() {{
            for (Class acls = voCls; acls != null; acls = acls.getSuperclass()) {
                Field[] declaredFields1 = acls.getDeclaredFields();
                for (Field field : declaredFields1) {
                    field.setAccessible(true);
                    if (!field.isAnnotationPresent(ExcelHeardField.class)) {
                        continue;
                    }
                    ExcelHeardField excelHeardField = field.getAnnotation(ExcelHeardField.class);
                    if (excelHeardField.exportInclude()) {
                        this.add(new ExcelHeard() {{
                            setHeaderDictionary(this, excelHeardField.dictionary());
                            this.setColumnName(excelHeardField.columnName());
                            this.setJavaField(field.getName());
                            this.setColumnType(field.getType().getTypeName());
                        }});
                    }
                }
            }
        }};
        return export(data, sheetName, excelHeards);
    }

    /**
     * 数据导出
     *
     * @param data        需要导出的数据
     * @param sheetName   sheet名称
     * @param excelHeards 头部映射信息
     * @param <T>
     * @return
     */
    public static <T> XSSFWorkbook export(List<T> data, String sheetName, List<ExcelHeard> excelHeards) {
        //创建一个Excel
        XSSFWorkbook hwb = new XSSFWorkbook();
        //创建会员表
        XSSFSheet sheet = hwb.createSheet(sheetName);
        //设置列宽自适应
        sheet.autoSizeColumn(1, true);
        //设置对齐方式
        XSSFCellStyle style = hwb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (CollectionUtil.isEmpty(data)) {
            return hwb;
        }
        //创建导出的文件
        Iterator<T> it = data.iterator();

        int rowIndex = 1;
        try {
            //创建第一行
            XSSFRow row0 = sheet.createRow(0);
            List<ExcelDictionary> dictionaries = null;
            Map<String, List<ExcelDictionary>> dictionaryMapping = new TreeMap<>();
            int cellIndex = 0;
            for (ExcelHeard excelHeard : excelHeards) {
                row0.createCell(cellIndex++).setCellValue(excelHeard.getColumnName());
                String javaField = excelHeard.getJavaField();
                dictionaries = excelHeard.getDictionary();
                dictionaryMapping.put(javaField, dictionaries);
            }

            while (it.hasNext()) {
                int rowNum = rowIndex++;
                //创建其他行（list集合元素个数确定）
                XSSFRow row = sheet.createRow(rowNum);
                T t = it.next();
                if (t == null) {
                    continue;
                }
                JSONObject json = JSON.parseObject(JSON.toJSONString(t));

                cellIndex = 0;
                for (ExcelHeard excelHeard : excelHeards) {
                    String fieldName = excelHeard.getJavaField();
                    Object value = json.getString(fieldName);

                    XSSFCell cell = row.createCell(cellIndex++);

                    //类型判断
                    String textValue = null;
                    //日期格式
                    if (value instanceof Date || value instanceof Timestamp) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        textValue = sdf.format(date);
                    } else if (value instanceof LocalDateTime) {
                        textValue = value.toString().replace("T", "");
                    } else {
                        if (value != null) {
                            List<ExcelDictionary> excelDictionaries = dictionaryMapping.get(fieldName);
                            if (excelDictionaries != null && !excelDictionaries.isEmpty()) {
                                for (ExcelDictionary dictionary : excelDictionaries) {
                                    if (dictionary.getCode().equals(value)) {
                                        textValue = dictionary.getChinese();
                                        break;
                                    }
                                }
                            } else {
                                textValue = value.toString();
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(textValue)) {
                        cell.setCellValue(textValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hwb;
    }


    /**
     * 设置Header的字典映射
     *
     * @param heard      heard对象
     * @param dictionary 字典
     */
    public static void setHeaderDictionary(ExcelHeard heard, Dictionary[] dictionary) {
        if (dictionary != null && dictionary.length > 0) {
            heard.setDictionary(new ArrayList<ExcelDictionary>() {{
                for (Dictionary dictionary : dictionary) {
                    this.add(new ExcelDictionary() {{
                        this.setCode(dictionary.code());
                        this.setChinese(dictionary.chinese());
                    }});
                }
            }});
        }
    }

    /**
     * 加载Excel数据
     *
     * @param cls                         泛型对象
     * @param xlsPath                     Excel对象的路径
     * @param excelImportErrorData        错误信息集合
     * @param fieldDynamicExcelDictionary 属性动态映射字典
     * @param disposeProgress             泛型
     * @param excelHeards                 excel头部映射信息
     * @param <T>
     * @return
     */
    private static <T> List<T> doLoadExcel(Class<T> cls,
                                           String xlsPath,
                                           List<ExcelImportErrorData> excelImportErrorData,
                                           Map<String, List<ExcelDictionary>> fieldDynamicExcelDictionary,
                                           ExcelDisposeProgress disposeProgress,
                                           List<ExcelHeard> excelHeards) {
        excelImportErrorData = excelImportErrorData == null ? new ArrayList<>() : excelImportErrorData;
        List<T> readDataList = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(xlsPath);
             //根据指定的文件输入流导入Excel产生workbook对象
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            //获取第一行的行号num
            int firstRowNum = sheet.getFirstRowNum();
            int lastRowNum = sheet.getLastRowNum();
            if (firstRowNum == lastRowNum) {
                excelImportErrorData.add(new ExcelImportErrorData(0, 0, true,
                        "未找到任何可解析数据!", null));
                return readDataList;
            }
            //根据行号取得第一行
            Row firstRow = sheet.getRow(firstRowNum);
            //取得第一行的第一列和最后一列
            short firstCellNum = firstRow.getFirstCellNum();
            short lastCellNum = firstRow.getLastCellNum();

            Map<String, String> columnNameMapping = new TreeMap<>();
            Map<String, List<ExcelDictionary>> dictionaryMapping = new TreeMap<>();
            Map<String, String> columnTypeMapping = new HashMap<>();
            Map<String, String> patternMapping = new TreeMap<>();
            Map<String, String> requiredMapping = new TreeMap<>();
            Map<String, String> errorMessageMapping = new TreeMap<>();
            for (ExcelHeard excelHeard : excelHeards) {
                String javaField = excelHeard.getJavaField();
                columnNameMapping.put(javaField, excelHeard.getColumnName());

                List<ExcelDictionary> dictionary = excelHeard.getDictionary();
                if (fieldDynamicExcelDictionary != null && !fieldDynamicExcelDictionary.isEmpty()) {
                    List<ExcelDictionary> dynamicExcelDictionary = fieldDynamicExcelDictionary.get(javaField);
                    if (dynamicExcelDictionary != null && !dynamicExcelDictionary.isEmpty()) {
                        dictionary = dynamicExcelDictionary;
                    }
                }
                dictionaryMapping.put(javaField, dictionary);
                columnTypeMapping.put(javaField, excelHeard.getColumnType());
                patternMapping.put(javaField, excelHeard.getPattern());
                requiredMapping.put(javaField, excelHeard.getRequired().toString());
                errorMessageMapping.put(javaField, excelHeard.getMatchErrorMessage());
            }

            Map<String, Integer> fieldIndex = new TreeMap<>();
            Field[] declaredFields = cls.getDeclaredFields();// 获取属性
            for (Field field : declaredFields) {
                String fieldName = field.getName();// id ,name ,sex,age
                if (StringUtils.isNotBlank(fieldName)) {
                    String columnName = columnNameMapping.get(fieldName);
                    for (int i = firstCellNum; i < lastCellNum; i++) {
                        Cell cell = firstRow.getCell(i);
                        String realColumnName = cell.getStringCellValue();
                        if (realColumnName.equals(columnName)) {
                            fieldIndex.put(fieldName, i);
                        }
                    }
                }
            }
            if (CollectionUtil.isEmpty(fieldIndex)) {
                excelImportErrorData.add(new ExcelImportErrorData(0, 0, true,
                        "excel头信息错误，无法解析!", null));
                return readDataList;
            }
            Integer rowIndex = 0;
            //循环行
            for (Row row : sheet) {
                try {
                    rowIndex = rowIndex + 1;
                    if (disposeProgress != null) {
                        disposeProgress.progress(lastRowNum + 1, rowIndex);
                    }
                    if (row.getRowNum() < 1) {
                        continue;
                    }
                    T t = cls.newInstance();
                    Field[] declaredFields1 = cls.getDeclaredFields();// 获取属性
                    boolean isError = false;
                    for (Field field : declaredFields1) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        Integer index = fieldIndex.get(fieldName);
                        if (index == null) {
                            continue;
                        }
                        Cell cell = row.getCell(index);
                        Class<?> type = field.getType();
                        String pattern = patternMapping.get(fieldName);
                        String required = requiredMapping.get(fieldName);
                        String errorMessage = errorMessageMapping.get(fieldName);
                        String typeName = type.getTypeName();
                        String columnName = columnTypeMapping.get(fieldName);
                        typeName = StringUtils.isBlank(columnName) ? typeName : columnName;
                        Object fieldValue = null;
                        boolean dontSetValue = false;
                        //列号
                        Integer columnIndex = index + 1;
                        if (("true".equals(required) && cell != null) || ("false".equals(required))) {
                            if (cell != null) {//判断required=false的 cell是否为空
                                String cellValue = getCellValue(cell);
                                if (StringUtils.isBlank(cellValue)) {
                                    continue;
                                }
                                //科学计数法处理
                                if (StringUtils.isNotBlank(cellValue)) {
                                    if (cellValue.matches("^\\d+\\.\\d+E\\d+$")) {
                                        BigDecimal bd = new BigDecimal(String.valueOf(cellValue));
                                        cellValue = bd.toPlainString();
                                    } else if (cellValue.matches("^\\d+\\.0+$")) {
                                        BigDecimal bd = new BigDecimal(String.valueOf(cellValue));
                                        cellValue = String.valueOf(bd.doubleValue());
                                    }
                                }

                                Object dictValue = getDictionaryValue(dictionaryMapping, fieldName, cellValue);
                                switch (typeName) {
                                    case "java.lang.String":
                                        fieldValue = dictValue != null ? dictValue : cellValue;
                                        if (!fieldValue.toString().matches(pattern)) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.lang.Boolean":
                                        try {
                                            fieldValue = BooleanUtil.toBoolean(cellValue);
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.lang.Integer":
                                        try {
                                            fieldValue = dictValue != null ? dictValue :
                                                    NumberUtil.parseNumber(cellValue).intValue();
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.lang.Short":
                                        try {
                                            fieldValue = dictValue != null ? dictValue :
                                                    NumberUtil.parseNumber(cellValue).shortValue();
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.lang.Float":
                                    case "java.lang.Double":
                                        try {
                                            fieldValue = dictValue != null ? dictValue :
                                                    NumberUtil.parseNumber(cellValue).doubleValue();
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.lang.Long":
                                        try {
                                            fieldValue = dictValue != null ? dictValue :
                                                    NumberUtil.parseNumber(cellValue).longValue();
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.math.BigDecimal":
                                        try {
                                            fieldValue = dictValue != null ? dictValue :
                                                    NumberUtil.parseNumber(cellValue).doubleValue();
                                            fieldValue = new BigDecimal(String.valueOf(fieldValue));
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.time.LocalDate":
                                        try {
                                            fieldValue = DateUtil.toLocalDate(DateUtil.parse(cellValue));
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.time.LocalDateTime":
                                        try {
                                            fieldValue = DateUtil.toLocalDateTime(DateUtil.parse(cellValue));
                                            if (!fieldValue.toString().matches(pattern)) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        } catch (Exception e) {
                                            dontSetValue = true;
                                            isError = true;
                                        }
                                        break;
                                    case "java.util.Date":
                                    case "java.sql.Timestamp":
                                        Date date = null;
                                        try {
                                            if (cell == null) {
                                                continue;
                                            }
                                            fieldValue = cellValue;
                                            if (!fieldValue.toString().matches(pattern)) {
                                                isError = true;
                                            } else {
                                                date = DateUtil.parse(fieldValue.toString());
                                                try {
                                                    field.set(t, date);
                                                } catch (Exception e) {
                                                    field.set(t, new Timestamp(date.getTime()));
                                                }
                                                dontSetValue = true;
                                            }
                                        } catch (Exception e) {
                                            try {
                                                date = cell.getDateCellValue();
                                                try {
                                                    field.set(t, date);
                                                } catch (Exception e1) {
                                                    field.set(t, new Timestamp(date.getTime()));
                                                }
                                                dontSetValue = true;
                                            } catch (Exception e1) {
                                                dontSetValue = true;
                                                isError = true;
                                            }
                                        }
                                        break;
                                }

                                if (dontSetValue) {
                                    excelImportErrorData.add(new ExcelImportErrorData(rowIndex, columnIndex,
                                            true, "数据格式不匹配" +
                                            (StringUtils.isNotBlank(errorMessage) ? ("," + errorMessage) : ""),
                                            fieldValue));
                                }
                            } else {//进入这里的是required为false的，不做处理
                            }
                        } else {//required=true, 值为空的
                            excelImportErrorData.add(new ExcelImportErrorData(rowIndex, columnIndex, true,
                                    "数据不能为空", fieldValue));
                            isError = true;
                        }

                        if (!dontSetValue) {
                            boolean boole = true;
                            if (fieldValue != null) {
                                if (Integer.class.equals(type) && fieldValue.getClass().equals(String.class)) {
                                    fieldValue = StringUtils.isNotBlank(fieldValue.toString()) ? fieldValue.toString() : null;
                                    if (fieldValue == null) {
                                        boole = false;
                                    } else {
                                        try {
                                            fieldValue = Integer.parseInt(fieldValue.toString());
                                        } catch (Exception e) {
                                            excelImportErrorData.add(new ExcelImportErrorData(rowIndex, columnIndex,
                                                    true, "数据类型不匹配", fieldValue));
                                            boole = false;
                                            isError = true;
                                        }
                                    }
                                }
                            }
                            if (boole) {
                                field.set(t, fieldValue);
                            }
                        }
                    }
                    if (!isError) {
                        readDataList.add(t);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            excelImportErrorData.add(new ExcelImportErrorData(0, 0, true,
                    "文件解析失败(" + e.getMessage() + ")!", null));
            return readDataList;
        }
        return readDataList;
    }

    /**
     * 获取字典值
     *
     * @param dictionaryMapping 字典映射
     * @param fieldName         字段名
     * @param cellValue         单元格值
     * @return
     */
    private static Object getDictionaryValue(Map<String, List<ExcelDictionary>> dictionaryMapping, String fieldName,
                                             String cellValue) {
        Object fieldValue = null;
        List<ExcelDictionary> dictionaries = dictionaryMapping.get(fieldName);
        if (dictionaries != null && !dictionaries.isEmpty()) {
            for (ExcelDictionary dictionary1 : dictionaries) {
                String chinese = dictionary1.getChinese();
                if (cellValue != null && chinese.equals(cellValue.trim())) {
                    fieldValue = dictionary1.getCode();
                    break;
                }
            }
        }
        return fieldValue;
    }

    /**
     * 获取单元格值
     *
     * @param cell 单元格对象
     * @return
     */
    private static String getCellValue(Cell cell) {
        Object value = null;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING://字符串
                value = cell.getStringCellValue();
                break;
            case BOOLEAN://Boolean
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC://数字
                value = cell.getNumericCellValue();
                break;
            case FORMULA://公式
                try {
                    value = cell.getNumericCellValue();
                } catch (Exception e) {
                    try {
                        value = cell.getStringCellValue();
                    } catch (Exception exception) {
                        try {
                            value = cell.getBooleanCellValue();
                        } catch (Exception ex) {
                        }
                    }
                }
        }
        return value == null ? null : value.toString();
    }
}
