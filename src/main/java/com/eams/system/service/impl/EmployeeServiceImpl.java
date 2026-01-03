package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.common.exception.BusinessException;
import com.eams.system.dto.EmployeeCreateRequest;
import com.eams.system.dto.EmployeePageQuery;
import com.eams.system.dto.EmployeeUpdateRequest;
import com.eams.system.entity.AssetAssignRecord;
import com.eams.system.entity.Department;
import com.eams.system.entity.Employee;
import com.eams.system.mapper.AssetAssignRecordMapper;
import com.eams.system.mapper.DepartmentMapper;
import com.eams.system.mapper.EmployeeMapper;
import com.eams.system.service.EmployeeService;
import com.eams.system.vo.EmployeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;
    private final AssetAssignRecordMapper assignRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmployee(EmployeeCreateRequest request) {
        // 检查工号唯一性
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getEmpNo, request.getEmpNo());
        if (employeeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("员工工号已存在");
        }

        // 检查部门是否存在
        Department department = departmentMapper.selectById(request.getDeptId());
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        Employee employee = new Employee();
        BeanUtils.copyProperties(request, employee);

        employeeMapper.insert(employee);
        return employee.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查部门是否存在
        if (request.getDeptId() != null) {
            Department department = departmentMapper.selectById(request.getDeptId());
            if (department == null) {
                throw new BusinessException("部门不存在");
            }
        }

        BeanUtils.copyProperties(request, employee);
        employee.setId(id);

        employeeMapper.updateById(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查是否有未归还资产
        if (hasUnreturnedAssets(id)) {
            throw new BusinessException("员工有未归还资产，无法删除");
        }

        employeeMapper.deleteById(id);
    }

    @Override
    public EmployeeVO getEmployeeById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        return convertToVO(employee);
    }

    @Override
    public Page<EmployeeVO> getEmployeePage(EmployeePageQuery query) {
        Page<Employee> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getEmpNo() != null, Employee::getEmpNo, query.getEmpNo());
        wrapper.like(query.getEmpName() != null, Employee::getEmpName, query.getEmpName());
        wrapper.eq(query.getDeptId() != null, Employee::getDeptId, query.getDeptId());
        wrapper.eq(query.getStatus() != null, Employee::getStatus, query.getStatus());
        wrapper.orderByDesc(Employee::getCreateTime);

        Page<Employee> employeePage = employeeMapper.selectPage(page, wrapper);

        Page<EmployeeVO> voPage = new Page<>();
        BeanUtils.copyProperties(employeePage, voPage, "records");
        voPage.setRecords(employeePage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployeeStatus(Long id, Integer status) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 离职时检查是否有未归还资产
        if (status == 0 && hasUnreturnedAssets(id)) {
            throw new BusinessException("员工有未归还资产，无法离职");
        }

        employee.setStatus(status);
        employeeMapper.updateById(employee);
    }

    @Override
    public boolean hasUnreturnedAssets(Long empId) {
        LambdaQueryWrapper<AssetAssignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAssignRecord::getToEmpId, empId);
        wrapper.eq(AssetAssignRecord::getAssignType, 1); // 分配给员工
        wrapper.isNull(AssetAssignRecord::getReturnDate); // 未归还
        return assignRecordMapper.selectCount(wrapper) > 0;
    }

    /**
     * 转换为VO
     */
    private EmployeeVO convertToVO(Employee employee) {
        EmployeeVO vo = new EmployeeVO();
        BeanUtils.copyProperties(employee, vo);

        // 设置部门名称
        if (employee.getDeptId() != null) {
            Department department = departmentMapper.selectById(employee.getDeptId());
            if (department != null) {
                vo.setDeptName(department.getDeptName());
            }
        }

        // 设置性别文本
        if (employee.getGender() != null) {
            vo.setGenderText(employee.getGender() == 1 ? "男" : "女");
        }

        // 设置状态文本
        vo.setStatusText(employee.getStatus() == 1 ? "在职" : "离职");

        return vo;
    }
}
