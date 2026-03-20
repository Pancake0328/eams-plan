package com.eams.asset.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.asset.dto.UsageApplicationAuditRequest;
import com.eams.asset.dto.UsageApplicationCreateRequest;
import com.eams.asset.dto.UsageApplicationPageQuery;
import com.eams.asset.service.AssetUsageApplicationService;
import com.eams.asset.vo.UsageApplicationVO;
import com.eams.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 资产使用申请管理控制器
 */
@Tag(name = "资产使用申请管理")
@RestController
@RequestMapping("/asset-usage-applications")
@RequiredArgsConstructor
@Slf4j
public class AssetUsageApplicationController {

    private final AssetUsageApplicationService applicationService;

    @Operation(summary = "创建资产使用申请")
    @PostMapping
    @OperationLog(module = "资产申请", action = "创建申请")
    @PreAuthorize("hasAuthority('asset:usage:apply')")
    public Result<Long> createApplication(@Valid @RequestBody UsageApplicationCreateRequest request) {
        log.info("创建资产使用申请，assetId={}", request.getAssetId());
        Long id = applicationService.createApplication(request);
        return Result.success("申请提交成功", id);
    }

    @Operation(summary = "分页查询资产使用申请")
    @GetMapping
    @PreAuthorize("hasAuthority('asset:usage:list')")
    public Result<Page<UsageApplicationVO>> getApplicationPage(UsageApplicationPageQuery query) {
        log.info("分页查询资产使用申请，query={}", query);
        return Result.success(applicationService.getApplicationPage(query));
    }

    @Operation(summary = "审核资产使用申请")
    @PutMapping("/{id}/audit")
    @OperationLog(module = "资产申请", action = "审核申请")
    @PreAuthorize("hasAuthority('asset:usage:audit')")
    public Result<Void> auditApplication(@PathVariable Long id, @Valid @RequestBody UsageApplicationAuditRequest request) {
        log.info("审核资产使用申请，id={}，approved={}", id, request.getApproved());
        applicationService.auditApplication(id, request);
        return Result.success();
    }
}
