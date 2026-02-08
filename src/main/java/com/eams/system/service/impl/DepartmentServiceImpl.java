package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.exception.BusinessException;
import com.eams.system.dto.DepartmentCreateRequest;
import com.eams.system.dto.DepartmentUpdateRequest;
import com.eams.system.entity.Department;
import com.eams.system.entity.User;
import com.eams.system.mapper.DepartmentMapper;
import com.eams.system.mapper.UserMapper;
import com.eams.system.service.DepartmentService;
import com.eams.system.vo.DepartmentTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(DepartmentCreateRequest request) {
        // 检查部门编码唯一性
        if (request.getDeptCode() != null && !request.getDeptCode().isEmpty()) {
            LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Department::getDeptCode, request.getDeptCode());
            if (departmentMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("部门编码已存在");
            }
        }

        // 检查父部门是否存在
        if (request.getParentId() != 0) {
            Department parent = departmentMapper.selectById(request.getParentId());
            if (parent == null) {
                throw new BusinessException("父部门不存在");
            }
        }

        Department department = new Department();
        BeanUtils.copyProperties(request, department);

        departmentMapper.insert(department);
        return department.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查部门编码唯一性
        if (request.getDeptCode() != null && !request.getDeptCode().isEmpty()) {
            LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Department::getDeptCode, request.getDeptCode());
            wrapper.ne(Department::getId, id);
            if (departmentMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("部门编码已存在");
            }
        }

        BeanUtils.copyProperties(request, department);
        department.setId(id);

        departmentMapper.updateById(department);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        Department department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否有子部门
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, id);
        if (departmentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("存在子部门，无法删除");
        }

        Long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getDepartmentId, id));
        if (userCount > 0) {
            throw new BusinessException("当前部门有用户，无法删除");
        }

        departmentMapper.deleteById(id);
    }

    @Override
    public Department getDepartmentById(Long id) {
        Department department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        return department;
    }

    @Override
    public List<DepartmentTreeNode> getDepartmentTree() {
        // 查询所有部门
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Department::getSortOrder);
        List<Department> allDepts = departmentMapper.selectList(wrapper);

        // 构建树结构
        return buildDepartmentTree(allDepts, 0L);
    }

    @Override
    public List<Department> getChildDepartments(Long parentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, parentId);
        wrapper.orderByAsc(Department::getSortOrder);
        return departmentMapper.selectList(wrapper);
    }

    /**
     * 递归构建部门树
     *
     * @param allDepts 所有部门
     * @param parentId 父部门ID
     * @return 部门树节点列表
     */
    private List<DepartmentTreeNode> buildDepartmentTree(List<Department> allDepts, Long parentId) {
        return allDepts.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .map(dept -> {
                    DepartmentTreeNode node = new DepartmentTreeNode();
                    BeanUtils.copyProperties(dept, node);

                    // 递归查找子部门
                    List<DepartmentTreeNode> children = buildDepartmentTree(allDepts, dept.getId());
                    if (!children.isEmpty()) {
                        node.setChildren(children);
                    }

                    return node;
                })
                .collect(Collectors.toList());
    }
}
