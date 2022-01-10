package com.laughingather.gulimall.adminnew.repository;

import com.laughingather.gulimall.adminnew.entity.SysUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户持久层
 *
 * @author：laughingather
 * @create：2021-11-24 2021/11/24
 */
public interface SysUserRepository extends JpaRepository<SysUserEntity, Long> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    SysUserEntity getByUsernameEquals(String username);

}