package com.xjinyao.xcloud.common.core.excel.progress;

/**
 * @author 谢进伟
 * @description Excel 处理进度
 * @createDate 2020/8/20 9:45
 */
public interface ExcelDisposeProgress {

    void progress(int total, int current);
}
