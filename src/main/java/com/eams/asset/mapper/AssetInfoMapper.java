package com.eams.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.asset.entity.AssetInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 资产信息 Mapper 接口
 *
 * @author Pancake
 * @since 2026-01-03
 */
@Mapper
public interface AssetInfoMapper extends BaseMapper<AssetInfo> {
    @Select("SELECT COUNT(1) FROM asset_info WHERE asset_number = #{assetNumber} AND deleted = 0")
    int countByAssetNumber(@Param("assetNumber") String assetNumber);

    @Insert({
            "<script>",
            "INSERT INTO asset_info (asset_number, asset_name, category_id, purchase_detail_id, purchase_amount, purchase_date, department_id, custodian, asset_status, specifications, manufacturer, remark, deleted)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.assetNumber}, #{item.assetName}, #{item.categoryId}, #{item.purchaseDetailId}, #{item.purchaseAmount}, #{item.purchaseDate}, #{item.departmentId}, #{item.custodian}, #{item.assetStatus}, #{item.specifications}, #{item.manufacturer}, #{item.remark}, 0)",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<AssetInfo> list);

    @Select({
            "<script>",
            "SELECT id, asset_number AS assetNumber",
            "FROM asset_info",
            "WHERE deleted = 0 AND asset_number IN",
            "<foreach collection='assetNumbers' item='assetNumber' open='(' separator=',' close=')'>",
            "#{assetNumber}",
            "</foreach>",
            "</script>"
    })
    List<AssetInfo> selectByAssetNumbers(@Param("assetNumbers") List<String> assetNumbers);

    @Update({
            "<script>",
            "UPDATE asset_info",
            "SET department_id = #{departmentId}, custodian = #{custodian}, asset_status = 1, remark = #{remark}, update_time = NOW()",
            "WHERE deleted = 0 AND id IN",
            "<foreach collection='assetIds' item='assetId' open='(' separator=',' close=')'>",
            "#{assetId}",
            "</foreach>",
            "</script>"
    })
    int inboundBatchUpdate(@Param("assetIds") List<Long> assetIds,
            @Param("departmentId") Long departmentId,
            @Param("custodian") String custodian,
            @Param("remark") String remark);

    @Update({
            "<script>",
            "UPDATE asset_info",
            "SET asset_status = #{assetStatus}, update_time = NOW()",
            "WHERE deleted = 0 AND id IN",
            "<foreach collection='assetIds' item='assetId' open='(' separator=',' close=')'>",
            "#{assetId}",
            "</foreach>",
            "</script>"
    })
    int updateAssetStatusByIds(@Param("assetIds") List<Long> assetIds, @Param("assetStatus") Integer assetStatus);
}
