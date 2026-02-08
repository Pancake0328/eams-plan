package com.eams.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资产编号序列实体
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Data
@TableName("asset_number_sequence")
public class AssetNumberSequence implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序列ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编号前缀
     */
    private String prefix;

    /**
     * 当前序号
     */
    private Integer currentNumber;

    /**
     * 日期部分（可选）
     */
    private String datePart;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
