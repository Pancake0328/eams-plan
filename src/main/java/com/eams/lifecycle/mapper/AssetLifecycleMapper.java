package com.eams.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.lifecycle.entity.AssetLifecycle;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资产生命周期Mapper
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Mapper
public interface AssetLifecycleMapper extends BaseMapper<AssetLifecycle> {

    @Insert({
            "<script>",
            "INSERT INTO asset_lifecycle (asset_id, from_department_id, from_department, to_department_id, to_department, from_custodian, to_custodian, stage, previous_stage, stage_date, reason, operator, remark, deleted)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.assetId}, #{item.fromDepartmentId}, #{item.fromDepartment}, #{item.toDepartmentId}, #{item.toDepartment}, #{item.fromCustodian}, #{item.toCustodian}, #{item.stage}, #{item.previousStage}, #{item.stageDate}, #{item.reason}, #{item.operator}, #{item.remark}, 0)",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<AssetLifecycle> list);

    @Select({
            "<script>",
            "SELECT asset_id AS assetId, stage",
            "FROM asset_lifecycle",
            "WHERE deleted = 0",
            "AND id IN (",
            "SELECT MAX(id) FROM asset_lifecycle",
            "WHERE deleted = 0 AND asset_id IN",
            "<foreach collection='assetIds' item='assetId' open='(' separator=',' close=')'>",
            "#{assetId}",
            "</foreach>",
            "GROUP BY asset_id",
            ")",
            "</script>"
    })
    List<AssetLifecycle> selectLatestStagesByAssetIds(@Param("assetIds") List<Long> assetIds);
}
