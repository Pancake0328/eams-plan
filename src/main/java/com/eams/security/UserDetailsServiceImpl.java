package com.eams.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.system.entity.User;
import com.eams.system.mapper.UserMapper;
import com.eams.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * 用户详情服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final PermissionService permissionService;

    /**
     * 根据用户名加载用户详情
     *
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        Set<String> permissions = permissionService.getUserPermissions(user.getId());
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UserDetailsImpl(user, authorities);
    }
}
