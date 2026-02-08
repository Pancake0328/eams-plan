package com.eams.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.lifecycle.entity.AssetInventory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产盘点计划Mapper
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Mapper
public interface AssetInventoryMapper extends BaseMapper<AssetInventory> {
}
