package com.xjinyao.xcloud.area.feign;

import com.xjinyao.xcloud.area.entity.Area;
import com.xjinyao.xcloud.area.feign.factory.RemoteAreaServiceFallbackFactory;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.constant.ServiceNameConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author 谢进伟
 * @description 行政区域服务
 * @createDate 2020/6/9 8:59
 */
@FeignClient(contextId = "remoteAreaService", value = ServiceNameConstants.AREA_SERVICE,
		fallbackFactory = RemoteAreaServiceFallbackFactory.class)
public interface RemoteAreaService {

	/**
	 * 根据id获取行政区域数据
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/inner/areaId")
	Area getAreaById(@RequestParam(value = "id") String id, @RequestHeader(SecurityConstants.FROM) String from);

	/**
	 * 根据等级查询地区集合
	 *
	 * @param level
	 * @return
	 */
	@GetMapping("/inner/level")
	List<Area> AreaLevelList(@RequestParam(value = "level") Integer level, @RequestHeader(SecurityConstants.FROM) String from);

	/**
	 * 根据county获取上级code
	 *
	 * @param level
	 * @return
	 */
	@GetMapping("/inner/level")
	Area areaInfo(@RequestParam(value = "level") Integer level, @RequestHeader(SecurityConstants.FROM) String from);

	/**
	 * 填满
	 *
	 * @param source       源
	 * @param fieldMapping 字段映射,key标识source对象中获取地域id的方法，value标识source对象中设置地域名称的方法
	 * @return {@link T}
	 */
	default <T> T fillAreaInfo(T source, Map<Function<T, String>, BiConsumer<T, String>> fieldMapping) {
		if (MapUtils.isNotEmpty(fieldMapping)) {
			fieldMapping.forEach((k, v) -> {
				String id = k.apply(source);
				if (StringUtils.isBlank(id)) {
					return;
				}
				String name = this.getAreaById(id, SecurityConstants.FROM_IN).getName();
				v.accept(source, name);
			});
		}
		return source;
	}

	/**
	 * 填满
	 *
	 * @param sourceList   源列表
	 * @param fieldMapping 字段映射,key标识source对象中获取地域id的方法，value标识source对象中设置地域名称的方法
	 * @return {@link List}<{@link T}>
	 */
	default <T> List<T> fillAreaInfo(List<T> sourceList, Map<Function<T, String>, BiConsumer<T, String>> fieldMapping) {
		sourceList.forEach(source -> fillAreaInfo(source, fieldMapping));
		return sourceList;
	}


}
