package com.eams.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.common.result.PageResult;
import com.eams.common.result.Result;
import com.eams.system.dto.EmployeeCreateRequest;
import com.eams.system.dto.EmployeePageQuery;
import com.eams.system.dto.EmployeeUpdateRequest;
import com.eams.system.service.EmployeeService;
import com.eams.system.vo.EmployeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Tag(name = "员工管理")
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "创建员工")
    @PostMapping
    @OperationLog(module = "员工管理", action = "创建员工")
    @PreAuthorize("hasAuthority('system:employee:add')")
    public Result<Long> createEmployee(@Validated @RequestBody EmployeeCreateRequest request) {
        Long id = employeeService.createEmployee(request);
        return Result.success(id);
    }

    @Operation(summary = "更新员工")
    @PutMapping("/{id}")
    @OperationLog(module = "员工管理", action = "更新员工")
    @PreAuthorize("hasAuthority('system:employee:edit')")
    public Result<Void> updateEmployee(@PathVariable Long id,
            @Validated @RequestBody EmployeeUpdateRequest request) {
        employeeService.updateEmployee(id, request);
        return Result.success();
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    @OperationLog(module = "员工管理", action = "删除员工")
    @PreAuthorize("hasAuthority('system:employee:delete')")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.success();
    }

    @Operation(summary = "获取员工详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:employee:view')")
    public Result<EmployeeVO> getEmployee(@PathVariable Long id) {
        EmployeeVO employee = employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    @Operation(summary = "分页查询员工")
    @GetMapping
    @PreAuthorize("hasAuthority('system:employee:list')")
    public Result<PageResult<EmployeeVO>> getEmployeePage(EmployeePageQuery query) {
        Page<EmployeeVO> page = employeeService.getEmployeePage(query);
        PageResult<EmployeeVO> result = PageResult.of(page.getRecords(), page.getTotal());
        return Result.success(result);
    }

    @Operation(summary = "更新员工状态")
    @PutMapping("/{id}/status")
    @OperationLog(module = "员工管理", action = "更新员工状态")
    @PreAuthorize("hasAuthority('system:employee:status')")
    public Result<Void> updateEmployeeStatus(@PathVariable Long id, @RequestParam Integer status) {
        employeeService.updateEmployeeStatus(id, status);
        return Result.success();
    }
}
