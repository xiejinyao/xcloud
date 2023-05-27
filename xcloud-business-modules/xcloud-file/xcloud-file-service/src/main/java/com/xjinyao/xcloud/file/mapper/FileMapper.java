package com.xjinyao.xcloud.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjinyao.xcloud.file.api.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件库
 *
 * @author 谢进伟
 * @date 2020-05-15 14:49:59
 */
@Mapper
public interface FileMapper extends BaseMapper<SysFile> {

}
