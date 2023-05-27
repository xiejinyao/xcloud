package com.xxl.job.admin.controller.inner;

import cn.hutool.db.PageResult;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.xxl.job.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.xxl.job.admin.api.model.XxlJobInfo;
import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.xxl.job.core.biz.model.ReturnT.SUCCESS_CODE;

/**
 * @author 谢进伟
 * @createDate 2023/3/10 16:48
 */
@RestController
@RequestMapping(ControllerMapping.JOB_INFO_CONTROLLER_MAPPING)
public class InnerJobInfoController {

    @Resource
    private XxlJobService xxlJobService;

    @GetMapping("/pageList")
    @PermissionLimit(limit = false)
    public R<PageResult<XxlJobInfo>> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                              @RequestParam(required = false, defaultValue = "10") int length,
                                              int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        Map<String, Object> map = xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        List<XxlJobInfo> data = (List<XxlJobInfo>) map.get("data");
        Integer total = (Integer) map.get("recordsTotal");
        PageResult<XxlJobInfo> data1 = new PageResult<>(start, length, total);
        data1.addAll(data);
        return R.ok(data1);

    }

    @PostMapping("/add")
    @PermissionLimit(limit = false)
    public R<String> add(@RequestBody XxlJobInfo jobInfo) {
        ReturnT<String> result = xxlJobService.add(jobInfo);
        if (result.getCode() == SUCCESS_CODE) {
            return R.ok(result.getContent(), "新增成功！");
        } else {
            return R.failed("新增失败！");
        }
    }

    @PutMapping("/update")
    @PermissionLimit(limit = false)
    public R<Boolean> update(@RequestBody XxlJobInfo jobInfo) {
        ReturnT<String> result = xxlJobService.update(jobInfo);
        if (result.getCode() == SUCCESS_CODE) {
            return R.ok(Boolean.TRUE, "修改成功！");
        } else {
            return R.failed("修改失败！");
        }
    }

    @DeleteMapping("/remove")
    @PermissionLimit(limit = false)
    public R<Boolean> remove(int id) {
        ReturnT<String> result = xxlJobService.remove(id);
        if (result.getCode() == SUCCESS_CODE) {
            return R.ok(Boolean.TRUE, "删除成功！");
        } else {
            return R.failed("删除失败！");
        }
    }

    @GetMapping("/stop")
    @PermissionLimit(limit = false)
    public R<Boolean> pause(int id) {
        ReturnT<String> result = xxlJobService.stop(id);
        if (result.getCode() == SUCCESS_CODE) {
            return R.ok(Boolean.TRUE, "停止成功！");
        } else {
            return R.failed("停止失败！");
        }
    }

    @GetMapping("/start")
    @PermissionLimit(limit = false)
    public R<Boolean> start(int id) {
        ReturnT<String> result = xxlJobService.start(id);
        if (result.getCode() == SUCCESS_CODE) {
            return R.ok(Boolean.TRUE, "启动成功！");
        } else {
            return R.failed("启动失败！");
        }
    }

    @GetMapping("/trigger")
    @PermissionLimit(limit = false)
    public R<Boolean> triggerJob(int id, String executorParam, String addressList) {
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return R.ok(Boolean.TRUE, "触发成功！");
    }
}
