package com.xjinyao.xcloud.admin.api.vo;

import com.xjinyao.xcloud.common.core.annotations.SysFileInfo;
import com.xjinyao.xcloud.common.core.desensitization.annotations.PrivacyEncrypt;
import com.xjinyao.xcloud.common.core.desensitization.enums.PrivacyTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @date 2019/2/1
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 随机盐
     */
    private String salt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 0-正常，1-删除
     */
    private Boolean delFlag;

    /**
     * 锁定标记
     */
    private Boolean lockFlag;

    /**
     * 手机号
     */
    @PrivacyEncrypt(type = PrivacyTypeEnum.PHONE)
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    @SysFileInfo("avatar")
    private String avatarUrl;

    /**
     * 组织ID
     */
    private String organizationId;

    /**
     * 组织名称
     */
    private String organizationName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private String sex;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否可以选择组织
     */
    private Boolean organizationCanSelect;

    /**
     * 角色列表
     */
    private List<SysUserRoleVO> roleList;
}
