package com.eams.system.controller;

import com.eams.aop.OperationLog;
import com.eams.common.result.Result;
import com.eams.system.dto.DepartmentCreateRequest;
import com.eams.system.dto.DepartmentUpdateRequest;
import com.eams.system.entity.Department;
import com.eams.system.service.DepartmentService;
import com.eams.system.vo.DepartmentTreeNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "创建部门")
    @PostMapping
    @OperationLog(module = "部门管理", action = "创建部门")
    public Result<Long> createDepartment(@Validated @RequestBody DepartmentCreateRequest request) {
        Long id = departmentService.createDepartment(request);
        return Result.success(id);
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    @OperationLog(module = "部门管理", action = "更新部门")
    public Result<Void> updateDepartment(@PathVariable Long id,
            @Validated @RequestBody DepartmentUpdateRequest request) {
        departmentService.updateDepartment(id, request);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @OperationLog(module = "部门管理", action = "删除部门")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public Result<Department> getDepartment(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return Result.success(department);
    }

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public Result<List<DepartmentTreeNode>> getDepartmentTree() {
        List<DepartmentTreeNode> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取子部门列表")
    @GetMapping("/children/{parentId}")
    public Result<List<Department>> getChildDepartments(@PathVariable Long parentId) {
        List<Department> children = departmentService.getChildDepartments(parentId);
        return Result.success(children);
    }
}
