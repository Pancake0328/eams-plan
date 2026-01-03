package com.eams.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
