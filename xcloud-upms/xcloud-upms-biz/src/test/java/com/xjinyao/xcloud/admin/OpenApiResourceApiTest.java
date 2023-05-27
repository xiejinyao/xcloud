package com.xjinyao.xcloud.admin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.xjinyao.xcloud.common.security.util.SignUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;

/**
 * @author 谢进伟
 * @description 开放资源测试, 本地测试请先启动好服务:platform-register、platform-gateway、platform-upms
 * @createDate 2021/2/26 17:45
 */
@Slf4j
public class OpenApiResourceApiTest {

    private String appId = "5209FA1E35E04DBFAB968346B579C0CBB9358E4D479943B9B4989BD7B9880AF0";
    private String appSecret = "153aFb9C6876419Ba2e113E46e40F5554267A26579c245dfB17Bf3f132Ac1A7EC5C880FB8f5744Dc93a53aad654571c43199079207B345D3bea5F8124903FEda";

    @Test
    @SneakyThrows
    public void test1() {
        String applicationCode = "SHEN_ZHEN_LUO_HU";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //普通参数测试
        String p1 = "张珊";
        String p2 = "123456";
        String p3 = "34,23";
        String p4 = "a,b";

        //业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);

            put("p1", p1);
            put("p2", p2);
            put("p3", p3);
            put("p4", p4);
        }};

        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, null, timestamp, randomStr, appId, appSecret));

        String url = "http://127.0.0.1:9999/admin/os/api/test/openResource/test1";
        try {
            String result = HttpUtil.get(url, paramMap);
            showLog(paramMap, url, result);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test1 " + e.getMessage());
        }
    }

    @Test
    @SneakyThrows
    public void test8() {
        String applicationCode = "HAIDE_ALARM_DEVICE";
        long timestamp = 1625308106000l;
        String randomStr = timestamp + "";

        //业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);
            put("imei", "864784041632922");
        }};
        String myAppId = "E6BEE8751B9742A3A9CF6D844E75293A67F4F8624FEF4FAE8BCC26874D6BDBA7";
        String myAppSecret = "3572A2006b0b4178b6bd78B264083Cc324CF9b06A5114F528AcA018CC8249554dd426d6f850144d3aeeBc86301401856f6f50c0bA16C49ACb26E675398c3E75F";

        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, null, timestamp, randomStr, myAppId, myAppSecret));

        System.out.println("+++++++++++++" + paramMap.get("sign"));

        String url = "http://iot.igeokey.com/xcloud_api/device/device_info/open/getMqttInfoByImei";
        try {
            String result = HttpUtil.get(url, paramMap);
            showLog(paramMap, url, result);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test1 " + e.getMessage());
        }
    }

    @Test
    @SneakyThrows
    public void test2() {
        String applicationCode = "SHEN_ZHEN_LUO_HU";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //普通参数测试
        String p1 = "张珊";
        String p2 = "123456";
        String p3 = "34,23";
        String p4 = "a,b";
        String p5 = "231243";

        //业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);

            put("p1", p1);
            put("p2", p2);
            put("p3", p3);
            put("p4", p4);
        }};

        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, null, timestamp, randomStr, appId, appSecret));

        String url = "http://127.0.0.1:9999/admin/os/api/test/openResource/test2/" + p5;
        try {
            String result = HttpUtil.get(url, paramMap);
            showLog(paramMap, url, result);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test2 " + e.getMessage());
        }
    }

    @Test
    public void test3() {
        String applicationCode = "SHEN_ZHEN_LUO_HU";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //普通参数测试
        String p1 = "张珊";
        String p2 = "123456";
        String p3 = "34,23";
        String p4 = "a,b";
        String p5 = "231243";

        //非body形式业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);

            put("p1", p1);
            put("p2", p2);
            put("p3", p3);
            put("p4", p4);
        }};

        //body参数
        String bodyJson = JSON.toJSONString(new HashMap<String, Object>() {{
            put("a", UUID.randomUUID().toString());
            put("b", UUID.randomUUID().toString());
            put("c", UUID.randomUUID().toString());
            put("d", UUID.randomUUID().toString());
        }});

        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, bodyJson, timestamp, randomStr, appId, appSecret));

        String url = urlParamParse(paramMap, "http://127.0.0.1:9999/admin/os/api/test/openResource/test3/" + p5);

        try {
            String post = HttpUtil.post(url, bodyJson);

            showLog(paramMap, url, post);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test3 " + e.getMessage());
        }
    }


    @Test
    public void test4() {
        String applicationCode = "SHEN_ZHEN_LUO_HU";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //非body形式业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);
        }};

        //body参数
        String bodyJson = JSON.toJSONString(new HashMap<String, Object>() {{
            put("cmd", "0b");
            put("sn", "20110250000002B5");
            put("devType", "03");
            put("playContent", "测试设备请勿惊慌");
            put("playTimes", 1);
        }});

        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, bodyJson, timestamp, randomStr, appId, appSecret));

        String url = urlParamParse(paramMap, "http://iot.igeokey.com/xcloud_api/device-command/jcbleHttpCommand/openCompleteCommand");

        try {
            String post = HttpUtil.post(url, bodyJson);

            showLog(paramMap, url, post);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test3 " + e.getMessage());
        }
    }

    @Test
    @SneakyThrows
    public void test5() {
        String applicationCode = "SHEN_ZHEN_LUO_HU";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //普通参数测试
        String deviceNo = "624809081";
        String startTime = "2020-12-29 17:00:00";
        String endTime = "2020-12-30 17:00:00";

        //业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);

            put("deviceNo", deviceNo);
            put("startTime", startTime);
            put("endTime", endTime);
        }};

        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, null, timestamp, randomStr, appId, appSecret));

        String url = "http://127.0.0.1:9999/device-data/open/api/device/data";
        try {
            String result = HttpUtil.get(url, paramMap);
            showLog(paramMap, url, result);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test5 " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<String, Object>() {{
            this.put("appId", "");
            this.put("startTime", "");
            this.put("deviceNo", "");
            this.put("applicationCode", "");
            this.put("appSecret", "");
            this.put("endTime", "");
        }};

        map.keySet().parallelStream()
                .sorted()
                .forEachOrdered(k -> {
                    System.out.println(k);
                });
    }

    @Test
    public void test6() {
        String applicationCode = "XI_ZANG_MONITOR";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //非body形式业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);
        }};

        //body参数
        String bodyJson = JSON.toJSONString(new HashMap<String, Object>() {{
            put("commandType", 1);
            put("deviceNo", "100000004303");
            put("devType", "03");
            put("emqNodeCode", 2);
            put("password", 123456);
        }});
        appId = "0A150B9138FF46DE8B580EF416D05A8A16C10C2332B04F0FAA9EDB979539B28B";
        appSecret = "Ebbfb9B22333466BBD57f8e11eD81daF22631f72b23247BAa71096Fb10DD62fb7f1cF55c143B4cf78d69345F3f247337D794E8b5fa91420188e48601B6f5Cb0E";
        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, bodyJson, timestamp, randomStr, appId, appSecret));
        String url = urlParamParse(paramMap, "http://iot.igeokey.com/xcloud_api/device-command/xcloudMqttCommand/openCommand");

        try {
            String post = HttpUtil.post(url, bodyJson);

            showLog(paramMap, url, post);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test3 " + e.getMessage());
        }
    }


    @Test
    public void test7() {
        String applicationCode = "GUANGDONG_MEIZHOU";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //非body形式业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);

            put("deviceNo", "7eb928b8-d94a-4589-bebf-46a1189f88aa");
            put("startTime", "2021-05-10 00:00:00");
            put("endTime", "2021-05-10 09:00:00");
        }};
        appId = "ABAE8A32E35E4C7B8B768FCE2BE7451CC50C9264AC39412F8758002B53AEF83C";
        appSecret = "9C81BFcF88b343B48874D3Bdd67E37d31681817a97894760aBd164e57e9D556ff28D136b991842d9BdE46AcAcD087D2deBF2b4Fe4a1e4894B94eC015Cdc60A77";
        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, null, timestamp, randomStr, appId, appSecret));
        String url = urlParamParse(paramMap, "http://iot.igeokey.com/xcloud_api/device-data/open/api/device/data");

        try {
            String post = HttpUtil.get(url);
            System.out.println(post);
            //showLog(paramMap, url, post);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test7 " + e.getMessage());
        }
    }

    @Test
    public void test10() {

        String applicationCode = "0dee4b79-911a-4888-9ac7-86548dda50c5";
        long timestamp = System.currentTimeMillis();
        String randomStr = UUID.randomUUID().toString();

        //非body形式业务参数
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("applicationCode", applicationCode);
            put("timestamp", timestamp);
            put("randomStr", randomStr);
        }};
        appId = "50946A82E8B343D08F2B445E3124E94DBAD21D53326E4D2CB0E7C70E8CE72CCC";
        appSecret = "D03233EbB4A84a288c7d388F70cF09B5Ea8054A7eEed4c3E895287b7B3AA749014C67A9a87314291A9f943794dDd29810Ea839476328420e8D024B0e8B1e985B";
        //生成签名
        paramMap.put("sign", SignUtil.genSign(paramMap, null, timestamp, randomStr, appId, appSecret));
        String url = urlParamParse(paramMap, "http://iot.igeokey.com/xcloud_api/device/device_info/open/getDeviceByProject");

        try {
            String post = HttpUtil.get(url);
            System.out.println(post);
            //showLog(paramMap, url, post);
        } catch (Exception e) {
            log.error(this.getClass().getName() + "@test7 " + e.getMessage());
        }

    }


    @Test
    public void test9() {
//        String deviceId = "100000000162";
//        String time = "2021/6/25 18:00:00";
//        DateTime parse = DateUtil.parse(time, "yyyy/MM/dd HH:mm:ss");
//        DateTime dateTime = DateUtil.offsetHour(parse, -8);
//        String nowTime = DateUtil.parseUTC(dateTime);
//
//        Map<String,Object> map = new HashMap<>();
//        map.put("deviceId",deviceId);
//        map.put("apikey","123");
//        Map<String,Object> data = new HashMap<>();
//        Map<String,Object> valueMap = new HashMap<>();
//        Map<String,Object> timeMap = new HashMap<>();
//        timeMap.put(nowTime,)
//        valueMap.put("l3_yl_1",)
//        data.put(deviceId,)
//        map.put("data",)
//        try {
//            String post = HttpUtil.get(url);
//            System.out.println(post);
//            //showLog(paramMap, url, post);
//        } catch (Exception e) {
//            log.error(this.getClass().getName() + "@test7 " + e.getMessage());
//        }

        List<String> strings = FileUtil.readLines("D:\\events.txt", "UTF-8");
        for (int i = 3000; i < strings.size(); i++) {
            String str = strings.get(i);
            JSONObject data = JSONUtil.parseObj(str).getJSONObject("data");
            String bodyJson = JSON.toJSONString(data);
            String post = HttpUtil.post("http://iot.igeokey.com/xcloud_api/live-data/api/2.0/oi/zhongli-shenzhen/zhongli-ruleId", bodyJson);
            System.out.println("第" + i + "条：》》》" + post);
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }


    private String urlParamParse(Map<String, Object> paramMap, String url) {
        List<String> paramList = new ArrayList<>();
        paramMap.forEach((k, v) -> paramList.add(k + "=" + v));
        return url + "?" + StringUtils.join(paramList, "&");
    }

    private void showLog(Map<String, Object> paramMap, String url, String result) {
        log.info("\n\n------------------------------------");
        log.info("参数：");
        paramMap.forEach((k, v) -> log.info(k + "=" + v));
        log.info("url:" + url);
        log.info("结果：" + result);
    }
}
