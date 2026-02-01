package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.common.result.ResultCode;
import com.eams.exception.BusinessException;
import com.eams.system.dto.ResetPasswordRequest;
import com.eams.system.dto.UserCreateRequest;
import com.eams.system.dto.UserPageQuery;
import com.eams.system.dto.UserUpdateRequest;
import com.eams.system.entity.Department;
import com.eams.system.entity.User;
import com.eams.system.mapper.DepartmentMapper;
import com.eams.system.mapper.UserMapper;
import com.eams.system.service.UserService;
import com.eams.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateRequest request) {
        // 校验用户名唯一性
        if (checkUsernameExists(request.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }

        // 校验手机号唯一性
        if (StringUtils.hasText(request.getPhone()) && checkPhoneExists(request.getPhone(), null)) {
            throw new BusinessException("手机号已被使用");
        }

        // 校验部门是否存在
        if (request.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(request.getDepartmentId());
            if (department == null) {
                throw new BusinessException("所属部门不存在");
            }
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setDepartmentId(request.getDepartmentId());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        // 保存用户
        userMapper.insert(user);
        log.info("创建用户成功，用户名: {}, 部门ID: {}", user.getUsername(), user.getDepartmentId());

        return user.getId();
    }

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 更新用户请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateRequest request) {
        // 检查用户是否存在
        User user = getUserEntityById(id);

        // 校验手机号唯一性
        if (StringUtils.hasText(request.getPhone()) && checkPhoneExists(request.getPhone(), id)) {
            throw new BusinessException("手机号已被使用");
        }

        // 校验部门是否存在
        if (request.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(request.getDepartmentId());
            if (department == null) {
                throw new BusinessException("所属部门不存在");
            }
        }

        // 更新用户信息
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        if (request.getDepartmentId() != null) {
            user.setDepartmentId(request.getDepartmentId());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        // 保存更新
        userMapper.updateById(user);
        log.info("更新用户成功，用户ID: {}", id);
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 检查用户是否存在
        User user = getUserEntityById(id);

        // 不允许删除管理员账号
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不允许删除管理员账号");
        }

        // 逻辑删除
        userMapper.deleteById(id);
        log.info("删除用户成功，用户ID: {}", id);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @Override
    public UserVO getUserById(Long id) {
        User user = getUserEntityById(id);
        return convertToVO(user);
    }

    /**
     * 分页查询用户
     *
     * @param query 查询条件
     * @return 用户分页列表
     */
    @Override
    public Page<UserVO> getUserPage(UserPageQuery query) {
        // 构建分页对象
        Page<User> page = new Page<>(query.getCurrent(), query.getSize());

        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), User::getUsername, query.getUsername())
                .like(StringUtils.hasText(query.getNickname()), User::getNickname, query.getNickname())
                .like(StringUtils.hasText(query.getPhone()), User::getPhone, query.getPhone())
                .eq(query.getStatus() != null, User::getStatus, query.getStatus())
                .orderByDesc(User::getCreateTime);

        // 执行查询
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为 VO
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态：0-禁用，1-正常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        // 检查用户是否存在
        User user = getUserEntityById(id);

        // 不允许禁用管理员账号
        if ("admin".equals(user.getUsername()) && status == 0) {
            throw new BusinessException("不允许禁用管理员账号");
        }

        // 更新状态
        user.setStatus(status);
        userMapper.updateById(user);
        log.info("更新用户状态成功，用户ID: {}, 状态: {}", id, status);
    }

    /**
     * 重置密码
     *
     * @param id      用户ID
     * @param request 重置密码请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, ResetPasswordRequest request) {
        // 检查用户是否存在
        User user = getUserEntityById(id);

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // 更新密码
        user.setPassword(encodedPassword);
        userMapper.updateById(user);
        log.info("重置密码成功，用户ID: {}", id);
    }

    /**
     * 根据ID获取用户实体（内部方法）
     *
     * @param id 用户ID
     * @return 用户实体
     */
    private User getUserEntityById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED);
        }
        return user;
    }

    /**
     * 检查用户名是否已存在
     *
     * @param username  用户名
     * @param excludeId 排除的用户ID（用于更新时排除自己）
     * @return true-已存在，false-不存在
     */
    private boolean checkUsernameExists(String username, Long excludeId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 检查手机号是否已存在
     *
     * @param phone     手机号
     * @param excludeId 排除的用户ID（用于更新时排除自己）
     * @return true-已存在，false-不存在
     */
    private boolean checkPhoneExists(String phone, Long excludeId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 将用户实体转换为VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToVO(User user) {
        // 查询部门名称
        String departmentName = null;
        if (user.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(user.getDepartmentId());
            if (department != null) {
                departmentName = department.getDeptName();
            }
        }

        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .departmentId(user.getDepartmentId())
                .departmentName(departmentName)
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }
}
