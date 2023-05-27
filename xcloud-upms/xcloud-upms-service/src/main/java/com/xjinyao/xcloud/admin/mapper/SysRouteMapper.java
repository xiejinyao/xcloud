package com.xjinyao.xcloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjinyao.xcloud.admin.api.entity.SysRoute;
import com.xjinyao.xcloud.admin.api.vo.SysRouteVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统路由表 Mapper 接口
 * </p>
 *
 * @author 谢进伟
 * @since 2020-10-17
 */
@Mapper
public interface SysRouteMapper extends BaseMapper<SysRoute> {

    /**
     * 微服务视图列表
     *
     * @return List<SysRouteVO>
     */
    List<SysRouteVO> listItem();

}
