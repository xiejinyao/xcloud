package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysSequence;

/**
 * 系统序列表
 *
 * @author 刘元林
 * @date 2021-03-30 18:42:29
 */
public interface ISysSequenceService extends IService<SysSequence> {

    String getSequenceNum(String name);
}
