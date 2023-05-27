package com.xjinyao.xcloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务队列
 *
 * @author 谢进伟
 * @date 2021-03-18 11:06:30
 */
@Mapper
public interface SysTasksMapper extends BaseMapper<SysTasks> {

}
