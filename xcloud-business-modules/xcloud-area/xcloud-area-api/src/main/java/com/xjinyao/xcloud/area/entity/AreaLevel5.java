package com.xjinyao.xcloud.area.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.thinwonton.mybatis.metamodel.core.annotation.GenMetaModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区域(第五级)
 *
 * @author 谢进伟
 * @date 2020-05-05 11:35:14
 */
@Data
@GenMetaModel
@TableName("sys_area_level_5")
@EqualsAndHashCode(callSuper = true)
@ApiModel(parent = Area.class)
public class AreaLevel5 extends Area {
    private static final long serialVersionUID = 1L;
}
