package com.xjinyao.xcloud.admin.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 谢进伟
 * @description 设备导入任务
 * @createDate 2021/3/4 9:09
 */
@Data
@Builder
public class DataImportTask implements Serializable {


    /**
     * 任务id
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 创建人Id
     */
    private Integer adminUserId;

    /**
     * 导入文件Id
     */
    private Integer fileId;

    /**
     * 数据处理队列绑定的交换机
     */
    private String exchage;

    /**
     * 数据处理队列绑定交换机的路由
     */
    private String routingKey;

    /**
     * 扩展数据
     */
    private Map<String, Object> extendData;

    @Tolerate
    public DataImportTask() {

    }
}
