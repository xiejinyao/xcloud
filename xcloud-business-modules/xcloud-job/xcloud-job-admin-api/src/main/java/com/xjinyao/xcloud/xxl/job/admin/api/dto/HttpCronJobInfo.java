package com.xjinyao.xcloud.xxl.job.admin.api.dto;

import com.xjinyao.xcloud.xxl.job.admin.api.enums.ExecutorRouteStrategyEnum;
import com.xjinyao.xcloud.xxl.job.admin.api.enums.MisfireStrategyEnum;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * @author 谢进伟
 * @createDate 2023/2/10 17:02
 */
@Data
@Builder
public class HttpCronJobInfo {

    /**
     * 执行器主键ID
     */
    private int jobGroup;

    /**
     * 负责人
     */
    private String author;

    /**
     * 报警邮件
     */
    private String alarmEmail;

    /**
     * 调度配置，值含义取决于调度类型
     */
    private String scheduleConf;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * 调度过期策略
     */
    private MisfireStrategyEnum misfireStrategy;

    /**
     * 任务执行超时时间，单位秒
     */
    private int executorTimeout;

    /**
     * 失败重试次数
     */
    private int executorFailRetryCount;

    /**
     * 执行器路由策略
     */
    private ExecutorRouteStrategyEnum executorRouteStrategy;

    /**
     * 阻塞处理策略
     */
    private ExecutorBlockStrategyEnum executorBlockStrategy;

    /**
     * 子任务ID
     */
    private List<Integer> childJobIds;

    /**
     * 处理程序参数
     */
    private HttpJobHandlerParams handlerParams;

    @Tolerate
    public HttpCronJobInfo() {

    }
}
