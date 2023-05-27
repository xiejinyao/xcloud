package com.xjinyao.xcloud.common.core.constant;

/**
 * @date 2019/2/1
 */
public interface SecurityConstants {

    /**
     * 角色Id前缀
     */
    String ROLE_ID = "ROLE_ID_";
    /**
     * 角色编码前缀
     */
    String ROLE_CODE = "ROLE_CODE_";

    /**
     * 前缀
     */
    String PROJECT_PREFIX = "tcs_";

    /**
     * 项目的license
     */
    String PROJECT_LICENSE = "made by xcloud";


    /**
     * 微服务之间传递的唯一标识
     */
    String TRACE_ID = "hs-trace-id";

    /**
     * 内部
     */
    String FROM_IN = "Y";

    /**
     * web
     */
    String FROM_WEB = "WEB";

    /**
     * 标志
     */
    String FROM = "from";
    String PROJECT_ID = "projectId";

    /**
     * 默认登录URL
     */
    String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * grant_type
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * {bcrypt} 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * sys_oauth_client_details 表的字段，不包括client_id、client_secret
     */
    String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from sys_oauth_client_details";

    /**
     * 默认的查询语句
     */
    String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";

    /**
     * 按条件client_id 查询
     */
    String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

    /***
     * 资源服务器默认bean名称
     */
    String RESOURCE_SERVER_CONFIGURER = "resourceServerConfigurerAdapter";

    /**
     * 用户ID字段
     */
    String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    String DETAILS_USERNAME = "username";

    /**
     * 用户组织字段
     */
    String DETAILS_ORGANIZATION_ID = "organization_id";

    /**
     * 用户组织编码字段
     */
    String DETAILS_ORGANIZATION_CODE = "organization_code";

    /**
     * 数据权限
     */
    String DATA_PERMISSION = "data_permission";

    /**
     * 扩展数据
     */
    String EXTENDED_PARAMETERS = "extended_parameters";

    /**
     * 协议字段
     */
    String DETAILS_LICENSE = "license";

    /**
     * 验证码有效期,默认 60秒
     */
    long CODE_TIME = 60;

}
