package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.dto.DataImportTask;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteSysTasksServiceFallbacFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author ：lyl
 * @date ：Created in 2021/2/1 15:02
 * @description：
 * @modified By：
 */
@FeignClient(contextId = "remoteSysTasksService", value = ServiceNameConstants.UMPS_SERVICE,
        fallbackFactory = RemoteSysTasksServiceFallbacFactory.class,
        path = ControllerMapping.SYS_TASKS_CONTROLLER_MAPPING)
public interface RemoteSysTasksService {

    /**
     * 保存日志
     *
     * @param sysTasks 任务实体
     * @param from     内部调用标志
     * @return succes、false
     */
    @PostMapping("/save")
    R<Boolean> save(@RequestBody SysTasks sysTasks,
                    @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 创建导入任务
     *
     * @param dataImportTask 数据导入任务参数
     * @param from           内部调用标志
     * @return
     */
    @PostMapping("/createImportTask")
    R<Boolean> createImportTask(@RequestBody DataImportTask dataImportTask,
                                @RequestHeader(SecurityConstants.FROM) String from);


    /**
     * 更新任务
     *
     * @param task 任务
     * @param from 内部调用标志
     * @return
     */
    @PostMapping("/updateTask")
    R<Boolean> update(@RequestBody SysTasks task,
                      @RequestHeader(SecurityConstants.FROM) String from);
}
