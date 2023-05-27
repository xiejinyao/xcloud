package com.xjinyao.xcloud.file.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 上传文件扩展信息
 * @createDate 2020/8/5 11:13
 */
@Data
@ApiModel(value = "上传文件扩展信息")
public class UploadExtFileInfoDTO implements Serializable {


    @ApiModelProperty("上传表单文件域name属性")
    private String fileInputName;
    @ApiModelProperty("自定义文件名")
    private String customName;
    @ApiModelProperty("业务编码")
    private String businessCode;
}

