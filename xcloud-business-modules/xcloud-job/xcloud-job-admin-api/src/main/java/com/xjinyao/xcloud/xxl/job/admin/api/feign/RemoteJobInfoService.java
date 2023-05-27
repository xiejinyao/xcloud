package com.xjinyao.xcloud.xxl.job.admin.api.feign;

import cn.hutool.db.PageResult;
import com.alibaba.fastjson.JSON;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.xxl.job.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.xxl.job.admin.api.constants.JobExecutors;
import com.xjinyao.xcloud.xxl.job.admin.api.dto.HttpCronJobInfo;
import com.xjinyao.xcloud.xxl.job.admin.api.enums.ScheduleTypeEnum;
import com.xjinyao.xcloud.xxl.job.admin.api.feign.factory.RemoteJobInfoServiceFallbackFactory;
import com.xjinyao.xcloud.xxl.job.admin.api.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @createDate 2023/3/10 16:48
 */
@FeignClient(contextId = "remoteJobInfoService", value = ServiceNameConstants.JOB_ADMIN_SERVICE,
        path = ControllerMapping.CONTEXT_PATH + ControllerMapping.JOB_INFO_CONTROLLER_MAPPING,
        fallbackFactory = RemoteJobInfoServiceFallbackFactory.class)
public interface RemoteJobInfoService {

    /**
     * r任务列表
     *
     * @param start           开始
     * @param length          长度
     * @param jobGroup        任务小组
     * @param triggerStatus   触发状态
     * @param jobDesc         任务desc
     * @param executorHandler 遗嘱执行人处理程序
     * @param author          作者
     * @return R
     */
    @GetMapping("/pageList")
    R<PageResult<XxlJobInfo>> pageList(@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
                                       @RequestParam(name = "length", required = false, defaultValue = "10") Integer length,
                                       @RequestParam("jobGroup") Integer jobGroup,
                                       @RequestParam("triggerStatus") Integer triggerStatus,
                                       @RequestParam("jobDesc") String jobDesc,
                                       @RequestParam("executorHandler") String executorHandler,
                                       @RequestParam("author") String author);

    /**
     * 添加
     *
     * @param jobInfo 任务信息
     * @return {@link ReturnT}<{@link String}>
     */
    @PostMapping("/add")
    R<String> add(@RequestBody XxlJobInfo jobInfo);

    /**
     * 添加http cron作业
     *
     * @param httpCronJobInfo 处理程序参数
     * @return {@link ReturnT}<{@link String}>
     */
    default R<String> addHttpCronJob(HttpCronJobInfo httpCronJobInfo) {
        XxlJobInfo jobInfo = new XxlJobInfo();

        jobInfo.setJobGroup(httpCronJobInfo.getJobGroup());
        jobInfo.setJobDesc(httpCronJobInfo.getJobDesc());
        jobInfo.setAuthor(httpCronJobInfo.getAuthor());
        jobInfo.setAlarmEmail(httpCronJobInfo.getAlarmEmail());

        jobInfo.setScheduleType(ScheduleTypeEnum.CRON.name());
        jobInfo.setScheduleConf(httpCronJobInfo.getScheduleConf());
        jobInfo.setMisfireStrategy(httpCronJobInfo.getMisfireStrategy().name());

        jobInfo.setExecutorRouteStrategy(httpCronJobInfo.getExecutorRouteStrategy().name());
        jobInfo.setExecutorHandler(JobExecutors.HTTP_JOB_EXECUTOR);
        jobInfo.setExecutorParam(JSON.toJSONString(httpCronJobInfo.getHandlerParams()));
        jobInfo.setExecutorBlockStrategy(httpCronJobInfo.getExecutorBlockStrategy().name());
        jobInfo.setExecutorTimeout(httpCronJobInfo.getExecutorTimeout());
        jobInfo.setExecutorFailRetryCount(httpCronJobInfo.getExecutorFailRetryCount());

        jobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());

        jobInfo.setChildJobId(StringUtils.join(httpCronJobInfo.getChildJobIds(), ","));
        return this.add(jobInfo);
    }

    /**
     * 更新
     *
     * @param jobInfo 任务信息
     * @return {@link ReturnT}<{@link String}>
     */
    @PutMapping("/update")
    R<Boolean> update(@RequestBody XxlJobInfo jobInfo);

    /**
     * 删除
     *
     * @param id id
     * @return {@link ReturnT}<{@link String}>
     */
    @DeleteMapping("/remove")
    R<Boolean> remove(@RequestParam("id") Integer id);

    /**
     * 暂停
     *
     * @param id id
     * @return {@link ReturnT}<{@link String}>
     */
    @GetMapping("/stop")
    R<Boolean> pause(@RequestParam("id") Integer id);

    /**
     * 开始
     *
     * @param id id
     * @return {@link ReturnT}<{@link String}>
     */
    @GetMapping("/start")
    R<Boolean> start(@RequestParam("id") Integer id);

    /**
     * 触发器任务
     *
     * @param id            id
     * @param executorParam 执行器参数
     * @param addressList   地址列表
     * @return {@link ReturnT}<{@link String}>
     */
    @GetMapping("/trigger")
    R<Boolean> triggerJob(@RequestParam("id") Integer id,
                          @RequestParam("executorParam") String executorParam,
                          @RequestParam("addressList") String addressList);
}
