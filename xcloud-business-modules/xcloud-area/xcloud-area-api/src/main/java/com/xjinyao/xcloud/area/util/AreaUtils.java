package com.xjinyao.xcloud.area.util;

import com.xjinyao.xcloud.area.enums.AreaLevelEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : laishihao
 * @date : 2020/8/18
 * description :
 */
public class AreaUtils {

    public static Integer getAreaCodeLevel(String code) {
        int length = code.length();
        Integer AreaCodeLevel = 0;
        switch (length) {
            case 2:
                AreaCodeLevel = AreaLevelEnum.PROVINCE.getValue();
                break;
            case 4:
                AreaCodeLevel = AreaLevelEnum.CITY.getValue();
                break;
            case 6:
                AreaCodeLevel = AreaLevelEnum.COUNTY.getValue();
                break;
            case 9:
                AreaCodeLevel = AreaLevelEnum.TOWN.getValue();
                break;
        }
        return AreaCodeLevel;
    }

    //返回省市区县
    public static Map<String, String> getAreaCodeByLength(String code) {
        Map<String, String> maps = new HashMap<>();
        if (code.length() == 2) {
            maps.put("province", code);
        }
        if (code.length() == 4) {
            maps.put("province", code.substring(0, 2));
            maps.put("city", code);
        }
        if (code.length() == 6) {
            maps.put("province", code.substring(0, 2));
            maps.put("city", code.substring(0, 4));
            maps.put("county", code);
        }
        if (code.length() == 9) {
            maps.put("province", code.substring(0, 2));
            maps.put("city", code.substring(0, 4));
            maps.put("county", code.substring(0, 6));
            maps.put("town", code);
        }
        return maps;
    }


}
