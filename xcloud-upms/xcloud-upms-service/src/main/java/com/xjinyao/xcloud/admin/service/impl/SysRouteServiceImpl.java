package com.xjinyao.xcloud.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.entity.SysRoute;
import com.xjinyao.xcloud.admin.api.vo.SysRouteVO;
import com.xjinyao.xcloud.admin.mapper.SysRouteMapper;
import com.xjinyao.xcloud.admin.service.ISysRouteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统路由表 服务实现类
 * </p>
 *
 * @author 谢进伟
 * @since 2020-10-17
 */
@Service
public class SysRouteServiceImpl extends ServiceImpl<SysRouteMapper, SysRoute> implements ISysRouteService {

    @Override
    public List<SysRouteVO> listItem() {
        return this.baseMapper.listItem();
    }
}
