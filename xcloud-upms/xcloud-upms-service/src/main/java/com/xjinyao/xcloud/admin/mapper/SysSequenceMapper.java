package com.xjinyao.xcloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjinyao.xcloud.admin.api.entity.SysSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统序列表
 *
 * @author 刘元林
 * @date 2021-03-30 18:42:29
 */
@Mapper
public interface SysSequenceMapper extends BaseMapper<SysSequence> {

    String getSequenceNum(@Param("name") String name);
}
