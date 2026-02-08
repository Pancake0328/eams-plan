package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.system.dto.MenuCreateRequest;
import com.eams.system.dto.MenuUpdateRequest;
import com.eams.system.entity.SysMenu;
import com.eams.system.mapper.SysMenuMapper;
import com.eams.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单权限服务实现
 *
 * @author EAMS Team
 * @since 2026-02-08
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper menuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuCreateRequest request) {
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(request, menu);
        validateParent(menu.getParentId(), menu.getMenuType(), null);
        applyDefaults(menu);
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long id, MenuUpdateRequest request) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        validateParent(request.getParentId(), request.getMenuType(), id);
        if ("BUTTON".equals(request.getMenuType()) && hasChildren(id)) {
            throw new RuntimeException("存在子权限，无法调整为按钮");
        }
        BeanUtils.copyProperties(request, menu);
        menu.setId(id);
        applyDefaults(menu);
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        if (menuMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("存在子菜单，无法删除");
        }
        menuMapper.deleteById(id);
    }

    private void applyDefaults(SysMenu menu) {
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
    }

    private void validateParent(Long parentId, String menuType, Long currentId) {
        if (parentId == null) {
            throw new RuntimeException("父菜单不能为空");
        }
        if ("BUTTON".equals(menuType) && parentId == 0) {
            throw new RuntimeException("按钮必须选择上级菜单");
        }
        if (parentId == 0) {
            return;
        }
        SysMenu parent = menuMapper.selectById(parentId);
        if (parent == null) {
            throw new RuntimeException("父菜单不存在");
        }
        if ("BUTTON".equals(parent.getMenuType())) {
            throw new RuntimeException("父菜单不能是按钮");
        }
        if ("BUTTON".equals(menuType) && !"MENU".equals(parent.getMenuType())) {
            throw new RuntimeException("按钮必须挂在菜单下");
        }
        if (currentId != null) {
            Long cursor = parent.getParentId();
            while (cursor != null && cursor != 0) {
                if (cursor.equals(currentId)) {
                    throw new RuntimeException("不能选择自身或子节点作为父级");
                }
                SysMenu ancestor = menuMapper.selectById(cursor);
                if (ancestor == null) {
                    break;
                }
                cursor = ancestor.getParentId();
            }
            if (parentId.equals(currentId)) {
                throw new RuntimeException("不能选择自身作为父级");
            }
        }
    }

    private boolean hasChildren(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        return menuMapper.selectCount(wrapper) > 0;
    }
}
