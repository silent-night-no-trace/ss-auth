package com.style.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.style.persistence.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author leon
 * @date 2024-06-22 16:02:48
 */
@Mapper
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username username
     * @return SysUser
     */
    SysUser getUserByUsername(@Param("username") String username);
}
