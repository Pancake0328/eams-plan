package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.system.dto.AssignPermissionRequest;
import com.eams.system.dto.RoleCreateRequest;
import com.eams.system.entity.SysMenu;
import com.eams.system.entity.SysRole;
import com.eams.system.entity.SysRoleMenu;
import com.eams.system.mapper.SysMenuMapper;
import com.eams.system.mapper.SysRoleMapper;
import com.eams.system.mapper.SysRoleMenuMapper;
import com.eams.system.service.RoleService;
import com.eams.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateRequest request) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(request, role);
        roleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long id, RoleCreateRequest request) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        BeanUtils.copyProperties(request, role);
        role.setId(id);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public RoleVO getRoleById(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            return null;
        }
        return convertToVO(role);
    }

    @Override
    public Page<RoleVO> getRolePage(int current, int size, String roleName, Integer status) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(SysRole::getStatus, status);
        }

        wrapper.orderByDesc(SysRole::getCreateTime);

        Page<SysRole> rolePage = roleMapper.selectPage(page, wrapper);
        Page<RoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        voPage.setRecords(rolePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long id, Integer status) {
        SysRole role = roleMapper.selectById(id);
        if (role != null) {
            role.setStatus(status);
            roleMapper.updateById(role);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(AssignPermissionRequest request) {
        // 删除旧的权限
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, request.getRoleId());
        roleMenuMapper.delete(wrapper);

        // 添加新的权限
        for (Long menuId : request.getMenuIds()) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(request.getRoleId());
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getRolePermissions(Long roleId) {
        List<Long> menuIds = getRoleMenuIds(roleId);
        if (menuIds.isEmpty()) {
            return Set.of();
        }

        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);
        return menus.stream()
                .map(SysMenu::getPermissionCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public List<RoleVO> getAllRoles() {
        List<SysRole> roles = roleMapper.selectList(null);
        return roles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        vo.setStatusText(role.getStatus() == 1 ? "启用" : "禁用");
        return vo;
    }
}
