package com.xjinyao.xcloud.admin.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @date 2020/2/10
 */
@Data
@ApiModel(value = "前端角色展示对象")
public class RoleMenuDTO implements Serializable {

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 菜单列表
     */
    private List<RoleMenuInfo> menuIds;

    /**
     * 角色菜单信息
     *
     * @author 谢进伟
     * @createDate 2023/04/06
     */
    @Data
    public static class RoleMenuInfo{

        /**
         * 角色id
         */
        private Long menuId;

        /**
         * 菜单id
         */
        private Boolean reAuth;
    }

}
