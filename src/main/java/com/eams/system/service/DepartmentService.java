package com.eams.system.service;

import com.eams.system.dto.DepartmentCreateRequest;
import com.eams.system.dto.DepartmentUpdateRequest;
import com.eams.system.entity.Department;
import com.eams.system.vo.DepartmentTreeNode;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface DepartmentService {

    /**
     * 创建部门
     *
     * @param request 创建请求
     * @return 部门ID
     */
    Long createDepartment(DepartmentCreateRequest request);

    /**
     * 更新部门
     *
     * @param id      部门ID
     * @param request 更新请求
     */
    void updateDepartment(Long id, DepartmentUpdateRequest request);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDepartment(Long id);

    /**
     * 获取部门详情
     *
     * @param id 部门ID
     * @return 部门信息
     */
    Department getDepartmentById(Long id);

    /**
     * 获取部门树
     *
     * @return 部门树列表
     */
    List<DepartmentTreeNode> getDepartmentTree();

    /**
     * 获取子部门列表
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<Department> getChildDepartments(Long parentId);
}
