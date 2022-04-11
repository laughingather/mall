package com.laughingather.gulimall.admin.entity;

import lombok.Data;

/**
 * 用户角色关联实体
 *
 * @author <a href="#">flipped</a>
 * @version v1.0
 * @since 2022-04-11 19:35:16
 */
@Data
public class SysUserRoleEntity {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userid;

    /**
     * 角色id
     */
    private Long roleId;
}
