package com.xjinyao.xcloud.mq.receiver;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.xjinyao.xcloud.admin.api.dto.DataImportTask;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import com.xjinyao.xcloud.admin.api.enums.SysTaskStatusEnum;
import com.xjinyao.xcloud.admin.api.feign.RemoteSysTasksService;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.excel.SimpleDataImport;
import com.xjinyao.xcloud.common.core.excel.pojo.PersistenceErrorVO;
import com.xjinyao.xcloud.file.api.feign.RemoteSysFileService;
import com.xjinyao.xcloud.file.api.vo.SysFileVO;
import com.xjinyao.xcloud.socket.enums.CommandEnum;
import com.xjinyao.xcloud.socket.feign.RemoteSocketIOMessageService;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author 谢进伟
 * @description 通用数据导入过程抽象，请继承此类进行数据导入任务出队入库操作
 * @createDate 2021/3/12 9:09
 */
public abstract class GeneralDataImportTaskHandler<T, M extends PersistenceErrorVO> extends SimpleDataImport<T, M> {

    /**
     * 导入错误信息文件在文件服务器中的存储目录
     */
    protected static final String DATA_IMPORT_ERROR = "dataImportError";
    protected RemoteSysFileService remoteSysFileService;
    protected RemoteSysTasksService remoteSysTasksService;
    protected RemoteSocketIOMessageService remoteSocketIOMessageService;
    private String adminUserId;

    protected GeneralDataImportTaskHandler() {

    }

    public GeneralDataImportTaskHandler(RemoteSysFileService remoteSysFileService,
                                        RemoteSysTasksService remoteSysTasksService,
                                        RemoteSocketIOMessageService remoteSocketIOMessageService) {
        this.remoteSysFileService = remoteSysFileService;
        this.remoteSysTasksService = remoteSysTasksService;
        this.remoteSocketIOMessageService = remoteSocketIOMessageService;
    }

    /**
     * 执行导入继续
     *
     * @param cls            excel行数据映射类
     * @param voCls          excel表头映射类
     * @param dataImportTask 导入任务
     * @throws Exception
     */
    protected void executeImportParse(Class<T> cls,
                                      Class<M> voCls,
                                      DataImportTask dataImportTask) throws Exception {
        final String taskId = dataImportTask.getTaskId();
        SysFileVO fileVO = remoteSysFileService.getFile(dataImportTask.getFileId(), SecurityConstants.FROM_IN).getData();
        if (fileVO == null) {
            final String result = "导入失败，导入文件未找到，无法执行后续操作!";
            this.updateTaskStatus(taskId, SysTaskStatusEnum.PROCESSED.getValue(), result);
            return;
        }
        this.cls = cls;
        this.voCls = voCls;
        this.adminUserId = String.valueOf(dataImportTask.getAdminUserId());
        this.excelFilePath = fileVO.getUrl();
        this.taskId = taskId;
        this.taskName = dataImportTask.getTaskName();
        this.fileId = fileVO.getId().toString();
        this.fileName = fileVO.getOriginalName();

        //清理历史数据
        this.errorTemplateZipFile = null;
        this.errorFileDownloadUrl = null;
        CollectionUtil.clear(this.excelImportErrorData, this.persistenceErrorDataList, this.excelDataList);
        this.excelDataSize = 0;
        this.fieldDynamicExcelDictionary.clear();
        this.excelFileSize = 0L;
        this.disposeProgress = null;
        this.persistenceProgress = null;

        //初始化进度实现
        this.initProgressImpl(this.taskId, fileId, fileName);

        //开始执行
        this.execute();
    }

    @Override
    protected void begin() {
        //更新任务为处理中状态
        remoteSysTasksService.update(SysTasks.builder()
                .id(Integer.valueOf(taskId))
                .status(SysTaskStatusEnum.PROCESSING.getValue())
                .beginTime(LocalDateTime.now())
                .build(), SecurityConstants.FROM_IN);
    }

    @Override
    protected void loadDataAfter() {
        super.loadDataAfter();
        remoteSysFileService.deleteFile(Integer.valueOf(fileId), SecurityConstants.FROM_IN);
    }

    @Override
    protected String uploadErrorFile(CommonsMultipartFile commonsMultipartFile) {
        try {
            final Collection<SysFileVO> sfvos = remoteSysFileService.uploadFileToServerDir(commonsMultipartFile,
                    DATA_IMPORT_ERROR, true, SecurityConstants.FROM_IN).getData();
            if (CollectionUtil.isNotEmpty(sfvos)) {
                return sfvos.iterator().next().getUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.deleteQuietly(errorTemplateZipFile);
        }
        return null;
    }

    @Override
    protected void done() {
        super.done();
        //更新任务状态为已处理
        remoteSysTasksService.update(SysTasks.builder()
                .id(Integer.valueOf(taskId))
                .status(SysTaskStatusEnum.PROCESSED.getValue())
                .endTime(LocalDateTime.now())
                .result("导入完成!")
                .build(), SecurityConstants.FROM_IN);
    }

    @Override
    protected void broadcast(Object content) {
        if (StrUtil.isBlankOrUndefined(adminUserId)) {
            return;
        }
        remoteSocketIOMessageService.unicastCommand(UnicastCommand.builder()
                        .command(CommandEnum.DATA_IMPORT_DISPOSE_PROGRESS_COMMAND)
                        .remark("导入进度提示")
                        .to(adminUserId)
                        .data(content)
                        .build()
                , SecurityConstants.FROM_IN);
    }

    /**
     * 更新任务状态
     *
     * @param taskId 任务id
     * @param status 状态
     * @param result 结果
     */
    protected void updateTaskStatus(String taskId, Integer status, String result) {
        remoteSysTasksService.update(SysTasks.builder()
                .id(Integer.valueOf(taskId))
                .status(status)
                .endTime(LocalDateTime.now())
                .result(result)
                .build(), SecurityConstants.FROM_IN);
    }
}
