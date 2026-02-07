package com.eams.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 权限校验辅助类
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Component("permissionChecker")
public class PermissionChecker {

    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> permission.equals(authority.getAuthority()));
    }

    public boolean isSelf(Long userId) {
        Long currentUserId = com.eams.security.SecurityContextHolder.getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    public boolean isSelfOrHasPermission(Long userId, String permission) {
        return isSelf(userId) || hasPermission(permission);
    }
}
