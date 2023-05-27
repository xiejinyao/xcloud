package com.xjinyao.xcloud.admin.api.feign;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.feign.factory.RemoteOrganizationServiceFallbackFactory;
import com.xjinyao.xcloud.admin.api.vo.SysOrganizationVO;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import com.xjinyao.xcloud.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 组织结构远程服务
 *
 * @author 谢进伟
 * @createDate 2022/11/16 10:40
 */
@FeignClient(contextId = "remoteOrganizationService", value = ServiceNameConstants.UMPS_SERVICE,
        path = ControllerMapping.SYS_ORGANIZATION_CONTROLLER_MAPPING,
        fallbackFactory = RemoteOrganizationServiceFallbackFactory.class)
public interface RemoteOrganizationService {


    /**
     * 通过组织Id获取组织信息
     *
     * @param id 组织Id
     * @return
     */
    @GetMapping("/get/{id}")
    R<SysOrganizationVO> getById(@PathVariable("id") String id, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 通过组织Id集合批量获取组织信息
     *
     * @param ids 组织Id集合
     * @return
     */
    @PostMapping("/getByIds")
    R<Map<String, SysOrganizationVO>> getByIds(@RequestBody List<String> ids, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取所有子节点id
     *
     * @param parentId 组织id
     * @param from     从
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/getChildrenIds")
    R<List<String>> getChildrenIds(@RequestParam("parentId") String parentId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取同一级别的节点Id
     *
     * @param id              id
     * @param includeChildren 是否包含子节点
     * @param from            从
     * @return {@link R}<{@link List}<{@link String}>>
     */
    @GetMapping("/getSiblingsChildrenIds")
    R<List<String>> getSiblingsChildrenIds(@RequestParam("id") String id,
                                           @RequestParam(value = "includeChildren", defaultValue = "false")
                                           Boolean includeChildren,
                                           @RequestHeader(SecurityConstants.FROM) String from);
}
