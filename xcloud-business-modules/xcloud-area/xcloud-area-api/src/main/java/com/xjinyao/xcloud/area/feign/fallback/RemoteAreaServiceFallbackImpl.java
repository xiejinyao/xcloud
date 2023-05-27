package com.xjinyao.xcloud.area.feign.fallback;

import com.xjinyao.xcloud.area.entity.Area;
import com.xjinyao.xcloud.area.feign.RemoteAreaService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 谢进伟
 * @description
 * @createDate 2020/9/10 14:08
 */
@Slf4j
public class RemoteAreaServiceFallbackImpl implements RemoteAreaService {

    @Setter
    private Throwable cause;

    @Override
    public Area getAreaById(String id, String from) {
        return null;
    }

    @Override
    public List<Area> AreaLevelList(Integer level, String from) {
        return null;
    }

    @Override
    public Area areaInfo(Integer level, String from) {
        return null;
    }
}
