package com.xjinyao.xcloud.admin.api.dto;

import com.xjinyao.xcloud.admin.api.entity.SysDict;
import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import lombok.Data;

import java.util.List;

/**
 * @author 谢进伟
 * @createDate 2022/12/6 09:56
 */
@Data
public class GenDictEnumDto {

    /**
     * 编码
     */
    private String encoding;

    /**
     * 模块路径
     */
    private String modulePath;

    /**
     * 包名
     */
    private String packageName;
    /**
     * 枚举文件名称
     */
    private String enumFileName;

    /**
     * 项数据类型
     */
    private String itemDataType;

    /**
     * 字典分组信息
     */
    private SysDict dict;

    /**
     * dict 项
     */
    private List<SysDictItem> dictItems;
}
