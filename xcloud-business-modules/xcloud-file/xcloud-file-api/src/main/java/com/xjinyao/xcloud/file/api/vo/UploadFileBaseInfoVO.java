package com.xjinyao.xcloud.file.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 上传成功之后返回的文件信息
 * @createDate 2020/12/02 17:03
 */
@Data
@AllArgsConstructor
@ApiModel(value = "文件转换结果")
public class UploadFileBaseInfoVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty("原始名称")
    private String name;

    @ApiModelProperty("网络路径，可通过改路径直接访问文件")
    private String url;
}
