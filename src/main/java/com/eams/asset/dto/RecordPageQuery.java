package com.eams.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 流转记录分页查询条件 DTO
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Data
@Schema(description = "流转记录分页查询条件")
public class RecordPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量", example = "10")
    private Long size = 10L;

    /**
     * 资产ID
     */
    @Schema(description = "资产ID")
    private Long assetId;

    /**
     * 记录类型
     */
    @Schema(description = "记录类型：1-入库，2-分配，3-调拨，4-归还，5-报废，6-送修，7-维修完成，8-报修拒绝")
    private Integer recordType;

    /**
     * 操作人
     */
    @Schema(description = "操作人（模糊查询）")
    private String operator;
}
