package com.eams.asset.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.asset.dto.AssetCreateRequest;
import com.eams.asset.dto.AssetPageQuery;
import com.eams.asset.dto.AssetUpdateRequest;
import com.eams.asset.service.AssetInfoService;
import com.eams.asset.vo.AssetVO;
import com.eams.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产信息管理控制器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
@Tag(name = "资产信息管理", description = "资产信息管理相关接口")
public class AssetInfoController {

    private final AssetInfoService assetInfoService;

    /**
     * 创建资产
     *
     * @param request 创建请求
     * @return 资产ID
     */
    @PostMapping
    @Operation(summary = "创建资产", description = "登记新的资产信息")
    @OperationLog(value = "创建资产", type = "CREATE")
    @PreAuthorize("hasAuthority('asset:info:add')")
    public Result<Long> createAsset(@Validated @RequestBody AssetCreateRequest request) {
        Long assetId = assetInfoService.createAsset(request);
        return Result.success("创建资产成功", assetId);
    }

    /**
     * 更新资产
     *
     * @param id      资产ID
     * @param request 更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新资产", description = "更新资产信息")
    @OperationLog(value = "更新资产", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:info:edit')")
    public Result<Void> updateAsset(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @Validated @RequestBody AssetUpdateRequest request) {
        assetInfoService.updateAsset(id, request);
        return Result.success();
    }

    /**
     * 删除资产
     *
     * @param id 资产ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资产", description = "逻辑删除资产")
    @OperationLog(value = "删除资产", type = "DELETE")
    @PreAuthorize("hasAuthority('asset:info:delete')")
    public Result<Void> deleteAsset(@Parameter(description = "资产ID") @PathVariable Long id) {
        assetInfoService.deleteAsset(id);
        return Result.success();
    }

    /**
     * 获取资产详情
     *
     * @param id 资产ID
     * @return 资产详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取资产详情", description = "根据ID获取资产详细信息")
    @PreAuthorize("hasAuthority('asset:info:view')")
    public Result<AssetVO> getAssetById(@Parameter(description = "资产ID") @PathVariable Long id) {
        AssetVO assetVO = assetInfoService.getAssetById(id);
        return Result.success(assetVO);
    }

    /**
     * 分页查询资产
     *
     * @param query 查询条件
     * @return 资产分页列表
     */
    @GetMapping
    @Operation(summary = "分页查询资产", description = "根据条件分页查询资产列表")
    @PreAuthorize("hasAuthority('asset:info:list')")
    public Result<Page<AssetVO>> getAssetPage(AssetPageQuery query) {
        Page<AssetVO> page = assetInfoService.getAssetPage(query);
        return Result.success(page);
    }

    /**
     * 更新资产状态
     *
     * @param id     资产ID
     * @param status 资产状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新资产状态", description = "更新资产的使用状态")
    @OperationLog(value = "更新资产状态", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:info:status')")
    public Result<Void> updateAssetStatus(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @Parameter(description = "资产状态：1-闲置，2-使用中，3-维修中，4-报废") @RequestParam Integer status) {
        assetInfoService.updateAssetStatus(id, status);
        return Result.success();
    }
}
