package com.xjinyao.xcloud.common.core.excel.constants;

/**
 * @author 谢进伟
 * @description 数据解析进度常量
 * @createDate 2021/3/5 9:40
 */
public interface ExcelDataDisposeProgressConstants {

    /**
     * 解析中
     */
    String ANALYTICAL = "analytical";

    /**
     * 存储中
     */
    String STORAGE = "storage";

    /**
     * 处理完成
     */
    String DONE = "done";
    /**
     * 处理完成但有错误
     */
    String DONE_ERROR = "done_error";
}
