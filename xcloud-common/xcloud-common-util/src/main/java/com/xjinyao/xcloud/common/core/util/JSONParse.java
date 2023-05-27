package com.xjinyao.xcloud.common.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 谢进伟
 * @description JSON解析工具
 * @createDate 2021/3/2 19:05
 */
public class JSONParse {

    /**
     * 对节点进行解析
     *
     * @param jsonObject
     * @param node
     * @return
     * @author mengfeiyang
     */
    private static JSONObject getObj(JSONObject jsonObject, String node) {
        try {
            if (node.contains("[")) {
                JSONArray arr = jsonObject.getJSONArray(node.substring(0, node.indexOf("[")));
                for (int i = 0; i < arr.size(); i++) {
                    if ((i + "").equals(node.substring(node.indexOf("["), node.indexOf("]")).replace("[", ""))) {
                        return arr.getJSONObject(i);
                    }
                }
            } else {
                return jsonObject.getJSONObject(node);
            }
        } catch (Exception e) {
            return jsonObject;
        }
        return null;
    }

    /**
     * 获取节点值
     *
     * @param json     JSON对象
     * @param jsonPath 需要解析的路径
     * @return
     */
    public static String getNodeValue(String json, String jsonPath) {
        String[] nodes = jsonPath.split("\\.");
        JSONObject obj = JSONObject.parseObject(json);
        for (int i = 1, len = nodes.length; i < len; i++) {
            if (obj != null) {
                obj = getObj(obj, nodes[i]);
            }
            if ((i + 1) == len) {
                try {
                    return obj.getString(nodes[i]);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
}
