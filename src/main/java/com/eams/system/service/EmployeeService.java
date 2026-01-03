package com.eams.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.system.dto.EmployeeCreateRequest;
import com.eams.system.dto.EmployeePageQuery;
import com.eams.system.dto.EmployeeUpdateRequest;
import com.eams.system.entity.Employee;
import com.eams.system.vo.EmployeeVO;

/**
 * 员工服务接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface EmployeeService {

    /**
     * 创建员工
     *
     * @param request 创建请求
     * @return 员工ID
     */
    Long createEmployee(EmployeeCreateRequest request);

    /**
     * 更新员工
     *
     * @param id      员工ID
     * @param request 更新请求
     */
    void updateEmployee(Long id, EmployeeUpdateRequest request);

    /**
     * 删除员工（逻辑删除）
     *
     * @param id 员工ID
     */
    void deleteEmployee(Long id);

    /**
     * 获取员工详情
     *
     * @param id 员工ID
     * @return 员工信息
     */
    EmployeeVO getEmployeeById(Long id);

    /**
     * 分页查询员工
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<EmployeeVO> getEmployeePage(EmployeePageQuery query);

    /**
     * 更新员工状态
     *
     * @param id     员工ID
     * @param status 状态：1-在职，0-离职
     */
    void updateEmployeeStatus(Long id, Integer status);

    /**
     * 检查员工是否有未归还资产
     *
     * @param empId 员工ID
     * @return true-有未归还资产，false-无未归还资产
     */
    boolean hasUnreturnedAssets(Long empId);
}
