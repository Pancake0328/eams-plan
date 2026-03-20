package com.eams.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.dto.RecordCreateRequest;
import com.eams.asset.dto.UsageApplicationAuditRequest;
import com.eams.asset.dto.UsageApplicationCreateRequest;
import com.eams.asset.dto.UsageApplicationPageQuery;
import com.eams.asset.entity.AssetInfo;
import com.eams.asset.entity.AssetUsageApplication;
import com.eams.asset.mapper.AssetInfoMapper;
import com.eams.asset.mapper.AssetUsageApplicationMapper;
import com.eams.asset.service.AssetRecordService;
import com.eams.asset.service.AssetUsageApplicationService;
import com.eams.asset.vo.UsageApplicationVO;
import com.eams.exception.BusinessException;
import com.eams.common.result.ResultCode;
import com.eams.system.entity.Department;
import com.eams.system.entity.User;
import com.eams.system.mapper.DepartmentMapper;
import com.eams.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资产使用申请服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssetUsageApplicationServiceImpl implements AssetUsageApplicationService {

    private final AssetUsageApplicationMapper applicationMapper;
    private final AssetInfoMapper assetInfoMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final AssetRecordService assetRecordService;

    private static final Map<Integer, String> APPLY_STATUS_MAP = new HashMap<>();

    static {
        APPLY_STATUS_MAP.put(1, "待审核");
        APPLY_STATUS_MAP.put(2, "已通过");
        APPLY_STATUS_MAP.put(3, "已拒绝");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createApplication(UsageApplicationCreateRequest request) {
        AssetInfo asset = assetInfoMapper.selectById(request.getAssetId());
        if (asset == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED.getCode(), "资产不存在");
        }
        if (asset.getAssetStatus() == null || asset.getAssetStatus() != 1) {
            throw new BusinessException("仅闲置资产可申请使用");
        }

        String applicant = getCurrentUsername();
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, applicant));
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED.getCode(), "当前用户不存在");
        }
        if (user.getDepartmentId() == null) {
            throw new BusinessException("当前用户未绑定部门，无法申请");
        }

        Long pendingCount = applicationMapper.selectCount(new LambdaQueryWrapper<AssetUsageApplication>()
                .eq(AssetUsageApplication::getAssetId, request.getAssetId())
                .eq(AssetUsageApplication::getApplyStatus, 1));
        if (pendingCount != null && pendingCount > 0) {
            throw new BusinessException("该资产已有待审核申请，请勿重复提交");
        }

        Department department = departmentMapper.selectById(user.getDepartmentId());
        if (department == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED.getCode(), "申请人所属部门不存在");
        }

        AssetUsageApplication application = new AssetUsageApplication();
        application.setApplicationNumber(generateApplicationNumber());
        application.setAssetId(asset.getId());
        application.setAssetNumber(asset.getAssetNumber());
        application.setAssetName(asset.getAssetName());
        application.setApplicant(applicant);
        application.setApplicantDepartmentId(user.getDepartmentId());
        application.setApplicantDepartment(department.getDeptName());
        application.setApplyReason(request.getApplyReason());
        application.setApplyStatus(1);
        applicationMapper.insert(application);

        log.info("创建资产使用申请成功，applicationId={}，assetId={}，applicant={}",
                application.getId(), request.getAssetId(), applicant);
        return application.getId();
    }

    @Override
    public Page<UsageApplicationVO> getApplicationPage(UsageApplicationPageQuery query) {
        Page<AssetUsageApplication> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<AssetUsageApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getApplyStatus() != null, AssetUsageApplication::getApplyStatus, query.getApplyStatus())
                .like(StringUtils.hasText(query.getAssetNumber()), AssetUsageApplication::getAssetNumber, query.getAssetNumber())
                .like(StringUtils.hasText(query.getAssetName()), AssetUsageApplication::getAssetName, query.getAssetName())
                .like(StringUtils.hasText(query.getApplicant()), AssetUsageApplication::getApplicant, query.getApplicant())
                .orderByDesc(AssetUsageApplication::getCreateTime);
        Page<AssetUsageApplication> dataPage = applicationMapper.selectPage(page, wrapper);

        Page<UsageApplicationVO> voPage = new Page<>(dataPage.getCurrent(), dataPage.getSize(), dataPage.getTotal());
        List<UsageApplicationVO> records = dataPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    public Page<UsageApplicationVO> getMyApplicationPage(UsageApplicationPageQuery query) {
        Page<AssetUsageApplication> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<AssetUsageApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetUsageApplication::getApplicant, getCurrentUsername())
                .eq(query.getApplyStatus() != null, AssetUsageApplication::getApplyStatus, query.getApplyStatus())
                .like(StringUtils.hasText(query.getAssetNumber()), AssetUsageApplication::getAssetNumber, query.getAssetNumber())
                .like(StringUtils.hasText(query.getAssetName()), AssetUsageApplication::getAssetName, query.getAssetName())
                .orderByDesc(AssetUsageApplication::getCreateTime);
        Page<AssetUsageApplication> dataPage = applicationMapper.selectPage(page, wrapper);

        Page<UsageApplicationVO> voPage = new Page<>(dataPage.getCurrent(), dataPage.getSize(), dataPage.getTotal());
        List<UsageApplicationVO> records = dataPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(records);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditApplication(Long id, UsageApplicationAuditRequest request) {
        AssetUsageApplication application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED.getCode(), "申请记录不存在");
        }
        if (application.getApplyStatus() == null || application.getApplyStatus() != 1) {
            throw new BusinessException("仅待审核申请可执行审核");
        }

        AssetInfo asset = assetInfoMapper.selectById(application.getAssetId());
        if (asset == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXISTED.getCode(), "资产不存在");
        }
        if (request.getApproved() && (asset.getAssetStatus() == null || asset.getAssetStatus() != 1)) {
            throw new BusinessException("资产已非闲置状态，无法通过申请");
        }

        String auditor = getCurrentUsername();
        application.setAuditor(auditor);
        application.setAuditTime(java.time.LocalDateTime.now());
        application.setAuditRemark(request.getAuditRemark());
        application.setApplyStatus(Boolean.TRUE.equals(request.getApproved()) ? 2 : 3);

        if (Boolean.TRUE.equals(request.getApproved())) {
            RecordCreateRequest allocateRequest = new RecordCreateRequest();
            allocateRequest.setAssetId(application.getAssetId());
            allocateRequest.setToCustodian(application.getApplicant());
            allocateRequest.setToDepartmentId(application.getApplicantDepartmentId());
            allocateRequest.setRemark(StringUtils.hasText(request.getAuditRemark())
                    ? request.getAuditRemark()
                    : "资产申请审核通过，自动分配");
            assetRecordService.allocateAsset(allocateRequest);
        }

        applicationMapper.updateById(application);
        log.info("审核资产使用申请完成，applicationId={}，approved={}，auditor={}",
                id, request.getApproved(), auditor);
    }

    private UsageApplicationVO convertToVO(AssetUsageApplication application) {
        UsageApplicationVO vo = new UsageApplicationVO();
        BeanUtils.copyProperties(application, vo);
        vo.setApplyStatusText(APPLY_STATUS_MAP.getOrDefault(application.getApplyStatus(), "未知"));
        vo.setApplicantName(getUserDisplayName(application.getApplicant()));
        vo.setAuditorName(getUserDisplayName(application.getAuditor()));
        return vo;
    }

    private String getUserDisplayName(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return username;
        }
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
    }

    private String generateApplicationNumber() {
        return "USE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new BusinessException("未获取到当前登录用户");
        }
        return authentication.getName();
    }
}
