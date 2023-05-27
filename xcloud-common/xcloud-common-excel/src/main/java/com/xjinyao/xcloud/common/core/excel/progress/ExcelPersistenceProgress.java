package com.xjinyao.xcloud.common.core.excel.progress;

/**
 * @author 谢进伟
 * @description Excel 处理进度
 * @createDate 2020/9/10 22:02
 */
public interface ExcelPersistenceProgress<T> {

    void progress(T obj, int batchNumber, int total, int current);
}
