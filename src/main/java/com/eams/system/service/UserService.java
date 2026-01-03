package com.eams.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.system.dto.ResetPasswordRequest;
import com.eams.system.dto.UserCreateRequest;
import com.eams.system.dto.UserPageQuery;
import com.eams.system.dto.UserUpdateRequest;
import com.eams.system.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 用户ID
     */
    Long createUser(UserCreateRequest request);

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 更新用户请求
     */
    void updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 分页查询用户
     *
     * @param query 查询条件
     * @return 用户分页列表
     */
    Page<UserVO> getUserPage(UserPageQuery query);

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态：0-禁用，1-正常
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 重置密码
     *
     * @param id      用户ID
     * @param request 重置密码请求
     */
    void resetPassword(Long id, ResetPasswordRequest request);
}
