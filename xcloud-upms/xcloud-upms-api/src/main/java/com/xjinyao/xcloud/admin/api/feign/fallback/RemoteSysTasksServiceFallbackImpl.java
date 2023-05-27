package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.dto.DataImportTask;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import com.xjinyao.xcloud.admin.api.feign.RemoteSysTasksService;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：lyl
 * @date ：Created in 2021/2/1 15:02
 * @description：
 * @modified By：
 */
@Slf4j
public class RemoteSysTasksServiceFallbackImpl implements RemoteSysTasksService {
    @Setter
    private Throwable cause;


    @Override
    public R<Boolean> save(SysTasks sysTasks, String from) {
        log.error("任务储存 失败 {}", sysTasks, cause);
        return R.failed(Boolean.FALSE, "任务储存失败!");
    }

    @Override
    public R<Boolean> createImportTask(DataImportTask dataImportTask, String from) {
        log.error("创建导入任务 失败 {}", dataImportTask, cause);
        return R.failed(Boolean.FALSE, "创建导入任务失败!");
    }

    @Override
    public R<Boolean> update(SysTasks task, String from) {
        log.error("更新任务状态 失败 {}", task, cause);
        return R.failed(Boolean.FALSE, "更新任务状态!");
    }
}
