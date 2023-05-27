package com.xjinyao.xcloud.admin.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.xjinyao.xcloud.admin.service.CacheEvictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mengjiajie
 * @description
 * @createDate 2023/4/6 17:50
 */

@Api(tags = "清除缓存")
@RestController
@AllArgsConstructor
@RequestMapping("/cacheEvict")
@ApiSupport(author = "mengjiajie", order = 1)
public class CacheEvictController {

    private final CacheEvictService cacheEvictService;
    @DeleteMapping("/cacheEvictByKey")
    @ApiOperation(value = "通过主键查询单条业户客户基础信息表数据", produces = MediaType.APPLICATION_JSON_VALUE)
    public void evictHouseTreeCache(){
        cacheEvictService.evictOrganization();
    }

}
