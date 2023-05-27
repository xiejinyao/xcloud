package com.xjinyao.xcloud.admin.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.dto.DataPermission;
import com.xjinyao.xcloud.admin.api.dto.UserBaseInfoDTO;
import com.xjinyao.xcloud.admin.api.dto.UserDTO;
import com.xjinyao.xcloud.admin.api.dto.UserInfo;
import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import com.xjinyao.xcloud.admin.api.entity.SysUser;
import com.xjinyao.xcloud.admin.api.vo.UserVO;
import com.xjinyao.xcloud.admin.properties.UserSecurityProperties;
import com.xjinyao.xcloud.admin.service.SysOrganizationService;
import com.xjinyao.xcloud.admin.service.SysUserService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import com.xjinyao.xcloud.file.api.feign.RemoteSysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @date 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Api(value = "/user", tags = "用户管理模块")
public class UserController {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
    private final SysUserService userService;
    private final RemoteSysFileService remoteSysFileService;
    private final SysOrganizationService organizationService;
    private final UserSecurityProperties userSecurityProperties;

    /**
     * 获取当前用户全部信息
     *
     * @return 用户信息
     */
    @GetMapping(value = {"/info"})
    @ApiOperation(value = "获取当前用户全部信息", notes = "获取当前用户全部信息")
    public R<UserInfo> info() {
        String username = SecurityUtils.getUser().getUsername();
        SysUser user = userService.getOne(Wrappers.<SysUser>query()
                .lambda().eq(SysUser::getUsername, username));
        if (user == null) {
            return R.failed("获取当前用户信息失败");
        }
        UserInfo userInfo = userService.getUserInfo(user, false);
        if (userInfo != null) {
            DataPermission userDataPermission = SecurityUtils.getUserDataPermission();
            userInfo.setDataPermission(userDataPermission);
            SysUser sysUser = userInfo.getSysUser();
            if (sysUser != null) {
                sysUser.setPhone(Base64Encoder.encode(sysUser.getPhone()));
                sysUser.setUsername(Base64Encoder.encode(sysUser.getUsername()));
                sysUser.setRealname(Base64Encoder.encode(sysUser.getRealname()));
                sysUser.setPassword(null);
            }
        }
        return R.ok(userInfo);
    }

    /**
     * 通过ID查询用户信息
     *
     * @param id ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "通过ID查询用户信息", notes = "通过ID查询用户信息")
    public R<UserVO> user(@PathVariable Integer id) {
        UserVO user = userService.getUserVoById(id);
        return R.ok(remoteSysFileService.fillFileInfos(user));
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return
     */
    @GetMapping("/details/{username}")
    @ApiOperation(value = "根据用户名查询用户信息", notes = "根据用户名查询用户信息")
    public R<SysUser> user(@PathVariable String username) {
        SysUser condition = new SysUser();
        if (StringUtils.isNotEmpty(username) || !username.equals("")) {
            condition.setUsername(username);
            return R.ok(userService.getOne(new QueryWrapper<>(condition)));
        }
        return R.failed();
    }

    /**
     * 删除用户信息
     *
     * @param id ID
     * @return R
     */
    @SysLog("删除用户信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('sys_user_del')")
    @ApiOperation(value = "删除用户信息", notes = "删除用户信息")
    public R<Boolean> userDel(@PathVariable Integer id) {
        SysUser sysUser = userService.getById(id);
        return R.ok(userService.removeUserById(sysUser));
    }

    /**
     * 添加用户
     *
     * @param userDto 用户信息
     * @return success/false
     */
    @SysLog("添加用户")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_user_add')")
    @ApiOperation(value = "添加用户", notes = "添加用户")
    public R<Boolean> user(@Valid
                           @RequestBody UserDTO userDto) {
        if (userService.checkUserNameExists(userDto.getUsername())) {
            return R.failed("用户名已存在");
        } else {
            return R.ok(userService.saveUser(userDto));
        }
    }

    /**
     * 更新用户信息
     *
     * @param userDto 用户信息
     * @return R
     */
    @SysLog("更新用户信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_user_edit')")
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    public R<Boolean> updateUser(@Valid @RequestBody UserDTO userDto) {
        return R.ok(userService.updateUser(userDto));
    }

    /**
     * 重置密码
     *
     * @param userId 用户id
     * @return R
     */
    @SysLog("更新用户信息")
    @GetMapping("resetPassword/{userId}")
    @PreAuthorize("@pms.hasPermission('sys_user_reset_password')")
    @ApiOperation(value = "重置密码", notes = "重置密码")
    public R<String> resetPassword(@PathVariable Long userId,
                                   @ApiParam("新密码") @RequestParam(required = false) String customPassword) {
        SysUser user = userService.getById(userId);
        if (user != null) {
            String password = userSecurityProperties.getPassword();
            if (!StrUtil.isBlankOrUndefined(customPassword)) {
                password = customPassword;
            }
            if (!StrUtil.isBlankOrUndefined(password)) {
                if (!userService.lambdaUpdate()
                        .eq(SysUser::getUserId, userId)
                        .set(SysUser::getPassword, ENCODER.encode(password))
                        .update()) {
                    return R.failed("重置密码失败!");
                } else {
                    userService.removeUserCache(user.getUsername());
                    return R.ok(password);
                }
            } else {
                return R.failed("重置密码失败(未设置默认初始密码,请使用自定义密码重置)!");
            }
        } else {
            return R.failed("用户不存在,无法重置密码!");
        }
    }

    /**
     * 分页查询用户
     *
     * @param page    参数集
     * @param userDTO 查询参数列表
     * @return 用户集合
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询用户", notes = "分页查询用户")
    public R<IPage<UserVO>> getUserPage(Page page, UserDTO userDTO) {
        IPage<UserVO> userWithRolePage = userService.getUserWithRolePage(page, userDTO);
        remoteSysFileService.fillFileInfos(userWithRolePage.getRecords());
        return R.ok(userWithRolePage);
    }

    /**
     * 修改个人信息
     *
     * @param userBaseInfoDTO userDto
     * @return success/false
     */
    @SysLog("修改个人信息")
    @PutMapping("/edit")
    @ApiOperation(value = "修改个人信息", notes = "修改个人信息")
    public R<Boolean> updateUserInfo(@RequestBody UserBaseInfoDTO userBaseInfoDTO) {
        return userService.updateUserInfo(userBaseInfoDTO);
    }


    /**
     * 修改密码
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return
     */
    @SysLog("修改个人信息")
    @PutMapping("/updatePassword")
    @ApiOperation(value = "修改个人信息", notes = "修改个人信息")
    public R<Boolean> updatePassword(
            @ApiParam("原密码") @RequestParam String oldPassword,
            @ApiParam("新密码") @RequestParam String newPassword
    ) {
        Integer userId = SecurityUtils.getUserId();
        if (userId == null) {
            return R.failed("请登录后再试!");
        }
        final SysUser sysUser = userService.lambdaQuery().eq(SysUser::getUserId, userId).oneOpt().orElse(null);
        if (sysUser == null) {
            return R.failed(Boolean.FALSE, "用户不存在,无法执行更新操作!");
        }

        if (ENCODER.matches(oldPassword, sysUser.getPassword()) &&
                userService.lambdaUpdate()
                        .set(SysUser::getPassword, ENCODER.encode(newPassword))
                        .eq(SysUser::getUserId, userId)
                        .update()) {
            return R.ok(Boolean.TRUE, "修改成功(新密码：" + newPassword + ")!");
        } else {
            return R.ok(Boolean.FALSE, "修改失败!");
        }
    }

    /**
     * 查询上级组织的用户信息
     *
     * @param username 用户名称
     * @return 上级组织用户列表
     */
    @GetMapping("/ancestor/{username}")
    @ApiOperation(value = "查询上级组织的用户信息", notes = "查询上级组织的用户信息")
    public R<List<SysUser>> listAncestorUsers(@PathVariable String username) {
        return R.ok(userService.listAncestorUsersByUsername(username));
    }

    @ApiOperation(value = "根据organizationCode查询用户", notes = "根据organizationCode查询用户")
    @ApiImplicitParam(name = "organizationCode", dataType = "String", paramType = "query")
    @GetMapping("/getUserByOrganizationCode")
    public R<List<SysUser>> getQunCeQunFangUserByOrganizationCode(@RequestParam(name = "organizationCode") String organizationCode) {
        //先查询组织id
        if (!StrUtil.hasEmpty(organizationCode)) {
            SysOrganization organization = organizationService.lambdaQuery().eq(SysOrganization::getCode, organizationCode).one();
            if (null != organization) {
                List<SysUser> list = userService.lambdaQuery().eq(SysUser::getOrganizationId, organization.getId()).list();
                return R.ok(list);
            }
        }
        return R.ok(Collections.EMPTY_LIST);
    }
}
