package com.eams.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联Mapper
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    @Insert({
            "<script>",
            "INSERT INTO sys_role_menu (role_id, menu_id)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.roleId}, #{item.menuId})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<SysRoleMenu> list);
}
