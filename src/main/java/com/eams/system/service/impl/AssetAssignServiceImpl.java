package com.eams.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.common.exception.BusinessException;
import com.eams.system.dto.AssignRecordPageQuery;
import com.eams.system.dto.AssetAssignRequest;
import com.eams.system.entity.AssetAssignRecord;
import com.eams.system.entity.Department;
import com.eams.system.entity.Employee;
import com.eams.system.mapper.AssetAssignRecordMapper;
import com.eams.system.mapper.DepartmentMapper;
import com.eams.system.mapper.EmployeeMapper;
import com.eams.system.service.AssetAssignService;
import com.eams.system.vo.AssetAssignRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资产分配服务实现
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Service
@RequiredArgsConstructor
public class AssetAssignServiceImpl implements AssetAssignService {

    private final AssetAssignRecordMapper assignRecordMapper;
    private final AssetInfoMapper assetInfoMapper;
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long assignAssetToEmployee(AssetAssignRequest request) {
        // 检查资产是否存在
        AssetInfo asset = assetInfoMapper.selectById(request.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 检查资产状态（必须是闲置状态）
        if (asset.getAssetStatus() != 1) {
            throw new BusinessException("只能分配闲置状态的资产");
        }

        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(request.getToEmpId());
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        // 检查员工是否在职
        if (employee.getStatus() != 1) {
            throw new BusinessException("只能分配给在职员工");
        }

        // 创建分配记录
        AssetAssignRecord record = new AssetAssignRecord();
        record.setAssetId(request.getAssetId());
        record.setAssignType(1); // 分配给员工
        record.setToEmpId(request.getToEmpId());
        record.setToDeptId(request.getToDeptId() != null ? request.getToDeptId() : employee.getDeptId());
        record.setAssignDate(LocalDateTime.now());
        record.setRemark(request.getRemark());
        record.setOperator("system"); // 实际应从上下文获取当前用户
        record.setOperateTime(LocalDateTime.now());

        assignRecordMapper.insert(record);

        // 更新资产状态为使用中
        asset.setAssetStatus(2);
        asset.setCustodian(employee.getEmpName());
        asset.setDepartment(employee.getDeptId() != null ? getDeptName(employee.getDeptId()) : null);
        assetInfoMapper.updateById(asset);

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long returnAsset(Long assetId, String remark) {
        // 检查资产是否存在
        AssetInfo asset = assetInfoMapper.selectById(assetId);
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 检查资产是否已分配
        if (asset.getAssetStatus() != 2) {
            throw new BusinessException("资产未分配，无需归还");
        }

        // 查找当前分配记录
        LambdaQueryWrapper<AssetAssignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAssignRecord::getAssetId, assetId);
        wrapper.eq(AssetAssignRecord::getAssignType, 1);
        wrapper.isNull(AssetAssignRecord::getReturnDate);
        wrapper.orderByDesc(AssetAssignRecord::getAssignDate);
        wrapper.last("LIMIT 1");
        AssetAssignRecord currentRecord = assignRecordMapper.selectOne(wrapper);

        if (currentRecord == null) {
            throw new BusinessException("未找到分配记录");
        }

        // 创建回收记录
        AssetAssignRecord record = new AssetAssignRecord();
        record.setAssetId(assetId);
        record.setAssignType(2); // 回收
        record.setFromEmpId(currentRecord.getToEmpId());
        record.setFromDeptId(currentRecord.getToDeptId());
        record.setAssignDate(LocalDateTime.now());
        record.setRemark(remark);
        record.setOperator("system");
        record.setOperateTime(LocalDateTime.now());

        assignRecordMapper.insert(record);

        // 更新原分配记录的归还时间
        currentRecord.setReturnDate(LocalDateTime.now());
        assignRecordMapper.updateById(currentRecord);

        // 更新资产状态为闲置
        asset.setAssetStatus(1);
        asset.setCustodian(null);
        assetInfoMapper.updateById(asset);

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long transferAsset(AssetAssignRequest request) {
        // 检查资产是否存在
        AssetInfo asset = assetInfoMapper.selectById(request.getAssetId());
        if (asset == null) {
            throw new BusinessException("资产不存在");
        }

        // 检查资产是否已分配
        if (asset.getAssetStatus() != 2) {
            throw new BusinessException("只能调拨已分配的资产");
        }

        // 检查目标员工
        Employee toEmployee = employeeMapper.selectById(request.getToEmpId());
        if (toEmployee == null) {
            throw new BusinessException("目标员工不存在");
        }

        // 查找当前分配记录
        LambdaQueryWrapper<AssetAssignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAssignRecord::getAssetId, request.getAssetId());
        wrapper.eq(AssetAssignRecord::getAssignType, 1);
        wrapper.isNull(AssetAssignRecord::getReturnDate);
        wrapper.orderByDesc(AssetAssignRecord::getAssignDate);
        wrapper.last("LIMIT 1");
        AssetAssignRecord currentRecord = assignRecordMapper.selectOne(wrapper);

        if (currentRecord == null) {
            throw new BusinessException("未找到当前分配记录");
        }

        // 创建调拨记录
        AssetAssignRecord record = new AssetAssignRecord();
        record.setAssetId(request.getAssetId());
        record.setAssignType(3); // 部门内调拨
        record.setFromEmpId(currentRecord.getToEmpId());
        record.setFromDeptId(currentRecord.getToDeptId());
        record.setToEmpId(request.getToEmpId());
        record.setToDeptId(request.getToDeptId() != null ? request.getToDeptId() : toEmployee.getDeptId());
        record.setAssignDate(LocalDateTime.now());
        record.setRemark(request.getRemark());
        record.setOperator("system");
        record.setOperateTime(LocalDateTime.now());

        assignRecordMapper.insert(record);

        // 更新原分配记录的归还时间
        currentRecord.setReturnDate(LocalDateTime.now());
        assignRecordMapper.updateById(currentRecord);

        // 更新资产责任人
        asset.setCustodian(toEmployee.getEmpName());
        asset.setDepartment(toEmployee.getDeptId() != null ? getDeptName(toEmployee.getDeptId()) : null);
        assetInfoMapper.updateById(asset);

        return record.getId();
    }

    @Override
    public Page<AssetAssignRecordVO> getAssignRecordPage(AssignRecordPageQuery query) {
        Page<AssetAssignRecord> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<AssetAssignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getAssetId() != null, AssetAssignRecord::getAssetId, query.getAssetId());
        wrapper.eq(query.getAssignType() != null, AssetAssignRecord::getAssignType, query.getAssignType());
        wrapper.or(query.getEmpId() != null, w -> w
                .eq(AssetAssignRecord::getFromEmpId, query.getEmpId())
                .or()
                .eq(AssetAssignRecord::getToEmpId, query.getEmpId()));
        wrapper.orderByDesc(AssetAssignRecord::getAssignDate);

        Page<AssetAssignRecord> recordPage = assignRecordMapper.selectPage(page, wrapper);

        Page<AssetAssignRecordVO> voPage = new Page<>();
        BeanUtils.copyProperties(recordPage, voPage, "records");
        voPage.setRecords(recordPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return voPage;
    }

    @Override
    public List<AssetAssignRecordVO> getAssetAssignHistory(Long assetId) {
        LambdaQueryWrapper<AssetAssignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetAssignRecord::getAssetId, assetId);
        wrapper.orderByDesc(AssetAssignRecord::getAssignDate);

        List<AssetAssignRecord> records = assignRecordMapper.selectList(wrapper);
        return records.stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    public List<AssetAssignRecordVO> getEmployeeAssignHistory(Long empId) {
        LambdaQueryWrapper<AssetAssignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .eq(AssetAssignRecord::getFromEmpId, empId)
                .or()
                .eq(AssetAssignRecord::getToEmpId, empId));
        wrapper.orderByDesc(AssetAssignRecord::getAssignDate);

        List<AssetAssignRecord> records = assignRecordMapper.selectList(wrapper);
        return records.stream()
                .map(this::convertToVO)
                .toList();
    }

    /**
     * 转换为VO
     */
    private AssetAssignRecordVO convertToVO(AssetAssignRecord record) {
        AssetAssignRecordVO vo = new AssetAssignRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 设置资产信息
        AssetInfo asset = assetInfoMapper.selectById(record.getAssetId());
        if (asset != null) {
            vo.setAssetNumber(asset.getAssetNumber());
            vo.setAssetName(asset.getAssetName());
        }

        // 设置员工信息
        if (record.getFromEmpId() != null) {
            Employee fromEmp = employeeMapper.selectById(record.getFromEmpId());
            if (fromEmp != null) {
                vo.setFromEmpName(fromEmp.getEmpName());
            }
        }
        if (record.getToEmpId() != null) {
            Employee toEmp = employeeMapper.selectById(record.getToEmpId());
            if (toEmp != null) {
                vo.setToEmpName(toEmp.getEmpName());
            }
        }

        // 设置部门信息
        if (record.getFromDeptId() != null) {
            vo.setFromDeptName(getDeptName(record.getFromDeptId()));
        }
        if (record.getToDeptId() != null) {
            vo.setToDeptName(getDeptName(record.getToDeptId()));
        }

        // 设置分配类型文本
        String assignTypeText = switch (record.getAssignType()) {
            case 1 -> "分配给员工";
            case 2 -> "回收";
            case 3 -> "部门内调拨";
            default -> "未知";
        };
        vo.setAssignTypeText(assignTypeText);

        return vo;
    }

    /**
     * 获取部门名称
     */
    private String getDeptName(Long deptId) {
        if (deptId == null) {
            return null;
        }
        Department dept = departmentMapper.selectById(deptId);
        return dept != null ? dept.getDeptName() : null;
    }
}
