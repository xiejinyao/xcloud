package com.xjinyao.xcloud.common.core.excel.progress.dto;

import lombok.Getter;

/**
 * @author 谢进伟
 * @description 数据解析进度信息
 * @createDate 2020/9/11 10:28
 */
@Getter
public class ExcelDisposeProgressInfo extends ProgressInfo {

    /**
     * 进度名称
     */
    protected String progressName = "EXCEL_DISPOSE_PROGRESS";

    /**
     * 导入任务Id
     */
    protected String taskId;

    /**
     * 导入任务名称
     */
    protected String taskName;

    /**
     * excel 文件id
     */
    protected String excelFileId;
    /**
     * excel文件名
     */
    protected String excelFileName;

    public ExcelDisposeProgressInfo(String stage,
                                    Integer current,
                                    Integer total,
                                    String remark,
                                    String taskId,
                                    String taskName,
                                    String excelFileId,
                                    String excelFileName) {
        super(stage, current, total, remark);
        this.taskId = taskId;
        this.taskName = taskName;
        this.excelFileId = excelFileId;
        this.excelFileName = excelFileName;
    }
}
