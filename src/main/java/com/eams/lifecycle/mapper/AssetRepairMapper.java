package com.eams.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.lifecycle.entity.AssetRepair;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产报修记录Mapper
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetRepairMapper extends BaseMapper<AssetRepair> {
}
