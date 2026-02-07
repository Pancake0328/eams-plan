package com.eams.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.lifecycle.entity.AssetInventoryDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资产盘点明细Mapper
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetInventoryDetailMapper extends BaseMapper<AssetInventoryDetail> {

    @Insert({
            "<script>",
            "INSERT INTO asset_inventory_detail (inventory_id, asset_id, asset_number, asset_name, expected_location, actual_location, inventory_result, inventory_person, inventory_time, remark)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.inventoryId}, #{item.assetId}, #{item.assetNumber}, #{item.assetName}, #{item.expectedLocation}, #{item.actualLocation}, #{item.inventoryResult}, #{item.inventoryPerson}, #{item.inventoryTime}, #{item.remark})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<AssetInventoryDetail> list);
}
