package com.eams.asset.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.aop.OperationLog;
import com.eams.asset.dto.RecordCreateRequest;
import com.eams.asset.dto.RecordPageQuery;
import com.eams.asset.service.AssetRecordService;
import com.eams.asset.vo.RecordVO;
import com.eams.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产流转记录管理控制器
 *
 * @author Pancake
 * @since 2026-01-03
 */
@RestController
@RequestMapping("/asset-records")
@RequiredArgsConstructor
@Tag(name = "资产流转管理", description = "资产出入库与流转管理相关接口")
public class AssetRecordController {

    private final AssetRecordService recordService;

    /**
     * 资产入库
     *
     * @param request 入库请求
     * @return 记录ID
     */
    @PostMapping("/in")
    @Operation(summary = "资产入库", description = "新资产入库记录")
    @OperationLog(value = "资产入库", type = "CREATE")
    @PreAuthorize("hasAuthority('asset:record:in')")
    public Result<Long> assetIn(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.assetIn(request);
        return Result.success("资产入库成功", recordId);
    }

    /**
     * 资产分配
     *
     * @param request 分配请求
     * @return 记录ID
     */
    @PostMapping("/allocate")
    @Operation(summary = "资产分配", description = "将资产分配给员工")
    @OperationLog(value = "资产分配", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:record:allocate')")
    public Result<Long> allocateAsset(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.allocateAsset(request);
        return Result.success("资产分配成功", recordId);
    }

    /**
     * 资产调拨
     *
     * @param request 调拨请求
     * @return 记录ID
     */
    @PostMapping("/transfer")
    @Operation(summary = "资产调拨", description = "部门间资产调拨")
    @OperationLog(value = "资产调拨", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:record:transfer')")
    public Result<Long> transferAsset(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.transferAsset(request);
        return Result.success("资产调拨成功", recordId);
    }

    /**
     * 资产归还
     *
     * @param request 归还请求
     * @return 记录ID
     */
    @PostMapping("/return")
    @Operation(summary = "资产归还", description = "归还使用中的资产")
    @OperationLog(value = "资产归还", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:record:return')")
    public Result<Long> returnAsset(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.returnAsset(request);
        return Result.success("资产归还成功", recordId);
    }

    /**
     * 资产报废
     *
     * @param request 报废请求
     * @return 记录ID
     */
    @PostMapping("/scrap")
    @Operation(summary = "资产报废", description = "将资产标记为报废")
    @OperationLog(value = "资产报废", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:record:scrap')")
    public Result<Long> scrapAsset(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.scrapAsset(request);
        return Result.success("资产报废成功", recordId);
    }

    /**
     * 资产送修
     *
     * @param request 送修请求
     * @return 记录ID
     */
    @PostMapping("/repair")
    @Operation(summary = "资产送修", description = "将资产送去维修")
    @OperationLog(value = "资产送修", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:record:repair')")
    public Result<Long> sendForRepair(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.sendForRepair(request);
        return Result.success("资产送修成功", recordId);
    }

    /**
     * 维修完成
     *
     * @param request 维修完成请求
     * @return 记录ID
     */
    @PostMapping("/repair-complete")
    @Operation(summary = "维修完成", description = "标记资产维修完成")
    @OperationLog(value = "维修完成", type = "UPDATE")
    @PreAuthorize("hasAuthority('asset:record:repair-complete')")
    public Result<Long> repairComplete(@Validated @RequestBody RecordCreateRequest request) {
        Long recordId = recordService.repairComplete(request);
        return Result.success("维修完成", recordId);
    }

    /**
     * 分页查询流转记录
     *
     * @param query 查询条件
     * @return 流转记录分页列表
     */
    @GetMapping
    @Operation(summary = "分页查询流转记录", description = "根据条件分页查询资产流转记录")
    @PreAuthorize("hasAuthority('asset:record:list')")
    public Result<Page<RecordVO>> getRecordPage(RecordPageQuery query) {
        Page<RecordVO> page = recordService.getRecordPage(query);
        return Result.success(page);
    }

    /**
     * 查询资产流转历史
     *
     * @param assetId 资产ID
     * @return 流转记录列表
     */
    @GetMapping("/asset/{assetId}")
    @Operation(summary = "查询资产流转历史", description = "查询指定资产的完整流转历史")
    @PreAuthorize("hasAuthority('asset:record:history')")
    public Result<List<RecordVO>> getAssetRecordHistory(
            @Parameter(description = "资产ID") @PathVariable Long assetId) {
        List<RecordVO> records = recordService.getAssetRecordHistory(assetId);
        return Result.success(records);
    }
}
