package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysApi;
import com.xjinyao.xcloud.admin.mapper.SysApiMapper;
import com.xjinyao.xcloud.admin.service.ISysApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * <p>
 * 系统接口表 服务实现类
 * </p>
 *
 * @author 谢进伟
 * @since 2020-10-14
 */
@Service
public class SysApiServiceImpl extends ServiceImpl<SysApiMapper, SysApi> implements ISysApiService {

    @Override
    public SysApi getByCode(String code) {
        LambdaQueryWrapper<SysApi> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysApi::getCode, code);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean status(String ids, Boolean status) {
        for (Object id : Arrays.asList(StringUtils.split(ids, ","))) {
            if (id == null) {
                continue;
            }
            SysApi sysApi = this.baseMapper.selectById(Long.parseLong(id.toString()));
            sysApi.setStatus(status);
            this.baseMapper.updateById(sysApi);
        }
        return true;
    }
}
