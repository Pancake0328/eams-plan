package com.eams.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.asset.entity.AssetInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 资产信息 Mapper 接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetInfoMapper extends BaseMapper<AssetInfo> {
    @Select("SELECT COUNT(1) FROM asset_info WHERE asset_number = #{assetNumber} AND deleted = 0")
    int countByAssetNumber(@Param("assetNumber") String assetNumber);
}
