package com.xjinyao.xcloud.admin.api.constants;

/**
 * 业务接口顶级controller映射
 *
 * @author 谢进伟
 * @description 控制器映射前缀常量配置
 * @createDate 2021/2/25 16:29
 */
public class ControllerMapping {

    /**
     * 用户管理
     */
    public final static String SYS_USER_CONTROLLER_MAPPING = "/inner/user";
    /**
     * 角色数据权限
     */
    public final static String SYS_USER_DATA_PERMISSION_CONTROLLER_MAPPING = "/inner/sysUserDataPermission";

    /**
     * 日志管理
     */
    public final static String SYS_LOG_CONTROLLER_MAPPING = "/inner/log";
    /**
     * 字典管理
     */
    public final static String SYS_DICT_CONTROLLER_MAPPING = "/inner/dict";

    /**
     * 业务变更日志
     */
    public final static String SYS_BUSINESS_LOG_CONTROLLER_MAPPING = "/inner/business/log";

    /**
     * 应用资源管理
     */
    public final static String SYS_APPLICATION_CONTROLLER_MAPPING = "/inner/application";

    /**
     * 应用资源管理
     */
    public final static String SYS_TASKS_CONTROLLER_MAPPING = "/inner/sysTasks";

    /**
     * 系统序列
     */
    public final static String SYS_SEQUENCE_CONTROLLER_MAPPING = "/inner/sequence";

    /**
     * 组织结构
     */
    public final static String SYS_ORGANIZATION_CONTROLLER_MAPPING = "/inner/organization";
}
