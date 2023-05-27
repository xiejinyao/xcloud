package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.vo.MenuVO;
import com.xjinyao.xcloud.common.core.tree.TreeNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @date 2017年11月9日23:33:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuTreeNode extends TreeNode<MenuTreeNode> implements Serializable {

    /**
     * 菜单图标
     */
    private String icon;

    private boolean spread = false;

    /**
     * 前端路由标识路径
     */
    private String path;

    /**
     * 路由缓冲
     */
    private String keepAlive;

    /**
     * 权限编码
     */
    private String permission;

    /**
     * 菜单类型 （0菜单 1按钮）
     */
    private String type;

    /**
     * 菜单标签
     */
    private String label;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 支持再授权
     */
    private boolean reAuth;

    public MenuTreeNode() {
    }

    public MenuTreeNode(int id, String name, int parentId) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.label = name;
    }

    public MenuTreeNode(int id, String name, MenuTreeNode parent) {
        this.id = id;
        this.parentId = parent.getId();
        this.name = name;
        this.label = name;
    }

    public MenuTreeNode(MenuVO menuVo) {
        this.id = menuVo.getMenuId();
        this.parentId = menuVo.getParentId();
        this.icon = menuVo.getIcon();
        this.name = menuVo.getName();
        this.path = menuVo.getPath();
        this.type = menuVo.getType();
        this.permission = menuVo.getPermission();
        this.label = menuVo.getName();
        this.sort = menuVo.getSort();
        this.keepAlive = menuVo.getKeepAlive();
    }

}
