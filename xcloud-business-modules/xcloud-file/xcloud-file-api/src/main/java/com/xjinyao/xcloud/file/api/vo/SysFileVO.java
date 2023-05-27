package com.xjinyao.xcloud.file.api.vo;

import com.xjinyao.xcloud.file.api.entity.SysFile;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author 谢进伟
 * @description 文件
 * @createDate 2020/5/22 18:47
 */
@Data
@ApiModel(parent = SysFile.class)
public class SysFileVO extends SysFile {
}
