package com.xjinyao.xcloud.xxl.job.admin.api.feign.fallback;

import cn.hutool.db.PageResult;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.xxl.job.admin.api.feign.RemoteJobInfoService;
import com.xjinyao.xcloud.xxl.job.admin.api.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @date 2019/2/1
 */
@Slf4j
public class RemoteJobInfoServiceFallbackImpl implements RemoteJobInfoService {

    @Setter
    private Throwable cause;

    /**
     * 任务列表
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
    @Override
    public R<PageResult<XxlJobInfo>> pageList(Integer start, Integer length, Integer jobGroup, Integer triggerStatus, String jobDesc, String executorHandler, String author) {
        return R.failed("查询失败！");
    }

    /**
     * 添加
     *
     * @param jobInfo 任务信息
     * @return {@link ReturnT}<{@link String}>
     */
    @Override
    public R<String> add(XxlJobInfo jobInfo) {
        return R.failed("新增失败！");
    }

    /**
     * 更新
     *
     * @param jobInfo 任务信息
     * @return {@link ReturnT}<{@link String}>
     */
    @Override
    public R<Boolean> update(XxlJobInfo jobInfo) {
        return R.failed(Boolean.FALSE, "修改失败！");
    }

    /**
     * 删除
     *
     * @param id id
     * @return {@link ReturnT}<{@link String}>
     */
    @Override
    public R<Boolean> remove(Integer id) {
        return R.failed(Boolean.FALSE, "删除失败！");
    }

    /**
     * 暂停
     *
     * @param id id
     * @return {@link ReturnT}<{@link String}>
     */
    @Override
    public R<Boolean> pause(Integer id) {
        return R.failed(Boolean.FALSE, "暂停失败！");
    }

    /**
     * 启动
     *
     * @param id id
     * @return {@link ReturnT}<{@link String}>
     */
    @Override
    public R<Boolean> start(Integer id) {
        return R.failed(Boolean.FALSE, "启动失败！");
    }

    /**
     * 触发器任务
     *
     * @param id            id
     * @param executorParam 执行器参数
     * @param addressList   地址列表
     * @return {@link ReturnT}<{@link String}>
     */
    @Override
    public R<Boolean> triggerJob(Integer id, String executorParam, String addressList) {
        return R.failed(Boolean.FALSE, "触发失败！");
    }
}
