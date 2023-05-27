package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @date 2019/2/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends SysUser {

    /**
     * 角色ID
     */
    private List<Integer> role;

    /**
     * 组织Id
     */
    private String organizationId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 组织名称
     */
    private String organizationName;

}
