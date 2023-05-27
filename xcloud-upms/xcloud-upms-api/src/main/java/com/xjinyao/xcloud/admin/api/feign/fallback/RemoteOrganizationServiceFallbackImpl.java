package com.xjinyao.xcloud.admin.api.feign.fallback;

import com.xjinyao.xcloud.admin.api.feign.RemoteOrganizationService;
import com.xjinyao.xcloud.admin.api.vo.SysOrganizationVO;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 组织结构远程服务 fallback 实现
 *
 * @author 谢进伟
 * @createDate 2022/11/16 10:45
 */
@Slf4j
public class RemoteOrganizationServiceFallbackImpl implements RemoteOrganizationService {

    @Setter
    private Throwable cause;

    /**
     * 通过组织Id获取组织信息
     *
     * @param id   组织Id
     * @param from
     * @return
     */
    @Override
    public R<SysOrganizationVO> getById(String id, String from) {
        log.error("feign 通过组织Id获取组织信息 失败! id:{}", id, cause);
        return null;
    }

    /**
     * 通过组织Id集合批量获取组织信息
     *
     * @param ids  组织Id集合
     * @param from
     * @return
     */
    @Override
    public R<Map<String, SysOrganizationVO>> getByIds(List<String> ids, String from) {
        log.error("feign 通过组织Id获取组织信息 失败! id:{}", ids, cause);
        return R.failed(Collections.emptyMap());
    }

    /**
     * 获取所有子节点id
     *
     * @param parentId 组织id
     * @param from     从
     * @return {@link List}<{@link String}>
     */
    @Override
    public R<List<String>> getChildrenIds(String parentId, String from) {
        log.error("feign 获取所有子节点id 失败! parentId:{}", parentId, cause);
        return R.failed(Collections.emptyList());
    }

    /**
     * 获取同一级别的节点Id
     *
     * @param id              id
     * @param includeChildren 是否包含子节点
     * @param from            从
     * @return {@link R}<{@link List}<{@link String}>>
     */
    @Override
    public R<List<String>> getSiblingsChildrenIds(String id, Boolean includeChildren, String from) {
        log.error("feign 获取同一级别的节点Id 失败! id:{} includeChildren:{}", id, includeChildren, cause);
        return R.failed(Collections.emptyList());
    }
}
