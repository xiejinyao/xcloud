package com.xjinyao.xcloud.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @since 2019/2/1
 */
@Data
@GenMetaModel
@ApiModel("用户信息")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Integer userId;

    /**
     * 第三方Id
     */
    @TableField(value = "third_party_id")
    @ApiModelProperty(value = "第三方Id")
    private String thirdPartyId;

    /**
     * 第三方平台名称
     */
    @TableField(value = "sources")
    @ApiModelProperty(value = "第三方平台名称")
    private String sources;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空!")
    @ApiModelProperty(value = "用户名")
    private String username;


    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空!")
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 随机盐
     */
    @JsonIgnore
    @ApiModelProperty(value = "随机盐")
    private String salt;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 锁定标记
     */
    @ApiModelProperty(value = "锁定标记")
    @TableField(value = "lock_flag")
    private Boolean lockFlag;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像地址")
    private String avatar;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 组织ID
     */
    @ApiModelProperty(value = "用户所属组织id")
    @TableField(value = "organization_id")
    private String organizationId;
    /**
     * 0-正常，1-删除
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    private String realname;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}
