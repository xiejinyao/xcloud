package com.xjinyao.xcloud.admin.controller.innner;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.dto.UserInfo;
import com.xjinyao.xcloud.admin.api.entity.SysUser;
import com.xjinyao.xcloud.admin.service.SysUserService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 内部接口：用户信息
 *
 * @author 谢进伟
 * @createDate 2022/11/16 10:48
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerMapping.SYS_USER_CONTROLLER_MAPPING)
public class InnerUserController {

	private final SysUserService userService;


	/**
	 * 根据用户名获取指定用户全部信息
	 *
	 * @return 用户信息
	 */
	@Inner
	@GetMapping("/info/{username}")
	@ApiOperation(value = "根据用户名获取指定用户全部信息", notes = "根据用户名获取指定用户全部信息", hidden = true)
	public R<UserInfo> info(@PathVariable String username) {
		SysUser user = userService.lambdaQuery()
				.eq(SysUser::getUsername, username)
				.and(q -> q.isNull(SysUser::getThirdPartyId)
						.or()
						.eq(SysUser::getUsername, "admin"))
				.list()
				.stream()
				.findFirst()
				.orElse(null);
		if (user == null) {
			return R.failed(String.format("用户信息为空 %s", username));
		}
		UserInfo userInfo = userService.getUserInfo(user, true);
		return R.ok(userInfo);
	}


	/**
	 * 根据用户名和第三方id获取指定用户全部信息
	 *
	 * @param username     用户名
	 * @param thirdPartyId 第三方身份
	 * @param sources      第三平台编码
	 * @return 用户信息
	 */
	@Inner
	@GetMapping("/info")
	@ApiOperation(value = "根据用户名和第三方id获取指定用户全部信息", notes = "根据用户名和第三方id获取指定用户全部信息", hidden = true)
	public R<UserInfo> info(@RequestParam("username") String username,
							@RequestParam("thirdPartyId") String thirdPartyId,
							@RequestParam("sources") String sources) {
		SysUser user = userService.lambdaQuery()
				.eq(SysUser::getUsername, username)
				.eq(SysUser::getThirdPartyId, thirdPartyId)
				.eq(SysUser::getSources, sources)
				.list()
				.stream()
				.findFirst()
				.orElse(null);
		if (user == null) {
			return R.failed(String.format("用户信息为空 %s", username));
		}
		UserInfo userInfo = userService.getUserInfo(user, true);
		return R.ok(userInfo);
	}
}
