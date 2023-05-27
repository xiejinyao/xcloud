package com.xjinyao.xcloud.common.core.redis.constant;

/**
 * @date 2020年01月01日
 * <p>
 * 缓存的key 常量
 */
public interface CacheConstants {


    /**
     * 前缀
     */
    String PREFIX = "xcloud_";

    /**
     * 任何
     */
    String ANY = "*";

    /**
     * oauth 缓存前缀
     */
    String PROJECT_OAUTH_ACCESS = PREFIX + "oauth:access:";

    /**
     * oauth 缓存令牌前缀
     */
    String PROJECT_OAUTH_TOKEN = PREFIX + "oauth:token:";

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = PREFIX + "DEFAULT_CODE_KEY:";

    /**
     * 菜单信息缓存
     */
    String MENU_DETAILS = PREFIX + "menu_details";

    /**
     * 用户信息缓存
     */
    String USER_DETAILS = PREFIX + "user_details";

    /**
     * 字典信息缓存
     */
    String DICT_DETAILS = PREFIX + "dict_details";

    /**
     * 组织数据缓存
     */
    String ORGANIZATION = PREFIX + "organization";

    /**
     * oauth 客户端信息
     */
    String CLIENT_DETAILS_KEY = PREFIX + "oauth:client:details";

    /**
     * 服务资源
     */
    String XCLOUD_SERVICE_RESOURCE = PREFIX + ":service:resource";

    /**
     * API资源
     */
    String XCLOUD_API_RESOURCE = PREFIX + ":api:resource";

    /**
     * 行政区域信息缓存
     */
    String AREA_DETAILS = PREFIX + "area_details";

    /**
     * 根据上级id获取组织信息缓存
     */
    String ORGANIZATION_BY_PARENT_ID = PREFIX + "organization_by_parent_id";

    /**
     * 参数缓存
     */
    String PARAMS_DETAILS = PREFIX + "params_details";

    /**
     * 应用资源缓存
     */
    String APPLICATION_RESOURCES = PREFIX + "application_resources";

    /**
     * 通过socket连接到服务器的客户端Id与SessionId缓存集合
     */
    String SOCKET_IO_CLIENT_ID_LIST = PREFIX + "socket_io_client_id_list";

    /**
     * 插件元数据缓存
     */
    String PLUGIN_METADATA = PREFIX + "plugin_metadata";


}
