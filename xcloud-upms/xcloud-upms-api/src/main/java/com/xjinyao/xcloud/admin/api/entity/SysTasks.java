package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import java.time.LocalDateTime;

/**
 * 任务队列
 *
 * @author 谢进伟
 * @date 2021-03-18 11:06:30
 */
@Data
@Builder
@GenMetaModel
@TableName("sys_tasks")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "任务队列")
public class SysTasks extends Model<SysTasks> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键")
    @TableField(value = "id")
    private Integer id;
    /**
     * 创建人Id
     */
    @ApiModelProperty(value = "创建人Id")
    @TableField(value = "admin_user_id")
    private Integer adminUserId;
    /**
     * 任务编码
     */
    @ApiModelProperty(value = "任务编码")
    @TableField(value = "task_code")
    private String taskCode;
    /**
     * 任务编号
     */
    @ApiModelProperty(value = "任务编号")
    @TableField(value = "task_num")
    private String taskNum;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(value = "name")
    private String name;
    /**
     * 任务创建时间
     */
    @ApiModelProperty(value = "任务创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 任务开始处理时间
     */
    @ApiModelProperty(value = "任务开始处理时间")
    @TableField(value = "begin_time")
    private LocalDateTime beginTime;
    /**
     * 任务处理结束时间
     */
    @ApiModelProperty(value = "任务处理结束时间")
    @TableField(value = "end_time")
    private LocalDateTime endTime;
    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    @TableField(value = "config_json")
    private String configJson;
    /**
     * 任务处理结果
     */
    @ApiModelProperty(value = "任务处理结果")
    @TableField(value = "result")
    private String result;
    /**
     * 任务状态（1：待处理，2：处理中，3：成功完成处理，4：处理过程发生异常）
     */
    @ApiModelProperty(value = "任务状态（1：待处理，2：处理中，3：成功完成处理，4：处理过程发生异常）")
    @TableField(value = "status")
    private Integer status;
    /**
     * 版本
     */
    @ApiModelProperty(value = "版本")
    @TableField(value = "version")
    private Integer version;
    /**
     *
     */
    @ApiModelProperty(value = "")
    @TableField(value = "remark")
    private String remark;

    @Tolerate
    public SysTasks() {

    }
}
