package com.xjinyao.xcloud.admin.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 用户基本信息DTO
 * @createDate 2020/12/3 15:34
 */
@Data
@ApiModel("用户基本信息DTO")
public class UserBaseInfoDTO implements Serializable {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "主键id")
    private Integer userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "原密码")
    private String password;

    @ApiModelProperty(value = "新密码")
    private String newpassword1;
}
