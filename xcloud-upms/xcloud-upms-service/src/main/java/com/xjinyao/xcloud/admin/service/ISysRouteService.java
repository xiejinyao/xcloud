package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.entity.SysRoute;
import com.xjinyao.xcloud.admin.api.vo.SysRouteVO;

import java.util.List;

/**
 * <p>
 * 系统路由表 服务类
 * </p>
 *
 * @author 谢进伟
 * @since 2020-10-17
 */
public interface ISysRouteService extends IService<SysRoute> {

    /**
     * 查询微服务列表
     *
     * @return List<SysRouteVO>
     */
    List<SysRouteVO> listItem();

}
