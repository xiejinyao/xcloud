package com.xjinyao.xcloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 组织管理 Mapper 接口
 * </p>
 *
 * @since 2019/2/1
 */
@Mapper
public interface SysOrganizationMapper extends BaseMapper<SysOrganization> {


    /**
     * 查询指定组织下子节点的数量
     *
     * @param ids 组织id集合
     * @return
     */
    @MapKey("parent_id")
    Map<String, Map<String, Long>> hasChildren(@Param("ids") List<String> ids);
}
