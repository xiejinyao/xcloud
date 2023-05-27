package com.xjinyao.xcloud.common.core.tree;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 树节点
 *
 * @author 谢进伟
 * @date 2022/11/21
 */
@SuperBuilder
public class TreeNode<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    @Setter
    @Getter
    protected Serializable id;

    /**
     * 父id
     */
    @Setter
    @Getter
    protected Serializable parentId;

    /**
     * 名字
     */
    @Setter
    @Getter
    protected String name;

    /**
     * 子节点
     */
    @Getter
    protected List<T> children;


    /**
     * 有子节点
     */
    @Getter
    @Setter
    protected Boolean hasChildren;

    /**
     * 是否为叶子节点
     */
    protected Boolean leaf;

    /**
     * 细节
     */
    @Setter
    @Getter
    protected Object details;


    @Tolerate
    public TreeNode() {

    }

    public void add(T node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(node);
        this.hasChildren = true;
        this.leaf = false;
        children.add(node);
    }

    public void setChildren(List<T> children) {
        this.children = children;
        this.hasChildren = true;
        this.leaf = false;
    }

    public Boolean getHasChildren() {
        if (this.children == null && this.hasChildren == null && this.leaf == null) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(this.children)) {
            return true;
        }
        return Optional.ofNullable(this.hasChildren).orElse(Boolean.TRUE);
    }

    public Boolean getLeaf() {
        if (this.children == null && this.hasChildren == null && this.leaf == null) {
            return true;
        }
        if (CollectionUtils.isEmpty(this.children) && BooleanUtils.isFalse(this.hasChildren)) {
            return true;
        }
        return Optional.ofNullable(this.leaf).orElse(Boolean.FALSE);
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", children=" + children +
                ", hasChildren=" + getHasChildren() +
                ", leaf=" + getLeaf() +
                ", details=" + details +
                '}';
    }
}
