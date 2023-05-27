package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysBlacklist;
import com.xjinyao.xcloud.admin.mapper.SysBlacklistMapper;
import com.xjinyao.xcloud.admin.service.ISysBlacklistService;
import com.xjinyao.xcloud.core.rule.po.BlackList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * <p>
 * 系统黑名单表 服务实现类
 * </p>
 *
 * @author 谢进伟
 * @since 2020-08-26
 */
@Service
@AllArgsConstructor
public class SysBlacklistServiceImpl extends ServiceImpl<SysBlacklistMapper, SysBlacklist> implements ISysBlacklistService {

    private final IRuleCacheService ruleCacheService;

    @Override
    public boolean status(String ids, Boolean status) {
        for (Object id : Arrays.asList(StringUtils.split(ids, ","))) {
            if (id == null) {
                continue;
            }
            SysBlacklist sysBlacklist = this.baseMapper.selectById(Long.parseLong(id.toString()));
            sysBlacklist.setStatus(status);
            this.baseMapper.updateById(sysBlacklist);

            BlackList blackList = new BlackList();
            BeanUtils.copyProperties(sysBlacklist, blackList);
            ruleCacheService.cacheBlackList(blackList);
        }
        return true;
    }
}
