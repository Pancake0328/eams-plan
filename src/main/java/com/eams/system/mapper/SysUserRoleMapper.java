package com.eams.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @Insert({
            "<script>",
            "INSERT INTO sys_user_role (user_id, role_id)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.userId}, #{item.roleId})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<SysUserRole> list);
}
