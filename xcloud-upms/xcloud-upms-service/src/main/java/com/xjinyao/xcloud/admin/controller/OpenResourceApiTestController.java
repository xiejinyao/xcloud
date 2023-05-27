package com.xjinyao.xcloud.admin.controller;

import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.OpenApiResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 谢进伟
 * @description 对外开放资源Api接口测试控制器
 * @createDate 2021/2/26 17:30
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("os/api/test/")
@Api(value = "os/api/test/", tags = "对外开放资源Api接口测试")
public class OpenResourceApiTestController {

    @OpenApiResource(code = "OS_API_TEST_1", title = "普通参数测试", description = "普通参数测试")
    @ApiOperation(value = "普通参数测试", notes = "普通参数测试")
    @GetMapping("openResource/test1")
    public R<Boolean> test1(@RequestParam String p1,
                            @RequestParam String p2,
                            @RequestParam String[] p3,
                            @RequestParam List<String> p4) {
        log.info("请求成功:普通参数：p1=>{}\np2=>{}\np3=>{}\np4=>{}", p1, p2, p3, p4);
        return R.ok(Boolean.TRUE);
    }

    @OpenApiResource(code = "OS_API_TEST_2", title = "路径参数测试", description = "路径参数测试")
    @ApiOperation(value = "路径参数测试", notes = "路径参数测试")
    @GetMapping("openResource/test2/{p5}")
    public R<Boolean> test2(@RequestParam String p1,
                            @RequestParam String p2,
                            @RequestParam String[] p3,
                            @RequestParam List<String> p4,
                            @PathVariable String p5) {
        log.info("请求成功:普通参数：p1=>{}\np2=>{}\np3=>{}\np4=>{}\n路径参数:p5=>{}", p1, p2, p3, p4, p5);
        return R.ok(Boolean.TRUE);
    }

    @OpenApiResource(code = "OS_API_TEST_3", title = "混合参数测试", description = "混合参数测试，包含请求体类型测试")
    @ApiOperation(value = "RequestBody参数测试", notes = "RequestBody参数测试")
    @PostMapping("openResource/test3/{p5}")
    public R<Boolean> test2(@RequestParam String p1,
                            @RequestParam String p2,
                            @RequestParam String[] p3,
                            @RequestParam List<String> p4,
                            @PathVariable String p5,
                            @RequestBody String body) {
        log.info("请求成功:普通参数：p1=>{}\np2=>{}\np3=>{}\np4=>{}\n路径参数p5=>{}\nbody=>{}", p1, p2, p3, p4, p5, body);
        return R.ok(Boolean.TRUE);
    }
}
