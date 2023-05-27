package com.xjinyao.xcloud.admin.controller.innner;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.dto.DataImportTask;
import com.xjinyao.xcloud.admin.api.dto.Notice;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import com.xjinyao.xcloud.admin.api.enums.SysTaskStatusEnum;
import com.xjinyao.xcloud.admin.service.ISysTasksService;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.util.DateUtil;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import com.xjinyao.xcloud.mq.api.queue.QueueService;
import com.xjinyao.xcloud.socket.enums.CommandEnum;
import com.xjinyao.xcloud.socket.feign.RemoteSocketIOMessageService;
import com.xjinyao.xcloud.socket.message.command.UnicastCommand;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 内部接口：任务队列
 *
 * @author 谢进伟
 * @createDate 2022/11/16 10:53
 */
@ApiIgnore
@RestController
@RequestMapping(ControllerMapping.SYS_TASKS_CONTROLLER_MAPPING)
@AllArgsConstructor
public class InnerSysTasksController {

    private final ISysTasksService sysTasksService;
    private final QueueService queueService;
    private final RemoteSocketIOMessageService remoteSocketIOMessageService;

    @Inner
    @PostMapping("/save")
    @ApiOperation(value = "保存任务队列", notes = "保存任务队列")
    public R<Boolean> saveForInner(@RequestBody SysTasks sysTasks) {
        return R.ok(sysTasksService.save(sysTasks));
    }

    @Inner
    @PostMapping("/createImportTask")
    @ApiOperation(value = "创建导入任务", notes = "创建导入任务", hidden = true)
    public R<Boolean> createImportTask(@RequestBody DataImportTask dataImportTask) {
        if (dataImportTask.getAdminUserId() == null) {
            return R.failed(Boolean.FALSE, "缺失创建者信息，无法创建任务!");
        }
        String taskCodeValue = "task_data_import_" + UUID.fastUUID();
        SysTasks task = new SysTasks() {{
            this.setAdminUserId(dataImportTask.getAdminUserId());
            this.setName(dataImportTask.getTaskName());
            this.setTaskCode(taskCodeValue);
            this.setTaskNum(DateUtil.format(new Date(), "YYYYMMddHHmmss"));
            this.setStatus(SysTaskStatusEnum.PENDING.getValue());
            this.setConfigJson(JSON.toJSONString(dataImportTask));
            this.setCreateTime(LocalDateTime.now());
        }};
        if (sysTasksService.save(task)) {
            dataImportTask.setTaskId(task.getId().toString());
            queueService.sendToExchange(dataImportTask.getExchage(), dataImportTask.getRoutingKey(), dataImportTask);
            //发布消息通知
            remoteSocketIOMessageService.unicastCommand(UnicastCommand.builder()
                    .command(CommandEnum.SYS_TASK_CREATED_NOTICE_COMMAND)
                    .to(dataImportTask.getAdminUserId().toString())
                    .data(Notice.builder()
                            .title(dataImportTask.getTaskName())
                            .subtitle(LocalDateTime.now().toString())
                            .status(SysTaskStatusEnum.PENDING.getValue().toString())
                            .tag(SysTaskStatusEnum.PENDING.getRemark())
                            .build())
                    .remark("导入任务创建成功消息通知")
                    .build(), SecurityConstants.FROM_IN);
            return R.ok(Boolean.TRUE, "导入任务创建成功,数据将异步解析处理,请稍后查看数据!");
        } else {
            return R.ok(Boolean.FALSE, "导入任务创建失败!");
        }
    }

    @Inner
    @PostMapping("/updateTask")
    @ApiOperation(value = "更新任务", notes = "更新任务", hidden = true)
    public R<Boolean> update(@RequestBody SysTasks task) {
        if (task.getId() == null) {
            return R.failed(Boolean.FALSE);
        }
        return R.ok(sysTasksService.updateById(task));
    }
}
