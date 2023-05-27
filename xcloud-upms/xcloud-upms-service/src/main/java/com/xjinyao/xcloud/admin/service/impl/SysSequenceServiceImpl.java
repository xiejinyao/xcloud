package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysSequence;
import com.xjinyao.xcloud.admin.mapper.SysSequenceMapper;
import com.xjinyao.xcloud.admin.service.ISysSequenceService;
import org.springframework.stereotype.Service;

/**
 * 系统序列表
 *
 * @author 刘元林
 * @date 2021-03-30 18:42:29
 */
@Service
public class SysSequenceServiceImpl extends ServiceImpl<SysSequenceMapper, SysSequence> implements ISysSequenceService {

    @Override
    public synchronized String getSequenceNum(String name) {
        return baseMapper.getSequenceNum(name);
    }
}
