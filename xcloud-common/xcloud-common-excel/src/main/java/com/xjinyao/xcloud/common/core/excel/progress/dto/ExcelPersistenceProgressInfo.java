package com.xjinyao.xcloud.common.core.excel.progress.dto;

import lombok.Getter;

/**
 * @author 谢进伟
 * @description 持久化处理进度
 * @createDate 2020/9/11 10:25
 */
@Getter
public class ExcelPersistenceProgressInfo extends ProgressInfo {

    /**
     * 进度名称
     */
    protected String progressName = "EXCEL_PERSISTENCE_PROGRESS";
    /**
     * excel 任务Id
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
    /**
     * 批次号
     */
    protected String batchNumber;

    public ExcelPersistenceProgressInfo(String stage, Integer current, Integer total, String remark, String taskId,
                                        String taskName, String excelFileId, String excelFileName, String batchNumber) {
        super(stage, current, total, remark);
        this.taskId = taskId;
        this.taskName = taskName;
        this.excelFileId = excelFileId;
        this.excelFileName = excelFileName;
        this.batchNumber = batchNumber;
    }
}
