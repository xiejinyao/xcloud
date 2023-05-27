package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysTasks;
import com.xjinyao.xcloud.admin.mapper.SysTasksMapper;
import com.xjinyao.xcloud.admin.service.ISysTasksService;
import org.springframework.stereotype.Service;

/**
 * 任务队列
 *
 * @author 谢进伟
 * @date 2021-03-18 11:06:30
 */
@Service
public class SysTasksServiceImpl extends ServiceImpl<SysTasksMapper, SysTasks> implements ISysTasksService {

}
