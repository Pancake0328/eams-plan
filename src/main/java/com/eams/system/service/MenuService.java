package com.eams.system.service;

import com.eams.system.dto.MenuCreateRequest;
import com.eams.system.dto.MenuUpdateRequest;

/**
 * 菜单权限服务接口
 *
 * @author EAMS Team
 * @since 2026-02-08
 */
public interface MenuService {

    /**
     * 创建菜单权限
     */
    Long createMenu(MenuCreateRequest request);

    /**
     * 更新菜单权限
     */
    void updateMenu(Long id, MenuUpdateRequest request);

    /**
     * 删除菜单权限
     */
    void deleteMenu(Long id);
}
