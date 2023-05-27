package com.xjinyao.xcloud.common.core.tree;

import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;


/**
 * 默认树节点
 *
 * @author 谢进伟
 * @createDate 2022/11/21
 */
@SuperBuilder
public class DefaultTreeNode extends TreeNode<DefaultTreeNode> {

    @Tolerate
    public DefaultTreeNode() {

    }
}
