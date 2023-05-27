package com.xjinyao.xcloud.admin.controller;
import com.xjinyao.xcloud.common.core.redis.service.DefaultRedisService;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 谢进伟
 * @description 缓存管理
 * @createDate 2022/1/9 11:01
 */
@AllArgsConstructor
@Api(tags = "缓存管理")
@RestController
@RequestMapping("/cacheManager")
public class CacheManagerController {

    private final RedisService redisService;
    private final DefaultRedisService defaultRedisService;

    /**
     * 获取指定匹配模式的所有缓存key
     *
     * @param keyPattern 键的匹配模式
     */
    @ApiOperation(value = "获取指定匹配模式的所有缓存key", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("keys")
    public R<Set<String>> keys(@ApiParam(value = "键的匹配模式", required = true)
                               @RequestParam String keyPattern) {
        if (StringUtils.isBlank(keyPattern)) {
            return R.failed("请设置匹配模式");
        }
        Set<String> result = new HashSet<String>() {{
            this.addAll(redisService.keys(keyPattern));
            this.addAll(defaultRedisService.keys(keyPattern));
        }};

        return R.ok(result, "获取成功!");
    }

    /**
     * 获取指定key的值
     *
     * @param key 键的匹配模式
     */
    @ApiOperation(value = "获取指定key的值", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("get")
    public R<Set<Object>> get(@ApiParam(value = "键的匹配模式", required = true)
                               @RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return R.failed("请设置key");
        }
        Set<Object> result = new HashSet<Object>() {{
            this.add(redisService.get(key));
            this.add(defaultRedisService.get(key));
        }};

        return R.ok(result, "获取成功!");
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return Boolean
     */
    @ApiOperation(value = "指定缓存失效时间", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("expire")
    public R<Boolean> expire(@ApiParam(value = "键", required = true)
                             @RequestParam String key,
                             @ApiParam(value = "时间(秒)", required = true)
                             @RequestParam Long time) {
        try {
            if (time > 0) {
                if (redisService.hasKey(key)) {
                    redisService.expire(key, time);
                } else if (defaultRedisService.hasKey(key)) {
                    defaultRedisService.expire(key, time);
                } else {
                    return R.failed(Boolean.FALSE, "未找到指定键的缓存");
                }
            } else {
                return R.failed(Boolean.FALSE, "time参数应该大于零");
            }
            return R.ok(Boolean.TRUE);
        } catch (Exception e) {
            return R.failed(Boolean.FALSE, StringUtils.throwableToString(e));
        }
    }

    /**
     * 精确删除指定Key缓存
     *
     * @param key 键
     * @return Boolean
     */
    @ApiOperation(value = "精确删除指定Key缓存", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("delByKey")
    public R<Boolean> deleteByKey(@ApiParam(value = "键", required = true)
                                  @RequestParam String key) {
        if (redisService.hasKey(key)) {
            redisService.del(key);
        }
        if (defaultRedisService.hasKey(key)) {
            defaultRedisService.del(key);
        }
        return R.ok(Boolean.TRUE, "删除成功！");
    }

    /**
     * 删除缓存
     *
     * @param keyPattern 键的匹配模式
     * @return Boolean
     */
    @ApiOperation(value = "删除缓存", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("delByKeyPattern")
    public R<Boolean> deleteByKeyPattern(@ApiParam(value = "键的匹配模式", required = true)
                                         @RequestParam String keyPattern) {
        Set<String> keys = redisService.keys(keyPattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisService.del(keys.toArray(new String[]{}));
        }

        Set<String> keys1 = defaultRedisService.keys(keyPattern);
        if (CollectionUtils.isNotEmpty(keys1)) {
            defaultRedisService.del(keys.toArray(new String[]{}));
        }

        return R.ok(Boolean.TRUE, "删除成功！");
    }

    /**
     * 删除hash表中的值
     *
     * @param key   键 不能为 null
     * @param items 项 可以使多个不能为 null
     * @return Boolean
     */
    @ApiOperation(value = "删除hash表中的值", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("hdel")
    public R<Boolean> hdel(@ApiParam(value = "键的匹配模式", required = true)
                           @RequestParam String key,
                           @ApiParam(value = "项")
                           @RequestParam List<String> items) {
        redisService.hdel(key, items.toArray());
        defaultRedisService.hdel(key, items.toArray());
        return R.ok(Boolean.TRUE, "删除成功！");
    }

    /**
     * 哈希存储获取数据
     *
     * @param key 键
     * @param key 项
     * @return Boolean
     */
    @ApiOperation(value = "哈希存储获取数据", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("hget")
    public R<Result> hget(@ApiParam(value = "键", required = true)
                          @RequestParam String key,
                          @ApiParam(value = "项", required = true)
                          @RequestParam String item) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(item)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.hget(key, item))
                    .valueForDefaultSerialization(defaultRedisService.hget(key, item))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 获取 hashKey对应的所有键值
     *
     * @param key 键
     * @return Boolean
     */
    @ApiOperation(value = " 获取 hashKey对应的所有键值", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("hmget")
    public R<Result> hmget(@ApiParam(value = "键", required = true)
                           @RequestParam String key) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.hmget(key))
                    .valueForDefaultSerialization(defaultRedisService.hmget(key))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 根据 key获取 Set中的所有值
     *
     * @param key 键
     * @return Boolean
     */
    @ApiOperation(value = " 获取 hashKey对应的所有键值", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("sGet")
    public R<Result> sGet(@ApiParam(value = "键", required = true)
                          @RequestParam String key) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.sGet(key))
                    .valueForDefaultSerialization(defaultRedisService.sGet(key))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 根据 key获取 Set中的所有值
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    @ApiOperation(value = "根据 key获取 Set中的所有值", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("sHasKey")
    public R<Result> sHasKey(@ApiParam(value = "键", required = true)
                             @RequestParam String key,
                             @ApiParam(value = "值", required = true)
                             @RequestParam String value) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.sHasKey(key, value))
                    .valueForDefaultSerialization(defaultRedisService.sHasKey(key, value))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return Boolean
     */
    @ApiOperation(value = "获取set缓存的长度", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("sGetSetSize")
    public R<Result> sGetSetSize(@ApiParam(value = "键", required = true)
                                 @RequestParam String key) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.sGetSetSize(key))
                    .valueForDefaultSerialization(defaultRedisService.sGetSetSize(key))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 移除值为value的
     *
     * @param key   键
     * @param value 值
     * @return Boolean
     */
    @ApiOperation(value = "移除值为value的", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("setRemove")
    public R<Result> setRemove(@ApiParam(value = "键", required = true)
                               @RequestParam String key,
                               @ApiParam(value = "值", required = true)
                               @RequestParam String value) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.setRemove(key, value))
                    .valueForDefaultSerialization(defaultRedisService.setRemove(key, value))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 移除值为value的
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return Boolean
     */
    @ApiOperation(value = "移除值为value的", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("lGet")
    public R<Result> lGet(@ApiParam(value = "键", required = true)
                          @RequestParam String key,
                          @ApiParam(value = "开始", required = true)
                          @RequestParam Long start,
                          @ApiParam(value = "结束 0 到 -1代表所有值", required = true)
                          @RequestParam Long end) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.lGet(key, start, end))
                    .valueForDefaultSerialization(defaultRedisService.lGet(key, start, end))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 获取list缓存的长度
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return Boolean
     */
    @ApiOperation(value = "获取list缓存的长度", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("lGetIndex")
    public R<Result> lGetIndex(@ApiParam(value = "键", required = true)
                               @RequestParam String key,
                               @ApiParam(value = "索引 index>=0时， 0 表头，1 第二个元素，依次类推；" +
                                       "index<0时，-1，表尾，-2倒数第二个元素，依次类推", required = true)
                               @RequestParam Long index) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.lGetIndex(key, index))
                    .valueForDefaultSerialization(defaultRedisService.lGetIndex(key, index))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return Boolean
     */
    @ApiOperation(value = "获取list缓存的长度", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("lGetListSize")
    public R<Result> lGetListSize(@ApiParam(value = "键", required = true)
                                  @RequestParam String key) {
        if (StringUtils.isNotEmpty(key)) {
            return R.ok(Result.builder()
                    .valueForJsonSerialization(redisService.lGetListSize(key))
                    .valueForDefaultSerialization(defaultRedisService.lGetListSize(key))
                    .build(), "获取成功！");
        }
        return R.failed("获取失败！");
    }

    @Data
    @Builder
    public static class Result<T> {
        public T valueForJsonSerialization;
        public T valueForDefaultSerialization;
    }

}
