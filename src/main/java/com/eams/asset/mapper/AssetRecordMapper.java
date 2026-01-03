package com.eams.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.asset.entity.AssetRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产流转记录 Mapper 接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetRecordMapper extends BaseMapper<AssetRecord> {
}
