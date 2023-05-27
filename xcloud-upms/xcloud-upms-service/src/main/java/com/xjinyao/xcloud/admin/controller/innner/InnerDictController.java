package com.xjinyao.xcloud.admin.controller.innner;

import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.service.SysDictItemService;
import com.xjinyao.xcloud.admin.service.SysDictService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @since 2019-03-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/inner/dict")
@Api(value = "dict", tags = "字典管理模块")
public class InnerDictController {

    private final SysDictItemService sysDictItemService;

    private final SysDictService sysDictService;

    /**
     * 通过字典类型查找字典
     *
     * @param type 类型
     * @return 同类型字典
     */
    @GetMapping("/type/{type}")
    @ApiOperation(value = "通过字典类型查找字典", notes = "通过字典类型查找字典")
    @Cacheable(value = CacheConstants.DICT_DETAILS, key = "'getDictByType_'+#type")
    @Inner
    public R<List<SysDictItem>> getDictByType(@PathVariable String type) {
        return R.ok(getDicts(Collections.singletonList(type)));
    }

    private List<SysDictItem> getDicts(List<String> type) {
        return Optional.ofNullable(sysDictItemService.lambdaQuery()
                        .in(SysDictItem::getType, type).list())
                .orElse(Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(SysDictItem::getSort))
                .collect(Collectors.toList());
    }
}
