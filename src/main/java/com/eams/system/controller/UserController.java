package com.eams.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.Result;
import com.eams.system.dto.ResetPasswordRequest;
import com.eams.system.dto.UserCreateRequest;
import com.eams.system.dto.UserPageQuery;
import com.eams.system.dto.UserUpdateRequest;
import com.eams.system.service.UserService;
import com.eams.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 用户ID
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    @OperationLog(value = "创建用户", type = "CREATE")
    public Result<Long> createUser(@Validated @RequestBody UserCreateRequest request) {
        Long userId = userService.createUser(request);
        return Result.success("创建用户成功", userId);
    }

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 更新用户请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    @OperationLog(value = "更新用户", type = "UPDATE")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Validated @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success("更新用户成功");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "逻辑删除用户")
    @OperationLog(value = "删除用户", type = "DELETE")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    public Result<UserVO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }

    /**
     * 分页查询用户
     *
     * @param query 查询条件
     * @return 用户分页列表
     */
    @GetMapping
    @Operation(summary = "分页查询用户", description = "根据条件分页查询用户列表")
    public Result<Page<UserVO>> getUserPage(UserPageQuery query) {
        Page<UserVO> page = userService.getUserPage(query);
        return Result.success(page);
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态：0-禁用，1-正常
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @OperationLog(value = "更新用户状态", type = "UPDATE")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-正常") @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success("更新用户状态成功");
    }

    /**
     * 重置密码
     *
     * @param id      用户ID
     * @param request 重置密码请求
     * @return 操作结果
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "重置密码", description = "重置用户密码")
    @OperationLog(value = "重置密码", type = "UPDATE")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Validated @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request);
        return Result.success("重置密码成功");
    }
}
