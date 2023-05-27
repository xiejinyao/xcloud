package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 客户端信息
 * </p>
 *
 * @since 2019/2/1
 */
@Data
@GenMetaModel
@EqualsAndHashCode(callSuper = true)
public class SysOauthClientDetails extends Model<SysOauthClientDetails> {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端ID
     */
    @NotBlank(message = "client_id 不能为空")
    @TableId(value = "client_id", type = IdType.INPUT)
    @ApiModelProperty(value = "客户端id")
    private String clientId;

    /**
     * 客户端密钥
     */
    @NotBlank(message = "client_secret 不能为空")
    @ApiModelProperty(value = "客户端密钥")
    @TableField(value = "client_secret")
    private String clientSecret;

    /**
     * 资源ID
     */
    @ApiModelProperty(value = "资源id列表")
    @TableField(value = "resource_ids")
    private String resourceIds;

    /**
     * 作用域
     */
    @NotBlank(message = "scope 不能为空")
    @ApiModelProperty(value = "作用域")
    private String scope;

    /**
     * 授权方式（A,B,C）
     */
    @ApiModelProperty(value = "授权方式")
    @TableField(value = "authorized_grant_types")
    private String authorizedGrantTypes;

    /**
     * 回调地址
     */
    @ApiModelProperty(value = "回调地址")
    @TableField(value = "web_server_redirect_uri")
    private String webServerRedirectUri;

    /**
     * 权限
     */
    @ApiModelProperty(value = "权限列表")
    private String authorities;

    /**
     * 请求令牌有效时间
     */
    @ApiModelProperty(value = "请求令牌有效时间")
    @TableField(value = "access_token_validity")
    private Integer accessTokenValidity;

    /**
     * 刷新令牌有效时间
     */
    @ApiModelProperty(value = "刷新令牌有效时间")
    @TableField(value = "refresh_token_validity")
    private Integer refreshTokenValidity;

    /**
     * 扩展信息
     */
    @ApiModelProperty(value = "扩展信息")
    @TableField(value = "additional_information")
    private String additionalInformation;

    /**
     * 是否自动放行
     */
    @ApiModelProperty(value = "是否自动放行")
    private String autoapprove;

}
