package com.xjinyao.xcloud.gateway.test;

import org.junit.Test;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 谢进伟
 * @description 请求路径匹配
 * @createDate 2020/11/19 14:46
 */
public class RequestPathMatchTest {

    @Test
    public void t1() {
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        String path = "/a/1";

        List<String> patternList = new ArrayList<>();
        patternList.add("/a/*");
        patternList.add("/a/*/*");
        patternList.add("/a/*/*/*");

        for (String pattern : patternList) {
            boolean match = antPathMatcher.match(pattern, path);
            System.out.println(match);
            if (match) {
                System.out.println(pattern + ":" + antPathMatcher.extractPathWithinPattern(pattern, path));
            }
            System.out.println("-------------------");
        }
    }
}
