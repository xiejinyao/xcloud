package com.xjinyao.xcloud.file.api.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 谢进伟
 * @description 文件转换结果VO
 * @createDate 2020/6/18 11:34
 */
@Data
@ApiModel(value = "文件转换结果")
public class ConvertFileVO extends SysFileVO implements Serializable {

    /**
     * 转换完之后的文件访问路径
     */
    private String convertFileUrl;
}
