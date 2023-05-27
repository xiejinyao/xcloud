package com.xjinyao.xcloud.file.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 谢进伟
 * @description 文件上传成功之后的响应性
 * @createDate 2020/12/2 17:09
 */
@Data
@ApiModel(value = "文件上传成功之后的响应性")
public class FileUploadSuccessVO implements Serializable {

    @ApiModelProperty("上传成功之后的所有文件的id集合")
    private List<Integer> ids;

    @ApiModelProperty("上传成功之后的所有文件基本信息")
    private List<UploadFileBaseInfoVO> infos;

}
